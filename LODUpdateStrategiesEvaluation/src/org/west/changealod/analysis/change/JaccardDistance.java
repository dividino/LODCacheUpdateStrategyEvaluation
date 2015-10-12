package org.west.changealod.analysis.change;

import java.util.Set;


import com.google.common.collect.Sets;

//import org.apache.jena.atlas.lib.SetUtils;
import org.west.changealod.analysis.Triple;

/**
 * Jaccard distance of two sets.
 * 
 * @author Renata Dividino
 * 
 */

public class JaccardDistance extends Metric {
	
	/* 1 - intersection/union*/
	public Double extract(Set<Triple> t1List, Set<Triple> t2List, double defValue) {		
		if(t1List.isEmpty() && t2List.isEmpty())
			return 0.0;
		if(t1List.isEmpty() && !t2List.isEmpty())
			return defValue;
		if(!t1List.isEmpty() && t2List.isEmpty())
			return defValue;
		
		 		
		
		double unchanged_triples = Sets.intersection(t1List, t2List).size();
		double all_triples = Sets.union(t1List, t2List).size();
		
		double jaccard_coeficient =  unchanged_triples/all_triples;
		
		return (1 - jaccard_coeficient);
		
	}
}
	
	
	
