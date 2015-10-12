package org.changealod.main;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.west.changealod.analysis.Triple;
import org.west.changealod.file.LoadVersion;
import org.west.changealod.file.SaveAndLoad;
import org.west.changealod.utility.Constants;


/**
 * context-split
 * 
 * @author Renata Dividino
 * 
 */



public class ContextSplit {
	
		
	public static void main(String[] args) throws FileNotFoundException, IOException, URISyntaxException{
		
		LoadVersion load = new LoadVersion();
		Constants.setDataArray(load.getDates(Constants.getHeaderPath()));
		
		Iterator<String> itDates = Constants.getDateArray().iterator();
		String date;
		
		while(itDates.hasNext()){
			date = itDates.next();			
			Map<String, Set<Triple>> tmaps = SaveAndLoad.loadPLDTripleMap(new File(Constants.getDataHeaderPath(date)));
			Iterator<String> itCont = tmaps.keySet().iterator();
			while(itCont.hasNext()){
				String pld = itCont.next();				
				SaveAndLoad.saveTripleMap(tmaps.get(pld),Constants.getContextPath(date, pld), pld);
			}
			tmaps.clear();
		}
			
	}
					
}
	

