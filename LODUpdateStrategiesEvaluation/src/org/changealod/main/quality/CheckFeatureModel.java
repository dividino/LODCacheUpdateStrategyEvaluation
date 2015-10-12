package org.changealod.main.quality;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.west.changealod.file.LoadVersion;
import org.west.changealod.file.SaveAndLoad;
import org.west.changealod.utility.Constants;

/* Load the stats und write in a csv file.
 * Feature Map = For each context, the total of changed triples, and its feature scores.
 * Size Map =
 */
public class CheckFeatureModel {

	static Logger logger = Logger.getLogger(CheckFeatureModel.class);
	
	static Map<String, Map<Integer, Double>> chgMap = new HashMap<String, Map<Integer, Double>>();
	static Map<String, Map<Integer, Double>> map = new HashMap<String, Map<Integer, Double>>();
	
	
	static Map<Integer, Integer> qtContexts = new HashMap<Integer, Integer>(); 
	static Map<Integer, Integer> triplesInContexts = new HashMap<Integer, Integer>(); 
	static Map<Integer, Integer> changesInContexts = new HashMap<Integer, Integer>(); 
	//static Map<Integer, Integer> newInContexts = new HashMap<Integer, Integer>();
	//static Map<Integer, Integer> delInContexts = new HashMap<Integer, Integer>();
		
	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws ClassNotFoundException 
	 */
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {	
	
		BasicConfigurator.configure();
		LoadVersion load = new LoadVersion();
		
		int strategy = 0;	
		
		
		Constants.setDataArray(load.getDates(Constants.getIndexPath(strategy)));
		Iterator<String> itDates = Constants.getDateArray().iterator();
		//Iterator<String> itDates = Constants.getDateArray(Constants.getDataArray().indexOf("2014-06-01")).iterator();
		
		//set array with all dates(snapshots) of the dataset
		//int count = 0;
		while(itDates.hasNext()){
			String date = itDates.next();
			//String date = "2014-06-01";
			//count ++;
			
			String pathChg = Constants.getIndexPath(date, strategy, 1, 0) + File.separator + "statChange.gz";
			//String pathNew = Constants.getIndexPath(date, strategy, 1, 0) + File.separator + "statNew.gz";
			//String pathDel = Constants.getIndexPath(date, strategy, 1, 0) + File.separator + "statDel.gz";
			
			Map<String, Integer> changesMap = SaveAndLoad.loadStatChanges(new File(pathChg));
			//Map<String, Integer> newMap = SaveAndLoad.loadStatChanges(new File(pathNew));
			//Map<String, Integer> delMap = SaveAndLoad.loadStatChanges(new File(pathDel));
			
			addChanges2Map(changesMap);//, newMap, delMap); //adding to map only totalChangedTriples
			//addChanges2Map(changesMap); //adding to map only totalChangedTriples
			
			
			for(int feature = 1; feature< 9; feature++){			
				int bandwidth = 0;
				
				//stastictics : totalChangedTriplePerContext
				
				String path = Constants.getFeaturePath(date, strategy, feature, bandwidth);
				//feature score for all contexts
				Map<String, Double> featureMap = SaveAndLoad.loadFeature(new File(path));
				addFeatures2Map(featureMap, feature);
			}
				
			String path = Constants.getEvalPath(date, strategy);
			writeFeatureMap(path + File.separator + "features.csv");
			writeSize(path + File.separator + "sizeFreqC.csv");
			writeTriples(path + File.separator + "tripleFreq.csv");
		}
		
}


	
	

