package org.cluster.membership.protocol.structures;

import java.io.Serializable;
import java.util.Comparator;

import org.cluster.membership.protocol.util.ValuePriorityEntryComparators;

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
		this.comparator = ValuePriorityEntryComparators.<K,V>ascComparator();
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
	
}
