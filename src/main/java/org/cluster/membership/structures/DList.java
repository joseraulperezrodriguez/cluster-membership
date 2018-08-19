package org.cluster.membership.structures;

import java.io.Serializable;
import java.util.ArrayList;

import org.cluster.membership.model.Node;
import org.cluster.membership.util.Tuple2;

public class DList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	
	private ArrayList<Node> nodes;
	
	public DList() {
		this.nodes = new ArrayList<Node>();
	}
	
	/**Assumes arguments is a sorted list*/
	public void addSortedNodes(DList dlist) {
		for(Node n : dlist.nodes)
			nodes.add(n);
	}
	
	public Node get(int index) {
		return nodes.get(index);
	}
	
	public int size() {
		return nodes.size();
	}
	
	public boolean contains(Node nd) {
		Tuple2<Integer, Integer> bs = bs(nd);		
		return (bs.getA() == bs.getB() && bs.getA() < nodes.size() && 
				this.get(bs.getA()).getId().equals(nd.getId()));
	}
	
	public void remove(Node node) {
		Tuple2<Integer, Integer> bs = bs(node);	
		if(bs.getA() != bs.getB() || bs.getA() >= nodes.size() || 
				!this.get(bs.getA()).getId().equals(node.getId())) return;
		
		synchronized(this) { 
			nodes.remove((int)bs.getA());
		}
		
	}
		
	public boolean add(Node arg0) {
		
		if(nodes.size() == 0) synchronized(this) { return nodes.add(arg0); }
		
		Tuple2<Integer, Integer> bs = bs(arg0);
		
		synchronized (this) {
			if(arg0.getId().compareTo(nodes.get(bs.getA()).getId()) < 0) 
				insertionSort(bs.getA(), arg0);
			else if(bs.getA() != bs.getB())
				insertionSort(bs.getB(), arg0);
			else
				insertionSort(bs.getB() + 1, arg0);
		}
				
		return true;
	}
	
	private void insertionSort(int index, Node node) {
		Node toAdd = node;
		Node toReplace = null;
		while(index < nodes.size()) {
			toReplace = nodes.get(index);
			nodes.set(index, toAdd);									
			toAdd = toReplace;
			index++;			
		}
		nodes.add(toAdd);
	}
	
	
	private Tuple2<Integer, Integer> bs(Node node) {
		if(nodes.size() == 0) return new Tuple2<>(0,0);
		
		int first = 0;
		int last = nodes.size() - 1;
		
		while(last - first > 1) {
			int middle = (last + first) / 2;
			Node middleNode = nodes.get(middle);
			int comp = node.getId().compareTo(middleNode.getId());
			
			if(comp < 0) last = middle;
			else if(comp > 0) first = middle;
			else return new Tuple2<>(middle, middle);			
		}
	
		if(node.compareTo(nodes.get(first)) <= 0) return new Tuple2<>(first, first);
		if(node.compareTo(nodes.get(last)) >= 0) return new Tuple2<>(last, last);
		return new Tuple2<>(first, last);
	}

	
	

}