	private static void writeFeatureMap(String path) throws IOException {
		File f = new File(path);
		File outD = new File(f.getParent());
		if (!outD.exists()) {
			outD.mkdirs();
		}
		if(!f.exists()) f.createNewFile();
		
		FileWriter out = new FileWriter(f);
				

		out.append("Context" + ";");
		out.append("TotalOfChanges" + ";");
		
		int ft = 1;
		while(ft < Constants.features.length){						
			out.append(Constants.features[ft] + ";");
			ft++;
		}		 
		out.append("\n");
		
		Iterator<String> it = map.keySet().iterator();
		while(it.hasNext()){
			String context = it.next();
			out.append(context + ";");
			out.append(Double.toString(chgMap.get(context).get(0)).replace(".", ",")+ ";");
			Iterator<Integer> itFt = map.get(context).keySet().iterator();
			while(itFt.hasNext()){
				ft = itFt.next();
				out.append(map.get(context).get(ft).toString().replace(".", ",") + ";");				
			}		
			out.append("\n");
		}
		out.flush();
		out.close();
	}

	private static void writeSize(String path) throws IOException {
		// TODO Auto-generated method stub
		File f = new File(path);
		File outD = new File(f.getParent());
		if (!outD.exists()) {
			outD.mkdirs();
		}
		if(!f.exists()) f.createNewFile();
		
		FileWriter out = new FileWriter(f);
				
		out.append("Size_Buckets-Size_of_contexts_in_triples" + ";");
		out.append("Total_of_contexts_which_belong_to_the_bucket" + ";");
		out.append("Total_of_triples_(total_of_contexts_multipled_by_the_#_of_triples_in_each_context" + ";");
		out.append("Total_of_changes_(total_of_contexts_multipled_by_the_#_of_changed_triples_in_each_context" + ";");
		out.append("Total_of_new"+ ";");
		out.append("Total_of_deletion" + ";");
		out.append("\n");
		
		
		Iterator<Integer> it = qtContexts.keySet().iterator();
		while(it.hasNext()){
			Integer key = it.next();
			if(key == 0) out.append("0-9" + ";");
			else if(key == 1) out.append("10-99" + ";");
			else if(key == 2) out.append("100-999" + ";");
			else if(key == 3) out.append("1000-9999" + ";");
			else if(key == 4) out.append("10000-99999" + ";");
			else if(key == 5) out.append("100000-999999" + ";");
			else if(key == 6) out.append("1000000-9999999" + ";");
			else if(key == 7) out.append("<10000000" + ";");
			
			
			
			out.append(Double.toString(qtContexts.get(key)).replace(".", ",")+ ";");
			out.append(Double.toString(triplesInContexts.get(key)).replace(".", ",")+ ";");
			out.append(Double.toString(changesInContexts.get(key)).replace(".", ",")+ ";");
			//out.append(Double.toString(newInContexts.get(key)).replace(".", ",")+ ";");
			//out.append(Double.toString(delInContexts.get(key)).replace(".", ","));
			
			out.append("\n");
		}
		out.flush();
		out.close();
	}

	private static void writeTriples(String path) throws IOException {
		// TODO Auto-generated method stub
		File f = new File(path);
		File outD = new File(f.getParent());
		if (!outD.exists()) {
			outD.mkdirs();
		}
		if(!f.exists()) f.createNewFile();

		FileWriter out = new FileWriter(f);
				
		out.append("Size" + ";");
		out.append("Contexts" + ";");
		out.append("\n");


		Iterator<Integer> it = triplesInContexts.keySet().iterator();
		while(it.hasNext()){
			Integer key = it.next();
			if(key == 0) out.append("0-9" + ";");
			else if(key == 1) out.append("10-99" + ";");
			else if(key == 2) out.append("100-999" + ";");
			else if(key == 3) out.append("1000-9999" + ";");
			else if(key == 4) out.append("10000-99999" + ";");
			else if(key == 5) out.append("100000-999999" + ";");
			else if(key == 6) out.append("1000000-9999999" + ";");
			else if(key == 7) out.append("<10000000" + ";");
			
			
			out.append(Double.toString(triplesInContexts.get(key)));
			out.append("\n");
		}
		out.flush();
		out.close();
		}
	
