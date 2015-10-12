package org.changealod.main.index;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
 * Create index - Single Step Setup
 * 
 * @author Renata Dividino
 * 
 */

public class GenerateSourcesSingleStep {	
	
	static Logger logger = Logger.getLogger(GenerateSourcesSingleStep.class);	
	
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
		Constants.setDataArray(load.getDates(Constants.getContextPath()));
				
		int strategy = 0;
	
		//List<String> dates = new ArrayList<String>(Constants.getDateArray(Constants.getVersions()));	
		List<String> dates = new ArrayList<String>(Constants.getDateArray());
		Collections.sort(dates);
		String date;
		
		int i = 0;
		while(i<dates.size()){
			date = dates.get(i); i = i+1;
			load.setDate(date);
			String next = Constants.getNextDate(date);
			
			HashMap<String, Set<String>> hContextMap = null; 
			for (int type = 0; type<9; type++){//
				Feature ft = null;
				if(type==0 || type == 1 )
					ft = new SizeFt(load); 
				else if(type==2)
					ft = new PRFt(load);				
				else if(type==3){
					ft = new ChangeFt(load, Constants.getDefaultChangeValue(), new JaccardDistance());
				}
				else if(type==4){
					ft = new ChangeFt(load, Constants.getDefaultChangeValue(), new TripleDistance());				
				}				
				else if(type==5){
					ft = new ChangeFt(load, Constants.getDefaultChangeValue(), new Quantitative());
				}
				else if(type==6){
					ft = new DynamicsFt(load, Constants.getVersions(), Constants.getDefaultChangeValue(), new JaccardDistance());
				}
				else if(type==7){
					ft = new DynamicsFt(load, Constants.getVersions(), Constants.getDefaultChangeValue(), new TripleDistance());
				}	
				else if(type==8){
					ft = new AgeFt(load);
				}
				
				
				hContextMap = History.initHistory(load, date, Constants.getVersions());
				ft.extractFt(hContextMap, date, type, strategy, 0);
				int order = 0;	if(type == 1) order = 0; else order = 1;
				
															
				int size = 0; 
				Iterator<String> itCont = ft.getfMap().keySet().iterator();
		        while(itCont.hasNext()){		        	
		        	size = size + load.getTriples(itCont.next(), 0, date, 0, 0).size();
		        }
		        
		        Map<String, Double> sfMap = MapValueSort.sortByValue(ft.getfMap(), order);		        
		        int bandwidth_index = 0;
		        while(bandwidth_index < Constants.bandwidths.length){		        	
		        	int bandwidth = Constants.bandwidths[bandwidth_index];
					bandwidth_index ++;
					
					double micro_retrievedAndRelevant = 0, micro_relevant = 0, micro_retrieved = 0;
					double perc = (double) size/ 100.0;
					double limit = (perc * bandwidth);
								
					int loaded = 0;
					Set<Triple> tNext, tDate;
					Map<String, Integer> totalChangedTriplePerContext = new HashMap<String, Integer>();
					int totalNewTriples = 0, totalDeletedTriples = 0, totalChangedTriples= 0, totalUpdatedTriples = 0,
							totalUpdatedNewTriples = 0, totalUpdatedDeletedTriples = 0, totalUpdatedChangedTriples = 0,
							totalUpdatedContexts = 0, totalChangedContexts = 0, totalUpdatedChangedContexts = 0;
					itCont =  sfMap.keySet().iterator();
					while(itCont.hasNext()){
						String context = itCont.next();						
						tNext = load.getTriples(context, type, next, 0, 0);// always from the original source!
						tDate = load.getTriples(context, type, date, 0,0);
					    
						
						Set<Triple> newTriples = Sets.difference(tNext, tDate); totalNewTriples = totalNewTriples + newTriples.size();
						Set<Triple> deletedTriples = Sets.difference(tDate, tNext); totalDeletedTriples = totalDeletedTriples + deletedTriples.size();
						int changedTriples = newTriples.size() + deletedTriples.size();	totalChangedTriples = totalChangedTriples + changedTriples;					
						totalChangedTriplePerContext.put(context, changedTriples);
						if(changedTriples >0) totalChangedContexts++; 
						
						loaded = loaded + tDate.size();
						if((limit > 0) && (limit >= loaded)){
							micro_retrievedAndRelevant = micro_retrievedAndRelevant + tNext.size();
							micro_retrieved = micro_retrieved + tNext.size();
							micro_relevant = micro_relevant + tNext.size();
							totalUpdatedTriples = totalUpdatedTriples + tNext.size();
							totalUpdatedNewTriples = totalUpdatedNewTriples + newTriples.size();
							totalUpdatedDeletedTriples = totalUpdatedDeletedTriples + deletedTriples.size();
							totalUpdatedChangedTriples = totalUpdatedChangedTriples + changedTriples;
							totalUpdatedContexts++; 
							if(changedTriples >0) totalUpdatedChangedContexts++;
						}else{
							Set<Triple> intersectionTriples = Sets.intersection(tNext, tDate);
							micro_retrievedAndRelevant = micro_retrievedAndRelevant + intersectionTriples.size();
							micro_retrieved = micro_retrieved + tDate.size();
							micro_relevant = micro_relevant + tNext.size();
						}
					}
					
					double microPrecision = micro_retrievedAndRelevant/micro_retrieved; 
					double microRecall = micro_retrievedAndRelevant/micro_relevant;
						
					SaveAndLoad.saveQuality(Constants.getIndexPath(next, strategy, type, bandwidth) + File.separator + "microPrecision.gz",  microPrecision);
					SaveAndLoad.saveQuality(Constants.getIndexPath(next, strategy, type, bandwidth)+ File.separator + "microRecall.gz", microRecall);
					
					
					SaveAndLoad.saveFeature(ft.getfMap(), Constants.getFeaturePath(Constants.getNextDate(date), strategy, type, bandwidth));
					SaveAndLoad.saveStatChanges(Constants.getIndexPath(next, strategy, type, bandwidth) + File.separator + "statChange.gz", totalChangedTriplePerContext);
					
					SaveAndLoad.saveStat(Constants.getIndexPath(next, strategy, type, bandwidth) + File.separator + "stat.gz", 
							size, (int) limit, totalNewTriples, totalDeletedTriples, totalChangedTriples, 
							totalUpdatedTriples, totalUpdatedNewTriples, totalUpdatedDeletedTriples, totalUpdatedChangedTriples, 
							totalUpdatedContexts, totalChangedContexts, totalUpdatedChangedContexts);
						
						
				}				
			}
		}
	}
	
}



