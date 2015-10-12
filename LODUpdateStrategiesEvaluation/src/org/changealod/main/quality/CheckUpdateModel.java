package org.changealod.main.quality;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.west.changealod.file.LoadVersion;
import org.west.changealod.file.SaveAndLoad;
import org.west.changealod.file.WriteCVS;
import org.west.changealod.utility.Constants;

/**
 * Load the stats. and write in a csv file.
  for each bandwidth, write down how much triples have been updated, changed, changed and updated, and how much contexts
  have been updated, changed, changed and updated	
 * @author DIVIDINO
 *
 */
public class CheckUpdateModel {

	static Logger logger = Logger.getLogger(CheckUpdateModel.class);
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws ClassNotFoundException 
	 */
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {	
	
		LoadVersion load = new LoadVersion();
			
		BasicConfigurator.configure();
		
		int strategy = 0;
		
		
		Constants.setDataArray(load.getDates(Constants.getIndexPath(strategy)));
		Iterator<String> itDates = Constants.getDateArray().iterator();
		//Iterator<String> itDates = Constants.getDateArray(Constants.getDataArray().indexOf("2014-06-01")).iterator();		
		//strategy 0 = single step, 1 = iterative
		
		//int count = 0;		
		while(itDates.hasNext()){		
			String date = itDates.next();
			//count ++;
			
	
			for (int feature = 0 ; feature < (Constants.features.length -1); feature++){
				Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
				int bandwidth_index = 0;
				while(bandwidth_index < Constants.bandwidths.length){		
					int bandwidth = Constants.bandwidths[bandwidth_index];
					bandwidth_index ++;
					
					File file = new File(Constants.getIndexPath(date, strategy, feature, bandwidth) + File.separator + "stat.gz");
					if(file.exists()){
						List<Integer> stat = SaveAndLoad.loadStat(file);
					
						map.put(bandwidth, stat);
					}						
				}					
				
				
				String path = Constants.getEvalPath(date, strategy) + File.separator + feature + File.separator + "UpdateSummary.csv";
				WriteCVS.writeUpdateMap(map, path);
				map.clear();
			}
				
			
		}			
	}

	
}
