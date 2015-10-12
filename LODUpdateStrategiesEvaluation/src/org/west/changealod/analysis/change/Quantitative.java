package org.west.changealod.analysis.change;

import java.util.Set;

import org.apache.jena.atlas.lib.SetUtils;
import org.west.changealod.analysis.Triple;

/**
 * Percentage of changes between two sets.
 * 
 * @author Renata Dividino
 * 
 */

public class Quantitative extends Metric {
	
	public Double extract(Set<Triple> t1List, Set<Triple> t2List, double defValue) {		
		
		if(t1List.isEmpty() && t2List.isEmpty())
			return 0.0;
		
		
		double deleted_triples  = (double) SetUtils.difference(t1List, t2List).size();
		double new_triples = (double) SetUtils.difference(t2List, t1List).size();
		
		return (deleted_triples + new_triples);
	}
}
	
	
	
