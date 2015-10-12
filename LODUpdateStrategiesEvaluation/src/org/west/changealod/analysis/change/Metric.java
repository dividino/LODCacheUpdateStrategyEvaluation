package org.west.changealod.analysis.change;

import java.util.Set;

import org.west.changealod.analysis.Triple;

public abstract class Metric {

	
	public abstract Double extract(Set<Triple> t1List, Set<Triple> t2List, double defValue);
}
