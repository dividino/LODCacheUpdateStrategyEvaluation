package org.changealod.main.stat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.west.changealod.analysis.Triple;
import org.west.changealod.file.LoadVersion;
import org.west.changealod.file.SaveAndLoad;
import org.west.changealod.utility.Constants;
import com.google.common.collect.Sets;


/**
 *  
 * @author Renata Dividino
 * 
 */

public class DatasourceChangeDistributionInVersions {	
	
	static Logger logger = Logger.getLogger(DatasourceChangeDistributionInVersions.class);	
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws ClassNotFoundException 
	 * @throws ParseException 
	 */
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException, ParseException {	
		
		LoadVersion load = new LoadVersion();
		//logging
		BasicConfigurator.configure();		
		WriterAppender fh = new WriterAppender(new SimpleLayout(), new FileOutputStream(Constants.geLogPath()));
        logger.addAppender(fh);
        logger.setLevel(Level.INFO);
        
        Map<String, Double> dMap = new HashMap<String, Double>();
        Map<Double, Double> freqMap = new HashMap<Double, Double>();
        
		//set array with all dates(snapshots) of the dataset
		Constants.setDataArray(load.getDates(Constants.getContextPath()));
		Iterator<String> itDates = Constants.getDateArray().iterator();
		
		if(itDates.hasNext()){
					
			String date_v1 = itDates.next();
			Set<String> set_v1 = load.loadContexts(Constants.getContextPath(date_v1));
			
			Iterator<String> it = set_v1.iterator();
			while(it.hasNext()){
				String context = it.next();
				dMap.put(context, 0.0);				
			}
		
			do{
				String date_v2 = itDates.next();			
				Set<String> set_v2 = load.loadContexts(Constants.getContextPath(date_v2));
				
				Map<String, Boolean> changeMap = compare(set_v1,date_v1,set_v2, date_v2);
				
				//List<String> removal = new ArrayList<String>();
				it = dMap.keySet().iterator();
				while(it.hasNext()){
					String context = it.next();
					if(changeMap.containsKey(context)){
						Double value = dMap.get(context);
						if(changeMap.get(context)) dMap.put(context, value + 1);											
					}
					//else removal.add(context);
				}
				
				//it = removal.iterator();
				//while(it.hasNext()){
					//dMap.remove(it.next());
				//}
			}while(itDates.hasNext());
		
			
			Iterator<String> itCont = dMap.keySet().iterator();
			while(itCont.hasNext()){
				String context = itCont.next();							
				Double value = dMap.get(context);
				double freq = value/Constants.getDateArray().size();
				
					
				if(!freqMap.containsKey(freq))
					freqMap.put(freq, 0.0);
				freqMap.put(freq, freqMap.get(freq) + 1);
			}
			
			
			String path3 = Constants.evalPath + File.separator + "changeDist.csv";
			writeDistribution(path3, Constants.getDateArray().size(), dMap);
			String path4 = Constants.evalPath + File.separator + "freqDist.csv";
			writeDistribution(path4, freqMap);
		}		
		
	}

	private static void writeDistribution(String path,
			Map<Double, Double> map) throws IOException {
		File f = new File(path);
		File outD = new File(f.getParent());
		if (!outD.exists()) {
			outD.mkdirs();
		}
		if(!f.exists()) f.createNewFile();
		
		FileWriter out = new FileWriter(f);
		out.append("Change-Frequency; NumberOfContexts\n");
		
		Iterator<Double> it = map.keySet().iterator();
		while(it.hasNext()){
			Double freq = it.next();
			out.append(freq.toString().replace(".", ",") + ";");			
			out.append(map.get(freq).toString().replace(".", ",")+ "\n");			
		}
		
		out.flush();
		out.close();
		
	}

	private static void writeDistribution(String path, int size,
			Map<String, Double> map) throws IOException {

		File f = new File(path);
		File outD = new File(f.getParent());
		if (!outD.exists()) {
			outD.mkdirs();
		}
		if(!f.exists()) f.createNewFile();
		
		FileWriter out = new FileWriter(f);
		out.append("Context; Change-Frequency\n");
		
		Iterator<String> itCont = map.keySet().iterator();
		while(itCont.hasNext()){
			String context = itCont.next();
			out.append(context + ";");			
			double value = map.get(context);						
			out.append(Double.toString(value).replace(".", ",") + "\n");			
		}
		
		out.flush();
		out.close();
	}

	private static Map<String, Boolean> compare(Set<String> set1, String date1, Set<String> set2, String date2) throws FileNotFoundException, IOException {		  
		 
		Set<String> maintained = Sets.intersection(set1, set2);
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		
		
		Iterator<String> it = maintained.iterator();
		while(it.hasNext()){
			String context = it.next();
			
			Set<Triple> cont1 = SaveAndLoad.loadTripleMap(new File(Constants.getContextPath(date1, context)));
			Set<Triple> cont2 = SaveAndLoad.loadTripleMap(new File(Constants.getContextPath(date2, context)));
			if(cont1.size() == cont2.size()){
				if(Sets.intersection(cont1, cont2).size() == Sets.union(cont1, cont2).size()){
					map.put(context, false);
				}else{
					map.put(context, true);
				}		
			}else{
				map.put(context, true);
			}
		}
		
		
		return map;
	}
}

