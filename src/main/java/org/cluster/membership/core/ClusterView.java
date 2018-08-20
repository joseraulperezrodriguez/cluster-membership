package org.cluster.membership.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.cluster.membership.Config;
import org.cluster.membership.model.Message;
import org.cluster.membership.model.Node;
import org.cluster.membership.structures.DList;
import org.cluster.membership.structures.ValuePriorityEntry;
import org.cluster.membership.structures.ValuePrioritySet;
import org.cluster.membership.util.DateTime;


/**This class holds the view of the cluster from the perspective of the node is running the code 
 * */
public class ClusterView implements Serializable {

	private static final long serialVersionUID = 1L;

	/**All nodes available until now in the cluster, including the failing ones and marked for dead*/
	private DList nodes;

	/**Holds the nodes marked for deletion after Config.FAILING_NODE_EXPIRATION_TIME_MS  milliseconds*/
	private ValuePrioritySet<ValuePriorityEntry<Node, Long>> suspectingNodesTimeout;

	/**Node failing but not marked as suspected (suspectingNodesTimeout), waiting for a second chance after Config.RETRY_FAILED_TIME_MS */
	private ValuePrioritySet<ValuePriorityEntry<Node, Long>> failed;

	/**Gossip messages that have not completed all the iterations*/
	//private ValuePriorityEntrySet<Message, Integer> rumorsToSend;
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

	public ClusterView() {
		super();
		this.nodes = new DList();
		this.suspectingNodesTimeout = new ValuePrioritySet<>(ValuePriorityEntry.<Node, Long>ascComparator(),
				ValuePriorityEntry.<Node, Long>ascPriorityComparator());
		this.rumorsToSend = new ValuePrioritySet<>(Message.getIterationsDescComparator(), 
				Message.getIteratorPriorityAscComparator());		
		this.failed = new ValuePrioritySet<>(ValuePriorityEntry.<Node, Long>ascComparator(),
				ValuePriorityEntry.<Node, Long>ascPriorityComparator());		
		this.receivedRumors = new ValuePrioritySet<Message>(Message.getGeneratedTimeAscComparator(),
				Message.getGeneratedTimePriorityAscComparator());
	}



	public Long lastRumorTime() {
		Message last = receivedRumors.last();
		return ((last == null) ? System.currentTimeMillis() : last.getGeneratedTime()); 
	}

	public Object getUpdatedView(long firstTime, Node node) {
		Message first = receivedRumors.last();

		if(first == null || first.getGeneratedTime() > firstTime) {
			return yourView(node);
		} else {
			List<Message> missingMessages = new ArrayList<>();
			Iterator<Message> iterator = receivedRumors.tailSet(Message.getMinTimeTemplate(firstTime)).iterator();

			while(iterator.hasNext()) missingMessages.add(iterator.next());

			return missingMessages;
		}

	}

	/**START the synchronization part
	 * *******************************
	 * *******************************
	 * *******************************/

	public List<Message> getPendingRumors() {	
		List<Message> ans = new ArrayList<Message>();
		
		Message current = null;
		while((current = rumorsToSend.last()) != null && current.getIterations() <= 1) {
			current = rumorsToSend.pollLast();
			if(current.getIterations() == 0) continue;
			ans.add(current.sended());
		}
		
		Iterator<Message> iterator = rumorsToSend.iterator();
		while(iterator.hasNext()) ans.add(iterator.next().sended());
		return ans;		
	}

	public void keepAlive(Message keepAlive) {		
		suspectingNodesTimeout.remove(ValuePriorityEntry.<Node, Long>getKeyTemplate(keepAlive.getNode()));
		addRumorsToSend(keepAlive); 
	}

	public void addToCluster(Message ms) { 
		nodes.add(ms.getNode());
		addRumor(ms);
	}

	public void removeFromCluster(Message ms) { 
		nodes.remove(ms.getNode());

		ValuePriorityEntry<Node, Long> value = ValuePriorityEntry.<Node, Long>getKeyTemplate(ms.getNode());

		suspectingNodesTimeout.remove(value);
		failed.remove(value);
		addRumor(ms);
	}

	public void removeFailing(Node nd) { 
		ValuePriorityEntry<Node, Long> value = ValuePriorityEntry.<Node, Long>getKeyTemplate(nd);		
		failed.remove(value); 
	}

	public void suspect(Long expirationTime, Message sm) {
		ValuePriorityEntry<Node, Long> value = new ValuePriorityEntry<Node, Long>(sm.getNode(), expirationTime);

		suspectingNodesTimeout.add(value, true);
		failed.remove(value);
		addRumor(sm);
	}

	public void addFailed(ValuePriorityEntry<Node, Long> vp) { this.failed.add(vp, true); }

