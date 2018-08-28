package org.cluster.membership.protocol.structures;

import java.io.Serializable;
import java.util.Comparator;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ValuePriorityEntry<K extends Comparable<K>,V extends Comparable<V>> implements Comparable<ValuePriorityEntry<K,V>>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private K key;
	private V value;
	
	@JsonIgnore
	private Comparator<ValuePriorityEntry<K,V>> comparator;
	
	public ValuePriorityEntry(K key, V value) {		
		this.key = key;
		this.value = value;
		this.comparator = ValuePriorityEntry.<K,V>ascComparator();
	}
	
	public ValuePriorityEntry() {}
	
	public K getKey() {
		return key;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

	@Override
	public int compareTo(ValuePriorityEntry<K,V> arg0) {
		return comparator.compare(this,  arg0);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ValuePriorityEntry)) return false;
		try {
			@SuppressWarnings("unchecked")
			ValuePriorityEntry<K,V> sn = (ValuePriorityEntry<K,V>)obj;			
			return key.equals(sn.getKey());
			
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return key.hashCode();
	}

	@Override
	public String toString() {
		return this.key + " -> " + this.value;
	}
	
	private static <K extends Comparable<K>,V extends Comparable<V>> int compare(ValuePriorityEntry<K,V> first, ValuePriorityEntry<K,V> second) {
		int keyComparison = first.key.compareTo(second.key);		
		if(keyComparison == 0) return 0;
		
		int valueComparison = (first.value != null && second.value != null) ? first.value.compareTo(second.value) : 0;
		if(valueComparison < 0) return -1;
		else if(valueComparison > 0) return 1;
		else return 0;		
	}
	
	private static <K extends Comparable<K>,V extends Comparable<V>> int priorityCompare(ValuePriorityEntry<K,V> first, ValuePriorityEntry<K,V> second) {		
		int valueComparison = (first.value != null && second.value != null) ? first.value.compareTo(second.value) : 0;
		if(valueComparison < 0) return -1;
		else if(valueComparison > 0) return 1;
		else return 0;		
	}
	
	public static <K extends Comparable<K>,V extends Comparable<V>> SerializableComparator<ValuePriorityEntry<K,V>> ascComparator () {						
		return (a,b) -> {
			int comp = ValuePriorityEntry.<K,V>compare(a, b);
			return (comp == 0 ? a.key.compareTo(b.key) : comp);
		};
	};
	
	public static <K extends Comparable<K>,V extends Comparable<V>> SerializableComparator<ValuePriorityEntry<K,V>> ascPriorityComparator () {						
		return (a,b) -> { 
			int comp = ValuePriorityEntry.<K,V>priorityCompare(a, b);
			return (comp == 0 ? a.key.compareTo(b.key) : comp);		
		};
	};
	
	public static <K extends Comparable<K>,V extends Comparable<V>> SerializableComparator<ValuePriorityEntry<K,V>> descComparator () {						
		return (a,b) -> {
			int comp = ValuePriorityEntry.<K,V>compare(b, a);
			return (comp == 0 ? a.key.compareTo(b.key) : comp);
		};
	};
	
	public static <K extends Comparable<K>,V extends Comparable<V>> SerializableComparator<ValuePriorityEntry<K,V>> descPriorityComparator () {						
		return (a,b) -> {
			int comp = ValuePriorityEntry.<K,V>priorityCompare(b, a);
			return (comp == 0 ? a.key.compareTo(b.key) : comp);
		};
	};
	
	public static <K extends Comparable<K>,V extends Comparable<V>> ValuePriorityEntry<K,V> getKeyTemplate(K k) {
		return new ValuePriorityEntry<K,V>(k,null);
	}
	
	
}
