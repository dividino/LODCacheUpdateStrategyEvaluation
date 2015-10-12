package org.west.changealod.data.feature;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.west.changealod.file.LoadVersion;


/**
 * Features to be used for ranking contexts
 * 
 * @author Renata Dividino
 * 
 */


public abstract class Feature {
	
	static Logger logger = Logger.getLogger(Feature.class);
	
	Map<String, Double> fMap;
	LoadVersion load;	
	
	protected Feature(LoadVersion load){
		this.load = load;		
		fMap = new TreeMap<String, Double>();
	}
	
	abstract public void extractFt(Map<String, Set<String>> hMap, String date, int type, int strategy, int bd);
	
	
	public Map<String, Double> getfMap() {
		return fMap;
	}
	
}