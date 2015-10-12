package org.changealod.main.quality;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.west.changealod.analysis.Triple;
import org.west.changealod.file.LoadVersion;
import org.west.changealod.file.SaveAndLoad;
import org.west.changealod.file.WriteCVS;
import org.west.changealod.utility.Constants;

public class CheckQualityTripleModel {

	static Logger logger = Logger.getLogger(CheckQualityTripleModel.class);
	
	private static Map<Integer, Map<Integer, Double>> microprecisionMap = new HashMap<Integer, Map<Integer, Double>>();
	private static Map<Integer, Map<Integer, Double>> microrecallMap = new HashMap<Integer, Map<Integer, Double>>();
	
	
	
	
	private static Map<String, Set<Triple>> dataMap = new HashMap<String, Set<Triple>>();
	
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws ClassNotFoundException 
	 */
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {	
	
		LoadVersion load = new LoadVersion();
		
		BasicConfigurator.configure();
		int strategy = 0; 	int model = 0;
		//set array with all dates(snapshots) of the dataset
		//Constants.setDataArray(load.getDates(Constants.contextPath));
		Constants.setDataArray(load.getDates(Constants.getIndexPath(strategy)));
		List<String> dates = Constants.getDateArray();
		
	
		File fp, fr;
		
		for(int i= 0; i< dates.size(); i++){  
			String date = dates.get(i);	
			dataMap.clear();
			microprecisionMap.clear();
			microrecallMap.clear();
		
			for (int feature = 0 ; feature < Constants.features.length; feature++){			
				
				int bandwidth_index = 0;					
				while(bandwidth_index < Constants.bandwidths.length){		
					int bandwidth = Constants.bandwidths[bandwidth_index];
					bandwidth_index ++;
					
					Double microPrecision = 0.0;
					Double microRecall = 0.0;
					
					fp = new File(Constants.getIndexPath(date, strategy, feature,bandwidth)+ File.separator + "microPrecision.gz");
					if(fp.exists())  microPrecision = SaveAndLoad.loadPR(fp);
					
									
					fr = new File(Constants.getIndexPath(date, strategy, feature,bandwidth)+ File.separator + "microRecall.gz");
					if(fr.exists())	microRecall = SaveAndLoad.loadPR(fr);
				
					if(!microprecisionMap.containsKey(bandwidth))
						microprecisionMap.put(bandwidth, new HashMap<Integer, Double>());
					
					if(!microrecallMap.containsKey(bandwidth))
						microrecallMap.put(bandwidth, new HashMap<Integer, Double>());
					 
				
					microprecisionMap.get(bandwidth).put(feature, microPrecision);
					microrecallMap.get(bandwidth).put(feature, microRecall);
				}
			}
			
			
			SaveAndLoad.saveResultMap(microprecisionMap, Constants.getEvalPath(date, strategy, model) + File.separator + "microPrecision.gz");
			SaveAndLoad.saveResultMap(microrecallMap, Constants.getEvalPath(date, strategy, model) + File.separator +  "microRecall.gz");
			WriteCVS.writeResultMap(microprecisionMap, Constants.getEvalPath(date, strategy, model) + File.separator +  "microPrecision.csv");
			WriteCVS.writeResultMap(microrecallMap, Constants.getEvalPath(date, strategy, model) + File.separator +  "microRecall.csv");		
		}			
	}	
}

