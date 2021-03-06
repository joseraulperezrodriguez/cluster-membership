package org.cluster.membership.protocol.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.cluster.membership.common.debug.StateInfo;
import org.cluster.membership.common.model.Node;
import org.cluster.membership.common.model.util.MathOp;
import org.cluster.membership.protocol.ClusterNodeEntry;
import org.cluster.membership.protocol.Config;
import org.cluster.membership.protocol.model.ClusterData;
import org.cluster.membership.protocol.model.FrameMessageCount;
import org.cluster.membership.protocol.model.Message;
import org.cluster.membership.protocol.model.SynchroObject;
import org.cluster.membership.protocol.structures.ValuePrioritySet;
import org.cluster.membership.protocol.structures.DList;
import org.cluster.membership.protocol.structures.ValuePriorityEntry;
import org.cluster.membership.protocol.time.ServerTime;
import org.cluster.membership.protocol.util.MessageComparators;
import org.cluster.membership.protocol.util.ValuePriorityEntryComparators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**This class holds the view of the cluster from the perspective of the node is running the code 
 * */
@Component
public class ClusterView implements Serializable {

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(ClusterView.class.getName());

	private Config config;

	private FrameMessageCount frameMessCount;

	/**All nodes available until now in the cluster, including the failing ones and marked for dead*/
	private DList nodes;

	/**Holds the nodes marked for deletion after Config.FAILING_NODE_EXPIRATION_TIME_MS  milliseconds*/
	private ValuePrioritySet<ValuePriorityEntry<Node, Long>> suspectingNodesTimeout;

	/**Node failing but not marked as suspected (suspectingNodesTimeout), waiting for a second chance after Config.RETRY_FAILED_TIME_MS */
	private ValuePrioritySet<ValuePriorityEntry<Node, Long>> failed;

	/**Gossip messages that have not completed all the iterations*/
	private ValuePrioritySet<Message> rumorsToSend;

	private ValuePrioritySet<Message> receivedRumors;
	
	public ClusterView(DList nodes, 
			ValuePrioritySet<ValuePriorityEntry<Node, Long>> suspectingNodesTimeout,
			ValuePrioritySet<ValuePriorityEntry<Node, Long>> failed, 
			ValuePrioritySet<Message> rumorsToSend, 
			ValuePrioritySet<Message> receivedRumors) {
		super();
		this.nodes = nodes;
		this.suspectingNodesTimeout = suspectingNodesTimeout;
		this.failed = failed;
		this.rumorsToSend = rumorsToSend;
		this.receivedRumors = receivedRumors;
	}

	@Autowired
	public ClusterView(Config config) {
		super();
		long nowUTC = ServerTime.getTime();
		frameMessCount = new FrameMessageCount(nowUTC,nowUTC,0);
		this.config = config;
		this.nodes = new DList();
		this.suspectingNodesTimeout = new ValuePrioritySet<>(ValuePriorityEntryComparators.<Node, Long>ascComparator(),
				ValuePriorityEntryComparators.<Node, Long>ascPriorityComparator());
		this.rumorsToSend = new ValuePrioritySet<>(MessageComparators.getIterationsDescComparator(), 
				MessageComparators.getIteratorPriorityAscComparator());
		this.failed = new ValuePrioritySet<>(ValuePriorityEntryComparators.<Node, Long>ascComparator(),
				ValuePriorityEntryComparators.<Node, Long>ascPriorityComparator());
		this.receivedRumors = new ValuePrioritySet<Message>(MessageComparators.getGeneratedTimeAscComparator(),
				MessageComparators.getGeneratedTimePriorityAscComparator());
	}

	public List<Node> nodes() { return nodes.list(); }

	public FrameMessageCount getFrameMessageCount() { return frameMessCount; }

	public StateInfo getStateInfo() {
		Set<String> nodesId = new HashSet<String>();
		for(Node n : nodes.list()) nodesId.add(n.getId());		

		Iterator<ValuePriorityEntry<Node, Long>> iteratorSuspecting = suspectingNodesTimeout.iterator();		
		Set<String> suspectingNodes = new HashSet<String>();		
		while(iteratorSuspecting.hasNext()) suspectingNodes.add(iteratorSuspecting.next().getKey().getId());

		Iterator<ValuePriorityEntry<Node, Long>> iteratorFailing = failed.iterator();		
		Set<String> failingNodes = new HashSet<String>();		
		while(iteratorFailing.hasNext()) failingNodes.add(iteratorFailing.next().getKey().getId());

		StateInfo si = new StateInfo(nodesId, suspectingNodes, failingNodes);
		return si;
	}

	public void init() {
		nodes.add(config.getThisPeer());
		logger.info("initialized node, added this peer to node list");
	}

	public void unsubscribe() {
		if(getClusterSize() > 1) {
			Message uns = new Message(MessageType.UNSUBSCRIPTION, config.getThisPeer(), 1);
			rumorsToSend.add(uns, true);
		} else Global.shutdown(ClusterNodeEntry.applicationContexts.get(config.getThisPeer()), 5);		
	}

