package org.west.changealod.data.feature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.west.changealod.analysis.Triple;
import org.west.changealod.analysis.change.Metric;
import org.west.changealod.file.LoadVersion;
import org.west.changealod.utility.Constants;

public class ChangeFt extends Feature{
	
	/**
	 * Extract jaccard distance of two snapshots of a context
	 * 
	 * @author Renata Dividino
	 * 
	 */
	
	double defValue;
	Metric mt ;
	
	public ChangeFt(LoadVersion load, double defValue, Metric mt){
		super(load);
		this.defValue = defValue;
		this.mt = mt;
	}
	
	
	public void extractFt(Map<String, Set<String>> hMap, 
			String date, int type, int strategy, int bd){
		
		double score = 0;
		String t1 = "", t2 = "";
  		Set<Triple> t1Map = null, t2Map = null;
  		
  		Set<String> cList ;
		if(strategy==0)	cList = load.loadContexts(Constants.getContextPath(date));
		else cList = load.loadContexts(Constants.getIndexPath(date, strategy, type, bd));
		
  		List<String> dates =  new ArrayList<String>(hMap.keySet());
  		Collections.reverse(dates);
		
		Iterator<String> itCont = cList.iterator();			
		while(itCont.hasNext()){			   
			String context = itCont.next();
			score = 0;
						
			if(dates.size() > 1){
				t1 = dates.get(0);   
				t2 = dates.get(1);
				
				t1Map = load.getTriples(context, type, t1, strategy, bd);
				t2Map = load.getTriples(context, type, t2, strategy, bd);
				
				logger.info("Extracting change from " + context);
				logger.info("t1=" + t1 + ", t2= " + t2);
				
				score = mt.extract(t1Map, t2Map, defValue);
				
				logger.info("score= " + score);
									
			}else{ 
				score = defValue;
			}
			fMap.put(context, score);			
		}		
	}

}
