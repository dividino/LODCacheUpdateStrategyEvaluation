package org.west.changealod.data.feature;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.west.changealod.file.LoadVersion;
import org.west.changealod.utility.Constants;

public class SizeFt extends Feature{
	
	/**
	 * Extract context's size of a dataset
	 * 
	 * @author Renata Dividino
	 * 
	 */

	public SizeFt(LoadVersion load){
		super(load);
	}
	
	public void extractFt(Map<String, Set<String>> hMap, String date, int type, int strategy, int bd){
		
		Set<String> cList ;
		if(strategy==0)	cList = load.loadContexts(Constants.getContextPath(date));			
		else cList = load.loadContexts(Constants.getIndexPath(date, strategy, type, bd));		
		
		Iterator<String> itCont = cList.iterator();
		while(itCont.hasNext()){
			String context = itCont.next();
			fMap.put(context, (double) load.getTriples(context, type, date, strategy, bd).size());
		}
	
	}
	
	
}
