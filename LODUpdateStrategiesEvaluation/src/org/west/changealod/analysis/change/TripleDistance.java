package org.west.changealod.analysis.change;

import java.util.Set;


import com.google.common.collect.Sets;

//import org.apache.jena.atlas.lib.SetUtils;
import org.west.changealod.analysis.Triple;

/**
 * Triple distance of two sets.
 * 
 * @author Renata Dividino
 * 
 */

public class TripleDistance extends Metric {
	
	/* 1 - (2|common triple| / total triple in s1 + total triples s2*/
	
	public Double extract(Set<Triple> t1List, Set<Triple> t2List, double defValue) {		
		if(t1List.isEmpty() && t2List.isEmpty())
			return 0.0;
		if(t1List.isEmpty() && !t2List.isEmpty())
			return defValue;
		if(!t1List.isEmpty() && t2List.isEmpty())
			return defValue;
		
		double unchanged_triples = Sets.intersection(t1List, t2List).size();
		double total = t1List.size() + t2List.size();
		
		double triple_coeficient = 2 * (unchanged_triples/ total);
		
		return (1 - triple_coeficient);
	}
}
	
	
	
