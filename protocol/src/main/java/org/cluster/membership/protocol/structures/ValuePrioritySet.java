package org.cluster.membership.protocol.structures;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class ValuePrioritySet<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HashMap<T,T> hashed;
	private TreeSet<T> ordered;

	private SerializableComparator<T> priorityComparator;

	public ValuePrioritySet(Comparator<T> sortComparator, SerializableComparator<T> priorityComparator) {
		hashed = new HashMap<T,T>();
		ordered = new TreeSet<T>(sortComparator);
		this.priorityComparator = priorityComparator;
	}

	public ValuePrioritySet(Comparator<T> sortComparator, SerializableComparator<T> priorityComparator,
			Set<T> data) {

		ordered = new TreeSet<T>(sortComparator);
		hashed = new HashMap<T,T>();
		for(T t : data) {
			ordered.add(t);
			hashed.put(t, t);
		}		

		this.priorityComparator = priorityComparator;
	}


	public boolean add(T el, boolean updatePriority) {
		synchronized(this) {
			T present = hashed.get(el);

			if(present != null && !updatePriority) return false;

			if(present == null) {	
				hashed.put(el, el);
				ordered.add(el);
			} else if(priorityComparator.compare(el, present) < 0) {
				ordered.remove(present);
				hashed.put(el, el);
				ordered.add(el);
			} else return false;

			return true;		

		}

	}

	public void remove(T el) {
		synchronized(this) {
			T present = hashed.get(el);
			if(present == null) return;

			hashed.remove(present);
			ordered.remove(present);
		}		

	}

	public boolean contains(T el, boolean updatePriority) {
		synchronized(this) {
			boolean is = contains(el);
			if(!is) return false;

			if(updatePriority) add(el, true);		
			return true;
		}

	}

	public TreeSet<T> tailSet(T ele) {
		return (TreeSet<T>)ordered.tailSet(ele);
	}

	public TreeSet<T> headSet(T ele) {
		return (TreeSet<T>)ordered.headSet(ele);
	}

	public TreeSet<T> between(T start, T end) {
		TreeSet<T> tail = (TreeSet<T>)ordered.tailSet(start);
		return (TreeSet<T>)tail.headSet(end);
	}

	public TreeSet<T> getSet() {
		return ordered;
	}

	public Iterator<T> iterator() {
		return ordered.iterator();
	}



	public T pollFirst() {
		synchronized(this) {
			T el = ordered.pollFirst();		
			if(el == null) return null;		
			hashed.remove(el);		
			return el;
		}
	}

	public T pollLast() {
		synchronized(this) {
			T el = ordered.pollLast();		
			if(el == null) return null;		
			hashed.remove(el);		
			return el;
		}
	}

	public T last() {
		if(hashed.isEmpty()) return null;		
		return ordered.last();
	}

	public T first() {
		if(hashed.isEmpty()) return null;		
		return ordered.first();
	}

	private boolean contains(T el) { return hashed.get(el) != null; }

	public int size() { return hashed.size(); }

	public void clear() {
		synchronized(this) {
			hashed.clear();
			ordered.clear();
		}
	}

}