	public void subscribe(Node node) {
		logger.info("subscribe received from node: " + node);
		Message add = new Message(MessageType.ADD_TO_CLUSTER, node, MathOp.log2n(getClusterSize()));
		addToCluster(add);
	}

	public SynchroObject getSyncObject(Node node, FrameMessageCount frameMessCount) {

		if(isSuspectedDead(node)) {
			Message keepAliveMessage = new Message(MessageType.KEEP_ALIVE, node, MathOp.log2n(getClusterSize()));
			keepAlive(keepAliveMessage);
		}

		if(isFailing(node)) removeFailing(node);

		SynchroObject result = getUpdatedView(frameMessCount, node);
		return result;		
	}

	public void updateFrameMessageCount() {
		long nowUTC = ServerTime.getTime();
		
		long expectedIterations = (long)MathOp.log2n(getClusterSize());		
		long timeFrame = expectedIterations * config.getIterationIntervalMs() * config.getReadIddleIterationsFactor();		
		long startFrame = nowUTC - timeFrame;
		long endFrame = startFrame + (expectedIterations * config.getIterationIntervalMs() * (config.getReadIddleIterationsFactor() - 1));

		TreeSet<Message> tail = receivedRumors.tailSet(MessageComparators.getMinTimeTemplate(startFrame));
		int countTail = tail.tailSet(MessageComparators.getMinTimeTemplate(endFrame + 1)).size();

		frameMessCount = new FrameMessageCount(startFrame, endFrame, tail.size() - countTail);
	}

	private SynchroObject getUpdatedView(FrameMessageCount frameMessCount, Node node) {
		Message first = receivedRumors.first();

		if(first != null && first.getGeneratedTime() > frameMessCount.getStartTime()) {
			return new SynchroObject(myView(node));
		} else {

			TreeSet<Message> frame = receivedRumors.between(
					MessageComparators.getMinTimeTemplate(frameMessCount.getStartTime()),
					MessageComparators.getMaxTimeTemplate(frameMessCount.getEndTime()));

			ArrayList<Message> missingMessages = new ArrayList<>();
			if(frame.size() > frameMessCount.getCount()) {
				Iterator<Message> iterator = frame.iterator();
				while(iterator.hasNext()) missingMessages.add(iterator.next());
			}

			return new SynchroObject(missingMessages);
		}
	}

	public synchronized void removeExpired() {
		long nowUTC = ServerTime.getTime();
				
		ValuePriorityEntry<Node, Long> entry = null;
		while(suspectingNodesTimeout.size() > 0 && (entry = suspectingNodesTimeout.first()).getValue() >= nowUTC) { 
			nodes.remove(entry.getKey());
			suspectingNodesTimeout.remove(entry);
			failed.remove(entry);
			suspectingNodesTimeout.pollFirst();
			logger.info("Removed node: " + entry.getKey() + " due to suspect time expiration: " + entry.getValue());
		}
	}

	/**START the synchronization part
	 * *******************************
	 * *******************************
	 * *******************************/

	/**
	 * This method calls remove the message with pending iterations<=1 and add to result all the message with pending iterations>=1
	 */
	public List<Message> getPendingRumors() {
		List<Message> ans = new ArrayList<Message>();
		Message current = null;
		while((current = rumorsToSend.last()) != null && current.remainingIterations() <= 1) {
			current = rumorsToSend.pollLast();
			if(current.remainingIterations() == 1) ans.add(current.sent());
		}

		Iterator<Message> iterator = rumorsToSend.iterator();
		while(iterator.hasNext()) ans.add(iterator.next().sent());
		return ans;
	}

	public void keepAlive(Message keepAlive) {
		suspectingNodesTimeout.remove(ValuePriorityEntryComparators.<Node, Long>getKeyTemplate(keepAlive.getNode()));
		addRumorsToSend(keepAlive); 
		logger.info("keep alive message: " + keepAlive);
	}

	public void addToCluster(Message ms) { 
		nodes.add(ms.getNode());
		addRumor(ms);
		logger.info("add to cluster message: " + ms);
	}

	public void removeFromCluster(Message ms) { 
		nodes.remove(ms.getNode());

		ValuePriorityEntry<Node, Long> value = ValuePriorityEntryComparators.<Node, Long>getKeyTemplate(ms.getNode());

		suspectingNodesTimeout.remove(value);
		failed.remove(value);
		addRumor(ms);

		logger.info("remove from cluster message: " + ms);
	}

	public void removeFailing(Node nd) { 
		ValuePriorityEntry<Node, Long> value = ValuePriorityEntryComparators.<Node, Long>getKeyTemplate(nd);		
		failed.remove(value); 
	}

	public void suspect(Long expirationTime, Message sm) {
		ValuePriorityEntry<Node, Long> value = new ValuePriorityEntry<Node, Long>(sm.getNode(), expirationTime);

		suspectingNodesTimeout.add(value, true);
		failed.remove(value);
		addRumor(sm);

		logger.info("add suspecting message: " + sm);
	}

