package org.west.changealod.data.feature;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.west.changealod.file.LoadVersion;
import org.west.changealod.utility.Constants;

/**
 * Extract context's age (last update) of a dataset
 * 
 * @author Renata Dividino
 * 
 */

public class AgeFt extends Feature{
	
	public AgeFt(LoadVersion load){
		super(load);
	}
	
	// change age..
	public void extractFt(Map<String, Set<String>> hMap, 
			String date, int type, int strategy, int bd){
		logger.info("Extracting age");
		
		Set<String> cList ;
		
		if(strategy==0)	cList = load.loadContexts(Constants.getContextPath(date));			
		else cList = load.loadContexts(Constants.getIndexPath(date, strategy, type, bd));	
		
		
		Iterator<String> itCont = cList.iterator();
		
		while(itCont.hasNext()){
			String context = itCont.next();
			if(!hMap.isEmpty()){
				String lastUpdate = "";
				boolean found = false;
				Iterator<String> itDates = new TreeSet<String>(hMap.keySet()).iterator();
				while(itDates.hasNext() && (!found)){
					String dt = itDates.next();
					if(hMap.get(dt).contains(context)){
						lastUpdate = dt; found = true;
					}
				} if(!found) fMap.put(context, 0.0);			
				fMap.put(context, getAge(date, lastUpdate));
			}else fMap.put(context, 0.0);
		}
		logger.info("Age done");
	}
	
	public Double getAge(String date, String lastDate){
		Double age = 0.0;
		try {			
			DateFormat df = new SimpleDateFormat( "yyyy-mm-dd" );
			
			Calendar cDate = Calendar.getInstance();										
			cDate.setTime(df.parse(date));
			
			Calendar lastUpdate = Calendar.getInstance();						
			lastUpdate.setTime(df.parse(lastDate));
			
			Calendar ldate = (Calendar) lastUpdate.clone();  
		  
			while (ldate.before(cDate)) {  
				ldate.add(Calendar.DAY_OF_MONTH, 1);  
				age++;  
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return age;
	}
}