	private void addRumorsToSend(Message m) {
		if(m.getIterations() > 0) rumorsToSend.add(m, true);
	}

	public void addRumor(Message m) { 
		addRumorsToSend(m);
		this.receivedRumors.add(m, true);
		if(this.receivedRumors.size() > Config.MAX_RUMORS_LOG_SIZE) this.receivedRumors.pollFirst();		
	}

	public void updateMyView(ClusterView clusterView) {
		this.failed = clusterView.failed;
		this.nodes = clusterView.nodes;
		this.rumorsToSend = clusterView.rumorsToSend;
		this.suspectingNodesTimeout = clusterView.suspectingNodesTimeout;		
	}

	public void updateMyView(List<Message> missingMessages) {
		Set<Node> removingNodes = new TreeSet<Node>();
		for(Message m : missingMessages) {
			if(m.getType().equals(MessageType.ADD_TO_CLUSTER)) nodes.add(m.getNode());
			else if(m.getType().equals(MessageType.REMOVE_FROM_CLUSTER)) nodes.remove(m.getNode());
			else if(m.getType().equals(MessageType.SUSPECT_DEAD)) {
				Long expirationTime = (Long)m.getData();
				Long expirationTimeThisServer = DateTime.localTime(expirationTime, m.getGeneratedTimeZone(), Config.THIS_PEER.getTimeZone());

				if(expirationTimeThisServer < System.currentTimeMillis()) 
					suspectingNodesTimeout.add(new ValuePriorityEntry<Node, Long>(m.getNode(), expirationTimeThisServer), true);
				else removingNodes.add(m.getNode());				
			} 
			else if(m.getType().equals(MessageType.KEEP_ALIVE)) {
				ValuePriorityEntry<Node, Long> entry = ValuePriorityEntry.<Node, Long>getKeyTemplate(m.getNode());

				failed.remove(entry);
				suspectingNodesTimeout.remove(entry);
				removingNodes.remove(m.getNode());
			} 
		}



		for(Node n: removingNodes) {
			ValuePriorityEntry<Node, Long> nd = ValuePriorityEntry.<Node, Long>getKeyTemplate(n);
			nodes.remove(n);
			failed.remove(nd);
			suspectingNodesTimeout.remove(nd);			
		}
	}
	/**END the synchronization part
	 * *******************************
	 * *******************************
	 * *******************************/


	public ValuePriorityEntry<Node, Long> pollFailed() { return failed.pollFirst(); }

	public boolean isSuspectedDead(Node nd) { 
		return suspectingNodesTimeout.contains(ValuePriorityEntry.<Node, Long>getKeyTemplate(nd), true); 
	}

	public boolean isFailing(Node nd) { 
		return failed.contains(ValuePriorityEntry.<Node, Long>getKeyTemplate(nd), true); 
	}

	public boolean isRumor(Message m) { return rumorsToSend.contains(m, true); }

	public int getClusterSize() { return nodes.size() + 1; }

	public int getSuspectedSize() { return suspectingNodesTimeout.size(); }

	public int getFailedSize() { return failed.size(); }

	public Node getNodeAt(int index) throws IndexOutOfBoundsException { return nodes.get(index); }

	/**Prepare the ClusterView object, for sending it to a node joining the cluster*/
	public ClusterView yourView(Node otherPeer) {
		
		ClusterView clusterView = new ClusterView();
		DList otherNodes = new DList();
		otherNodes.addSortedNodes(nodes);
		otherNodes.add(Config.THIS_PEER);

		clusterView.nodes = otherNodes;
		clusterView.rumorsToSend = this.rumorsToSend;

		clusterView.failed = new ValuePrioritySet<>(ValuePriorityEntry.<Node, Long>ascComparator(),
				ValuePriorityEntry.<Node, Long>ascPriorityComparator());

		clusterView.suspectingNodesTimeout = new ValuePrioritySet<ValuePriorityEntry<Node, Long>>(ValuePriorityEntry.<Node, Long>ascComparator(),
				ValuePriorityEntry.<Node, Long>ascPriorityComparator());

		Iterator<ValuePriorityEntry<Node, Long>> iterator = this.suspectingNodesTimeout.iterator();

		while(iterator.hasNext()) {
			ValuePriorityEntry<Node, Long> next = iterator.next();			
			long convertedTime = DateTime.localTime(next.getValue(), Config.THIS_PEER.getTimeZone(), otherPeer.getTimeZone());

			ValuePriorityEntry<Node, Long> updated = new ValuePriorityEntry<>(next.getKey(), convertedTime);
			clusterView.suspectingNodesTimeout.add(updated, true);
		}

		return clusterView;
	}

	public static void main(String[] args) {
		System.out.println((int)'0');
		System.out.println((int)'z');
		System.out.println((int)' ');
	}



}
