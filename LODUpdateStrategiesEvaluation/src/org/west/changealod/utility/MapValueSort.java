package org.west.changealod.utility;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;


public class MapValueSort {
	
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue( Map<K, V> map , int order){
		TreeMap<K,V> sorted_map;
		if (order == 0) {
    	   ValueComparatorA<K,V> bvc =  new MapValueSort.ValueComparatorA<K,V>(map);
    	   sorted_map = new TreeMap<K,V>(bvc);
       }
       else {
    	   ValueComparatorD<K,V> bvc =  new MapValueSort.ValueComparatorD<K,V>(map);
    	   sorted_map = new TreeMap<K,V>(bvc);
       }
       
        sorted_map.putAll(map);
        return sorted_map;
    }
	/** inner class to do soring of the map **/
	public static class ValueComparatorD<K, V extends Comparable<? super V>> implements Comparator<K> {

	    Map<K, V> base;
	    public ValueComparatorD(Map<K, V> base) {
	        this.base = base;
	    }

	    public int compare(K a, K b) {
	        int result = (base.get(b).compareTo(base.get(a)));
	        if (result == 0) result =1;
	        // returning 0 would merge keys
	        return result;
	    }
	}
	
	public static class ValueComparatorA<K, V extends Comparable<? super V>> implements Comparator<K> {

	    Map<K, V> base;
	    public ValueComparatorA(Map<K, V> base) {
	        this.base = base;
	    }

	    public int compare(K a, K b) {
	        int result = (base.get(a).compareTo(base.get(b)));
	        if (result == 0) result=1;
	        // returning 0 would merge keys
	        return result;
	    }
	}
}