	//Map<Context, Feature Score>
	private static void addChanges2Map(Map<String, Integer> changesMap){//, Map<String, Integer> newMap, Map<String, Integer> delMap) {
	//private static void addChanges2Map(Map<String, Integer> changesMap) {
		Iterator<String> itContexts = changesMap.keySet().iterator();
		while(itContexts.hasNext()){
			String context = itContexts.next();
			Double valueChg = (Double.valueOf(Integer.toString(changesMap.get(context))));
			//Double valueNew = (Double.valueOf(Integer.toString(newMap.get(context))));
			//Double valueDel = (Double.valueOf(Integer.toString(delMap.get(context))));
			
			add(context, 0, valueChg);
			//add(context, 1, valueNew);
			//add(context, 2, valueDel);
		}
	}
	
	private static void add(String context, Integer feature, Double value){
		if(!chgMap.containsKey(context)) chgMap.put(context, new HashMap<Integer, Double>());			
		if(chgMap.get(context).containsKey(feature)){ 
			double tmp = chgMap.get(context).get(feature);
			value = value + tmp;
		}
		chgMap.get(context).put(feature, value);
	}

	private static void addFeatures2Map(Map<String, Double> featureMap, Integer feature) {
		Iterator<String> itContexts = featureMap.keySet().iterator();
		while(itContexts.hasNext()){
			String context = itContexts.next();
			Double value = featureMap.get(context);
			if(feature == 1) addSizeAndChanged(value, chgMap.get(context).get(0));//,chgMap.get(context).get(1),chgMap.get(context).get(2));
			
			if(!map.containsKey(context)) map.put(context, new HashMap<Integer, Double>());
			if(map.get(context).containsKey(feature)){ 
				double tmp = map.get(context).get(feature);
				value = value + tmp;
			}
			map.get(context).put(feature, value);			
		}
	}