	public void addFailed(ValuePriorityEntry<Node, Long> vp) { 
		this.failed.add(vp, true);
		logger.info("add failed node: " + vp.getKey());
	}

	private void addRumorsToSend(Message m) {
		if(m.hasNextIteration()) rumorsToSend.add(m, true);
	}

	public synchronized void addRumor(Message m) { 
		addRumorsToSend(m);
		this.receivedRumors.add(m, true);
		if(this.receivedRumors.size() > config.getMaxRumorsLogSize()) this.receivedRumors.pollFirst();
		logger.info("added rumor: " + m);
	}

	public synchronized void updateMyView(SynchroObject syncObjectWrapper) {
		assert(syncObjectWrapper.getClusterData() != null ^ syncObjectWrapper.getMessages() != null);

		if(syncObjectWrapper.getClusterData() != null) updateMyViewFully(syncObjectWrapper.getClusterData());
		else synchronizeMyView(syncObjectWrapper.getMessages());

	}

	private synchronized void updateMyViewFully(ClusterData clusterView) {
		this.failed.clear();
		this.receivedRumors.clear();
		this.nodes = clusterView.getNodes();
		this.rumorsToSend = new ValuePrioritySet<>(MessageComparators.getIterationsDescComparator(), 
				MessageComparators.getIteratorPriorityAscComparator(), clusterView.getRumorsToSend());
		this.suspectingNodesTimeout = new ValuePrioritySet<>(ValuePriorityEntryComparators.<Node, Long>ascComparator(),
				ValuePriorityEntryComparators.<Node, Long>ascPriorityComparator(), clusterView.getSuspectingNodesTimeout());

		this.nodes.add(config.getThisPeer());

		logger.info("update my own view: " + clusterView);
	}

	private void synchronizeMyView(List<Message> missingMessages) {
		Set<Node> removingNodes = new TreeSet<Node>();
		for(Message m : missingMessages) {
			receivedRumors.add(m, true);
			if(m.getType().equals(MessageType.ADD_TO_CLUSTER)) nodes.add(m.getNode());
			else if(m.getType().equals(MessageType.REMOVE_FROM_CLUSTER)) {
				nodes.remove(m.getNode());
				ValuePriorityEntry<Node, Long> value = ValuePriorityEntryComparators.<Node, Long>getKeyTemplate(m.getNode());
				suspectingNodesTimeout.remove(value);
				failed.remove(value);
			}
			else if(m.getType().equals(MessageType.SUSPECT_DEAD)) {
				Long expirationTime = (Long)m.getData();
				if(expirationTime >= ServerTime.getTime())
					suspectingNodesTimeout.add(new ValuePriorityEntry<Node, Long>(m.getNode(), expirationTime), true);
				else removingNodes.add(m.getNode());
			}
			else if(m.getType().equals(MessageType.KEEP_ALIVE)) {
				ValuePriorityEntry<Node, Long> entry = ValuePriorityEntryComparators.<Node, Long>getKeyTemplate(m.getNode());

				failed.remove(entry);
				suspectingNodesTimeout.remove(entry);
				removingNodes.remove(m.getNode());
			}
		}
		for(Node n: removingNodes) {
			ValuePriorityEntry<Node, Long> nd = ValuePriorityEntryComparators.<Node, Long>getKeyTemplate(n);
			nodes.remove(n);
			failed.remove(nd);
			suspectingNodesTimeout.remove(nd);			
		}
		logger.info("synchronize my own view: " + missingMessages.size() + " messages missing");

	}
	/**END the synchronization part
	 * *******************************
	 * *******************************
	 * *******************************/


	public ValuePriorityEntry<Node, Long> pollFailed() { return failed.pollLast(); }

	public boolean isSuspectedDead(Node nd) {  return suspectingNodesTimeout.contains(ValuePriorityEntryComparators.<Node, Long>getKeyTemplate(nd), true); }

	public boolean isFailing(Node nd) { return failed.contains(ValuePriorityEntryComparators.<Node, Long>getKeyTemplate(nd), true); }

	public boolean containsRumor(Message m) { return rumorsToSend.contains(m, true); }

	public int getClusterSize() { return nodes.size(); }

	public int getSuspectedSize() { return suspectingNodesTimeout.size(); }

	public int getFailedSize() { return failed.size(); }

	public Node getNodeAt(int index) throws IndexOutOfBoundsException { return nodes.get(index); }

	/**Prepare the ClusterView object, for sending it to a node joining the cluster*/
	public ClusterData myView(Node otherPeer) {
		ClusterData clusterData = new ClusterData(nodes, rumorsToSend.getSet(), suspectingNodesTimeout.getSet());
		return clusterData;
	}

	@Override
	public String toString() {
		return "nodes: " + nodes.size() + " suspecting: " + suspectingNodesTimeout.size();
	}

}
