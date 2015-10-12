package org.west.changealod.data;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.west.changealod.file.LoadVersion;
import org.west.changealod.file.SaveAndLoad;
import org.west.changealod.utility.Constants;


/**
 * Last-update list manager 
 * 
 * @author Renata Dividino
 * 
 */


// hMap - date to contexts

public class History {
		static Logger logger =Logger.getLogger(History.class);
	
	
		
	public static void saveHistory(Map<String, Set<String>> hMap, String date, int strategy, int feature, int bd){ 
		try {
			SaveAndLoad.saveHistory(hMap,Constants.getHistoryPath(date, strategy, feature, bd));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	public static Map<String, Set<String>> loadContextHistory(String date, int strategy, int feature, int bd) throws ClassNotFoundException{ 
		try {
			File f = new File(Constants.getHistoryPath(date, strategy, feature, bd));
			if (f.exists()) return SaveAndLoad.loadHistory(f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new HashMap<String, Set<String>>();
	}	
	
	/** History Map Initiation
	 * Map <Key, Value> = <Date, List of contexts/sources which have been found for that date>
	 * **/
	
	public static HashMap<String, Set<String>> initHistory(LoadVersion load, String date, Integer versions) throws ParseException {
		HashMap<String, Set<String>> hMap = new HashMap<String, Set<String>>();
		
		String initDate = Constants.getPreviousDate(date, versions);		
			
		date = initDate;
		int i = 0;
		while(i < versions){
			if(date!=null){
				if(!hMap.containsKey(date))
					hMap.put(date, new TreeSet<String>());
				
				//cList = load.getContexts(initDate);
				Set<String> cList = load.loadContexts(Constants.getContextPath(date));
				
				Iterator<String> itCont = cList.iterator();
				while(itCont.hasNext()){
		  			String context = itCont.next();  			
					hMap.get(date).add(context);		
				}
			}
			date = Constants.getNextDate(date);
			i++;						
		}		
	
		return hMap;
	}
	
	
}
