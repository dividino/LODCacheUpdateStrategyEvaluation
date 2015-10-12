package org.west.changealod.data.feature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.west.changealod.analysis.Triple;
import org.west.changealod.analysis.change.Metric;
import org.west.changealod.file.LoadVersion;
import org.west.changealod.utility.Constants;

/**
 * Extract context's Dynamics
 * 
 * @author Renata Dividino
 * 
 */


public class DynamicsFt extends Feature{
	
	double defValue;
	int versions;
	Metric mt;
	
	public DynamicsFt(LoadVersion load, int versions, double defValue, Metric mt){
		super(load);
		this.versions = versions;		
		this.defValue = defValue;
		this.mt = mt;
	}
	
	
	public void extractFt(Map<String, Set<String>> hMap, 
			String date, int type, int strategy, int bd) {
		logger.info("Extracting dynamics");
		
		
		double score;
		String t1 = "", t2 = "";			
		Set<Triple> t1Map = null, t2Map = null;
		
		Set<String> cList ;
		if(strategy==0)	cList = load.loadContexts(Constants.getContextPath(date));
		else cList = load.loadContexts(Constants.getIndexPath(date, strategy, type, bd));
		
		
  		List<String> dates =  new ArrayList<String>(hMap.keySet());
  		Collections.sort(dates);  		
 		
 		Iterator<String> itCont = cList.iterator();		
		while(itCont.hasNext()){
			String context = itCont.next();			
			score = 0;	
			
			int count = 0;
			Iterator<String> itDate = dates.iterator();
			if(itDate.hasNext()){
				t1 = itDate.next();							
				t1Map = load.getTriples(context, type, t1, strategy, bd);
				
				count++;
				while(itDate.hasNext() && count < versions ){
					t2 = itDate.next();
					t2Map = load.getTriples(context,type, t2,strategy, bd);
					score = score + mt.extract(t1Map, t2Map, defValue);
				 
					t1 = t2;
					t1Map = new TreeSet<Triple>(t2Map);
					count ++;
				}
			}
			if(count < versions ){
				double value = (versions - count) * defValue;
				score = score + value;
			}							
			fMap.put(context, score);			
		}
		logger.info("Dynamics done");
	}	
	
}