	private static void addSizeAndChanged(Double sizeOfContext, Double totalOfChanges){//, Double totalOfNew, Double totalOfDel) {
		Integer value =  sizeOfContext.intValue();
		Integer change = totalOfChanges.intValue();
		//Integer newT = totalOfNew.intValue();
		//Integer delT = totalOfDel.intValue();
		if(value < 10){
			if(!qtContexts.containsKey(0)) {qtContexts.put(0, 1); triplesInContexts.put(0,value); changesInContexts.put(0, change);}
			//newInContexts.put(0, newT); delInContexts.put(0, delT);} 
			else{
				int tmp = qtContexts.get(0); qtContexts.put(0, 1 + tmp);
				tmp = triplesInContexts.get(0); triplesInContexts.put(0, value + tmp);
				tmp = changesInContexts.get(0); changesInContexts.put(0, change + tmp);
				//tmp = newInContexts.get(0); newInContexts.put(0, newT + tmp);
				//tmp = delInContexts.get(0); delInContexts.put(0, delT + tmp);
			} 
		}else if((10 <= value) && (value < 100)){
			if(!qtContexts.containsKey(1)) {qtContexts.put(1, 1);triplesInContexts.put(1,value);changesInContexts.put(1, change);}
			//newInContexts.put(1, newT); delInContexts.put(1, delT);}
			else{
				int tmp = qtContexts.get(1); qtContexts.put(1, 1 + tmp);
				tmp = triplesInContexts.get(1); triplesInContexts.put(1, value + tmp);
				tmp = changesInContexts.get(1); changesInContexts.put(1, change + tmp);
				//tmp = newInContexts.get(1); newInContexts.put(1, newT + tmp);
				//tmp = delInContexts.get(1); delInContexts.put(1, delT + tmp);
			} 
		}else if((100 <= value) && (value < 1000)){
			if(!qtContexts.containsKey(2)) {qtContexts.put(2, 1);triplesInContexts.put(2,value);changesInContexts.put(2, change);}
			//newInContexts.put(2, newT); delInContexts.put(2, delT);}			
			else{
				int tmp = qtContexts.get(2); qtContexts.put(2, 1 + tmp);
				tmp = triplesInContexts.get(2); triplesInContexts.put(2, value + tmp);
				tmp = changesInContexts.get(2); changesInContexts.put(2, change + tmp);
				//tmp = newInContexts.get(2); newInContexts.put(2, newT + tmp);
				//tmp = delInContexts.get(2); delInContexts.put(2, delT + tmp);
			} 
		} else if((1000 <= value) && (value < 10000)){
			if(!qtContexts.containsKey(3)) {qtContexts.put(3, 1);triplesInContexts.put(3,value);changesInContexts.put(3, change);}
			//newInContexts.put(3, newT); delInContexts.put(3, delT);} 
			else{
				int tmp = qtContexts.get(3); qtContexts.put(3, 1 + tmp);
				tmp = triplesInContexts.get(3); triplesInContexts.put(3, value + tmp);
				tmp = changesInContexts.get(3); changesInContexts.put(3, change + tmp);
				//tmp = newInContexts.get(3); newInContexts.put(3, newT + tmp);
				//tmp = delInContexts.get(3); delInContexts.put(3, delT + tmp);
			} 
		}else if ((10000 <= value) && (value < 100000)){
			if(!qtContexts.containsKey(4)) {qtContexts.put(4, 1); triplesInContexts.put(4,value);changesInContexts.put(4, change);}
			//newInContexts.put(4, newT); delInContexts.put(4, delT);} 
			else{
				int tmp = qtContexts.get(4); qtContexts.put(4, 1 + tmp);
				tmp = triplesInContexts.get(4); triplesInContexts.put(4, value + tmp);
				tmp = changesInContexts.get(4); changesInContexts.put(4, change + tmp);
				//tmp = newInContexts.get(4); newInContexts.put(4, newT + tmp);
				//tmp = delInContexts.get(4); delInContexts.put(4, delT + tmp);
			} 
		}else if ((100000 <= value) && (value < 1000000)){
			if(!qtContexts.containsKey(5)) {qtContexts.put(5, 1);triplesInContexts.put(5,value);changesInContexts.put(5, change);}
			//newInContexts.put(5, newT); delInContexts.put(5, delT);} 
			else{
				int tmp = qtContexts.get(5); qtContexts.put(5, 1 + tmp);
				tmp = triplesInContexts.get(5); triplesInContexts.put(5, value + tmp);
				tmp = changesInContexts.get(5); changesInContexts.put(5, change + tmp);
				//tmp = newInContexts.get(5); newInContexts.put(5, newT + tmp);
				//tmp = delInContexts.get(5); delInContexts.put(5, delT + tmp);
			} 
		}else if ((1000000 <= value) && (value < 10000000)){
			if(!qtContexts.containsKey(6)) {qtContexts.put(6, 1);triplesInContexts.put(6,value);changesInContexts.put(6, change);}
			//newInContexts.put(6, newT); delInContexts.put(6, delT);} 
			else{
				int tmp = qtContexts.get(6); qtContexts.put(6, 1 + tmp);
				tmp = triplesInContexts.get(6); triplesInContexts.put(6, value + tmp);
				tmp = changesInContexts.get(6); changesInContexts.put(6, change + tmp);
				//tmp = newInContexts.get(6); newInContexts.put(6, newT + tmp);
				//tmp = delInContexts.get(6); delInContexts.put(6, delT + tmp);
			} 
		}else {
			if(!qtContexts.containsKey(7)) {qtContexts.put(7, 1);triplesInContexts.put(7,value);changesInContexts.put(7, change);}
			//newInContexts.put(7, newT); delInContexts.put(7, delT);} 
			else{
				int tmp = qtContexts.get(7); qtContexts.put(7, 1 + tmp);
				tmp = triplesInContexts.get(7); triplesInContexts.put(7, value + tmp);
				tmp = changesInContexts.get(7); changesInContexts.put(7, change + tmp);
				//tmp = newInContexts.get(7); newInContexts.put(7, newT + tmp);
				//tmp = delInContexts.get(7); delInContexts.put(7, delT + tmp);
			} 
		}
		
	}
	
	
}
