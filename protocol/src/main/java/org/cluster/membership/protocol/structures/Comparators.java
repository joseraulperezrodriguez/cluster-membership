package org.cluster.membership.protocol.structures;

public class Comparators {

	private static <K extends Comparable<K>,V extends Comparable<V>> int compare(ValuePriorityEntry<K,V> first, ValuePriorityEntry<K,V> second) {
		int keyComparison = first.getKey().compareTo(second.getKey());		
		if(keyComparison == 0) return 0;
		
		int valueComparison = (first.getValue() != null && second.getValue() != null) ? first.getValue().compareTo(second.getValue()) : 0;
		if(valueComparison < 0) return -1;
		else if(valueComparison > 0) return 1;
		else return 0;		
	}
	
	private static <K extends Comparable<K>,V extends Comparable<V>> int priorityCompare(ValuePriorityEntry<K,V> first, ValuePriorityEntry<K,V> second) {		
		int valueComparison = (first.getValue() != null && second.getValue() != null) ? first.getValue().compareTo(second.getValue()) : 0;
		if(valueComparison < 0) return -1;
		else if(valueComparison > 0) return 1;
		else return 0;
	}
	
	public static <K extends Comparable<K>,V extends Comparable<V>> SerializableComparator<ValuePriorityEntry<K,V>> ascComparator () {
		return (a,b) -> {
			int comp = compare(a, b);
			return (comp == 0 ? a.getKey().compareTo(b.getKey()) : comp);
		};
	};
	
	public static <K extends Comparable<K>,V extends Comparable<V>> SerializableComparator<ValuePriorityEntry<K,V>> ascPriorityComparator () {
		return (a,b) -> { 
			int comp = priorityCompare(a, b);
			return (comp == 0 ? a.getKey().compareTo(b.getKey()) : comp);
		};
	};
	
	public static <K extends Comparable<K>,V extends Comparable<V>> SerializableComparator<ValuePriorityEntry<K,V>> descComparator () {
		return (a,b) -> {
			int comp = compare(b, a);
			return (comp == 0 ? a.getKey().compareTo(b.getKey()) : comp);
		};
	};
	
	public static <K extends Comparable<K>,V extends Comparable<V>> SerializableComparator<ValuePriorityEntry<K,V>> descPriorityComparator () {
		return (a,b) -> {
			int comp = priorityCompare(b, a);
			return (comp == 0 ? a.getKey().compareTo(b.getKey()) : comp);
		};
	};
	
	public static <K extends Comparable<K>,V extends Comparable<V>> ValuePriorityEntry<K,V> getKeyTemplate(K k) {
		return new ValuePriorityEntry<K,V>(k,null);
	}
	
}
