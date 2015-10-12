package org.changealod.main.index;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.west.changealod.analysis.Triple;
import org.west.changealod.analysis.change.JaccardDistance;
import org.west.changealod.analysis.change.Quantitative;
import org.west.changealod.analysis.change.TripleDistance;
import org.west.changealod.data.History;
import org.west.changealod.data.feature.AgeFt;
import org.west.changealod.data.feature.ChangeFt;
import org.west.changealod.data.feature.DynamicsFt;
import org.west.changealod.data.feature.Feature;
import org.west.changealod.data.feature.PRFt;
import org.west.changealod.data.feature.SizeFt;
import org.west.changealod.file.LoadVersion;
import org.west.changealod.file.SaveAndLoad;
import org.west.changealod.utility.Constants;
import org.west.changealod.utility.MapValueSort;

import com.google.common.collect.Sets;


/**
 * Create index - Main
 * 
 * @author Renata Dividino
 * 
 */

public class GenerateIndexIterativeSetup {	
	
	static Logger logger = Logger.getLogger(GenerateIndexIterativeSetup.class);	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws ClassNotFoundException 
	 * @throws ParseException 
	 */
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException, ParseException {	
		//logging
		BasicConfigurator.configure();		
		WriterAppender fh = new WriterAppender(new SimpleLayout(), new FileOutputStream(Constants.geLogPath()));
        logger.addAppender(fh);
        logger.setLevel(Level.INFO); 
        LoadVersion load = new LoadVersion();
        
        int strategy = 1;
        
		Constants.setDataArray(load.getDates(Constants.getContextPath()));
		int total = Constants.getDateArray().size();
	
		
		for (int initDate= 5; initDate < total; initDate = initDate + 10){
			List<String> dates = new ArrayList<String>(Constants.getDateArrayUntilVersion(initDate));
			Collections.sort(dates);
			Iterator<String> itDates = dates.iterator();
			boolean first = true;
			String date, next;			
			while(itDates.hasNext()){				
				date = itDates.next();				
				next = Constants.getNextDate(date);
				if(next!=null){
					Map<String, Set<String>> hContextMap; Map<String, Double> sfContextMap;
					Feature ft = null;	
					for (int feature = 0; feature<8;  feature++){//
							if (first) load.copyData(date, strategy, feature); 
												
							if(feature==0 || feature == 1)	ft = new SizeFt(load); 
							else if(feature==2)	ft = new PRFt(load);				
							else if(feature==3)	ft = new ChangeFt(load, Constants.getDefaultChangeValue(), new JaccardDistance());						
							else if(feature==4)	ft = new ChangeFt(load, Constants.getDefaultChangeValue(), new TripleDistance());						
							else if(feature==5)	ft = new ChangeFt(load, Constants.getDefaultChangeValue(), new Quantitative());						
							else if(feature==6)	ft = new DynamicsFt(load, Constants.getVersions(), Constants.getDefaultChangeValue(), new JaccardDistance());						
							else if(feature==7) ft = new DynamicsFt(load, Constants.getVersions(), Constants.getDefaultChangeValue(), new TripleDistance());						
							else if(feature==8) ft = new AgeFt(load);		   			
							
							
							int order; if(feature == 1) order = 0; else order = 1;
							int bandwidth_index = 0;
							while(bandwidth_index < Constants.bandwidths.length){		
								int bandwidth = Constants.bandwidths[bandwidth_index];					
								bandwidth_index ++;
								
								
								hContextMap =  History.loadContextHistory(date, strategy, feature, bandwidth);
								ft.extractFt(hContextMap, date, feature, strategy, bandwidth);
								
		   						Iterator<String> itCont = ft.getfMap().keySet().iterator();
								int size = 0;
								while(itCont.hasNext()){
									String context = itCont.next();
									size = size + load.getTriples(context, feature, date, strategy, bandwidth).size();
								}
												        
								double micro_retrievedAndRelevant = 0, micro_relevant = 0, micro_retrieved = 0;
								
								double perc = (double)size/ 100.0;
								double limit = (perc * bandwidth);
								
								int loaded = 0;
								
								Set<Triple> tNext, tDate;						
								sfContextMap = MapValueSort.sortByValue(ft.getfMap(), order);
								itCont = sfContextMap.keySet().iterator(); 
								while(itCont.hasNext()){
									String context = itCont.next();		
									tDate = load.getTriples(context, feature, date, strategy, bandwidth);
									tNext = load.getTriples(context, feature, next, 0, bandwidth);// always from the original source!
									micro_relevant = micro_relevant + tNext.size();
								
							    	loaded = loaded + tDate.size();
									if((limit > 0) && (limit >= loaded)){						
										if(!hContextMap.containsKey(next)) hContextMap.put(next, new TreeSet<String>());									
										hContextMap.get(next).add(context);						
										SaveAndLoad.saveTripleMap(tNext, Constants.getIndexPath(next, context, strategy, feature, bandwidth), context);
										micro_retrievedAndRelevant = micro_retrievedAndRelevant + tNext.size();
										micro_retrieved = micro_retrieved + tNext.size();																		
									}else{					
										SaveAndLoad.saveTripleMap(tDate, Constants.getIndexPath(next, context, strategy, feature, bandwidth), context);									
										micro_retrievedAndRelevant = micro_retrievedAndRelevant + Sets.intersection(tNext, tDate).size();
										micro_retrieved = micro_retrieved + tDate.size();									
									}							    		    
								}
								double microPrecision = micro_retrievedAndRelevant/micro_retrieved; 
								double microRecall = micro_retrievedAndRelevant/micro_relevant;							
								SaveAndLoad.saveQuality(Constants.getIndexPath(next, strategy, feature, bandwidth) + File.separator + "microPrecision.gz",  microPrecision);
								SaveAndLoad.saveQuality(Constants.getIndexPath(next, strategy, feature, bandwidth)+ File.separator + "microRecall.gz", microRecall);						
								
								SaveAndLoad.saveFeature(ft.getfMap(), Constants.getFeaturePath(next, strategy, feature, bandwidth));
								History.saveHistory(hContextMap, next, strategy, feature, bandwidth);
							}
						}
					}
				}
				first = false;
			}
		}
	
}

