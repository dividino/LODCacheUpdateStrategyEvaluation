package org.changealod.main.quality;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.west.changealod.file.LoadVersion;
import org.west.changealod.file.SaveAndLoad;
import org.west.changealod.file.WriteCVS;
import org.west.changealod.utility.Constants;

public class ComputeAverageQualitySS {

	static Logger logger = Logger.getLogger(ComputeAverageQualitySS.class);
	
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws ClassNotFoundException 
	 */
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {	
	
		int strategy = 0;
		int model = 0;
		
		BasicConfigurator.configure();
		LoadVersion load = new LoadVersion();
		Constants.setDataArray(load.getDates(Constants.evalPath +File.separator + Constants.strategies[strategy]));
		
		Iterator<String> itDates = Constants.getDateArray().iterator();	
		Map<Integer, Map<Integer, Double>> microPMap = new HashMap<Integer, Map<Integer, Double>>();
		Map<Integer, Map<Integer, Double>> microRMap = new HashMap<Integer, Map<Integer, Double>>();
		
		
		while(itDates.hasNext()){
			String date = itDates.next();
			String microPfile = Constants.getEvalPath(date, strategy, model) + File.separator + "microPrecision.gz";
			String microRfile = Constants.getEvalPath(date, strategy, model) + File.separator + "microRecall.gz";
			
			Map<Integer, Map<Integer, Double>> microPtmp = SaveAndLoad.loadResultMap(new File(microPfile));
			Map<Integer, Map<Integer, Double>> microRtmp = SaveAndLoad.loadResultMap(new File(microRfile));
					
			addValue(microPtmp, microPMap);
			addValue(microRtmp, microRMap);
			
		}
		average(microPMap,Constants.getDateArray().size());
		average(microRMap,Constants.getDateArray().size());
		
		SaveAndLoad.saveResultMap(microPMap, Constants.getEvalPath(strategy, model) + File.separator + "microPrecision.gz");
		SaveAndLoad.saveResultMap(microRMap, Constants.getEvalPath(strategy, model) + File.separator +  "microRecall.gz");
		WriteCVS.writeResultMap(microPMap, Constants.getEvalPath(strategy, model) + File.separator +  "microPrecision.csv");
		WriteCVS.writeResultMap(microRMap, Constants.getEvalPath(strategy, model) +File.separator +  "microRecall.csv");
		
	}
	
	private static void addValue(
				Map<Integer, Map<Integer, Double>> tmpMap, 
				Map<Integer, Map<Integer, Double>> fMap){
	    
		Iterator<Integer> itbd = tmpMap.keySet().iterator();
		while(itbd.hasNext()){
			int bd = itbd.next();
			Iterator<Integer> itft = tmpMap.get(bd).keySet().iterator();
			while(itft.hasNext()){
				int ft = itft.next();
				double tmp = tmpMap.get(bd).get(ft);
				addValue(ft, bd, tmp, fMap);
			}
		}
	}
		
	private static void average(
			Map<Integer, Map<Integer, Double>> map, double total) {
		
		Iterator<Integer> itbd = map.keySet().iterator();
		while(itbd.hasNext()){
			int bd = itbd.next();
			Iterator<Integer> itft = map.get(bd).keySet().iterator();
			while(itft.hasNext()){
				int ft = itft.next();
				double tmp = map.get(bd).get(ft);
				tmp = tmp/total;
				map.get(bd).put(ft, tmp);
				
			}
		}
	}
	
	private static void addValue(int feature,
			int bandwidth, Double value, Map<Integer, Map<Integer, Double>> map) {
		
		if(!map.containsKey(bandwidth))
			map.put(bandwidth, new HashMap<Integer, Double>());
		 
		if(!map.get(bandwidth).containsKey(feature)){
			map.get(bandwidth).put(feature, 0.0);
		}
			
		double tmp = map.get(bandwidth).get(feature) + value;
		map.get(bandwidth).put(feature, tmp);
		
		
	}

	
		
}
