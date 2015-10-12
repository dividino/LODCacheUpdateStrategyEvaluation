package org.changealod.main.quality;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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


public class ComputeAverageQualityIT {


		static Logger logger = Logger.getLogger(ComputeAverageQualityIT.class);
		
		private static Map<Integer, Map<Integer, Map<Integer, Double>>> avgMicroprecisionMap = new HashMap<Integer, Map<Integer, Map<Integer, Double>>>();
		private static Map<Integer, Map<Integer, Map<Integer, Double>>> avgMicrorecallMap = new HashMap<Integer, Map<Integer, Map<Integer, Double>>>();
		
		
		/**
		 * @param args
		 * @throws IOException 
		 * @throws FileNotFoundException 
		 * @throws ClassNotFoundException 
		 */
		
		public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {	
		
			int strategy = 1;
			int model = 0;
			
			BasicConfigurator.configure();
			LoadVersion load = new LoadVersion();
			Constants.setDataArray(load.getDates(Constants.evalPath + File.separator + Constants.strategies[strategy]));
			List<String> dates = Constants.getDateArray();
			Collections.sort(dates);
			
			logger.info("read dates");
			
			
			Map<Integer, List<String>> datePartitions = new HashMap<Integer, List<String>>();
			for(int i = 0; i< dates.size(); i++){
				int key = i % 10;
				if(!datePartitions.containsKey(key)) datePartitions.put(key, new ArrayList<String>());
				datePartitions.get(key).add(dates.get(i));
			}
			
			logger.info("read partitions");			
			for(int partition = Constants.getVersions(); partition < datePartitions.keySet().size(); partition ++){ // ignore the first 5 index - they are copy of the original
				Map<Integer, Map<Integer, Double>> microPMap = new HashMap<Integer, Map<Integer, Double>>();
				Map<Integer, Map<Integer, Double>> microRMap = new HashMap<Integer, Map<Integer, Double>>();
				
				dates = datePartitions.get(partition);
				Collections.sort(dates);
				Iterator<String> itDates = dates.iterator();
				while(itDates.hasNext()){
					String date = itDates.next();
						
					String microPfile = Constants.getEvalPath(date, strategy, model) + File.separator + "microPrecision.gz";
					String microRfile = Constants.getEvalPath(date, strategy, model) + File.separator + "microRecall.gz";
					Map<Integer, Map<Integer, Double>> microPtmp = SaveAndLoad.loadResultMap(new File(microPfile));
					Map<Integer, Map<Integer, Double>> microRtmp = SaveAndLoad.loadResultMap(new File(microRfile));
					
					
					
					addValue(microPtmp, microPMap);
					addValue(microRtmp, microRMap);					
				}
				
				average(microPMap, datePartitions.get(partition).size());
				average(microRMap, datePartitions.get(partition).size());
						
				
				avgMicroprecisionMap.put(partition,microPMap);
				avgMicrorecallMap.put(partition,microRMap);
				
				SaveAndLoad.saveResultMap(microPMap, Constants.getEvalPath(strategy, model) + File.separator + partition + File.separator + "microPrecision.gz");
				SaveAndLoad.saveResultMap(microRMap, Constants.getEvalPath(strategy, model) + File.separator +  partition + File.separator + "microRecall.gz");
				WriteCVS.writeResultMap(microPMap, Constants.getEvalPath(strategy, model) + File.separator +  partition + File.separator + "microPrecision.csv");
				WriteCVS.writeResultMap(microRMap, Constants.getEvalPath(strategy, model) +File.separator +  partition + File.separator + "microRecall.csv");
				
				writeResultMap(avgMicroprecisionMap, Constants.getEvalPath(strategy, model) +  File.separator + "avgMicroPrecision.csv");
				writeResultMap(avgMicrorecallMap, Constants.getEvalPath(strategy, model) +  File.separator + "avgMicroRecall.csv");
			}
		}
			
		public static void writeResultMap(Map<Integer, Map<Integer, Map<Integer,Double>>> map, String path) throws IOException {
			
			File f = new File(path);
			File outD = new File(f.getParent());
			if (!outD.exists()) {
				outD.mkdirs();
			}
			if(!f.exists()) f.createNewFile();
			
			FileWriter out = new FileWriter(f);
			out.append("bandwidth;"); 
			
			int i = 0;
			while(i < Constants.features.length){			
				out.append(Constants.features[i] + ";");
				i++;
			}		
			out.append("\n");
			
			Iterator<Integer> itPartitions = map.keySet().iterator();
			if(itPartitions.hasNext()){
				Integer partition = itPartitions.next();
				Iterator<Integer> itbds = map.get(partition).keySet().iterator();
				while(itbds.hasNext()){
					Integer bd = itbds.next();	
					List<Integer> partitions = new ArrayList<Integer>(map.keySet());
					Collections.sort(partitions);
					itPartitions = partitions.iterator();
					while(itPartitions.hasNext()){
						partition = itPartitions.next();
						Iterator<Integer> itFt = map.get(partition).get(bd).keySet().iterator();
						out.append(bd.toString() + ";");
						while(itFt.hasNext()){
							int ft = itFt.next();
							out.append(map.get(partition).get(bd).get(ft).toString().replace(".", ",") + ";");
						}
						out.append("\n");
					}
					out.append("\n");
				}
			}
				
			
			out.flush();
			out.close();		
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

