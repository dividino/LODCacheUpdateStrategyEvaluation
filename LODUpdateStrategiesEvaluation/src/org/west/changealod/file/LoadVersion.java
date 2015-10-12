package org.west.changealod.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.west.changealod.analysis.Triple;
import org.west.changealod.data.History;
import org.west.changealod.utility.Constants;



/**
 * Auxiliar functions:  read index/data files
 * 
 * @author Renata Dividino
 * 
 */
public class LoadVersion{
	
	Logger logger = Logger.getLogger(LoadVersion.class);
	String myDate = "";
	
	Map<String, Set<Triple>> contextTriples = new HashMap<String, Set<Triple>>();
	
	public void setDate(String date){
		this.myDate = date;
		contextTriples.clear();
	}
	public Set<String> getDates(String path){
		Set<String> dates = new TreeSet<String>();
		File inDir = new File(path);
		for (File dir : inDir.listFiles()) {
			if (dir.isDirectory()) {
				String version = dir.getName();
				dates.add(version);
			}
		}
		return dates;
	}	
	
	public Set<String> loadContexts (String path){
		Set<String> contexts = new TreeSet<String>();		
		File dir = new File(path);
		if(dir.exists()){
			for (File f : dir.listFiles()){
				if(f!=null){
					String context = f.getName();
					if(context.contains(".nq.gz")){
						context = context.replace(".nq.gz", "");
						contexts.add(context);
					}
				}
			}
		}
		return contexts;
	}
	
	public Set<String> loadLogs (String path){
		Set<String> contexts = new TreeSet<String>();
		
		File dir = new File(path);			
		for (File f : dir.listFiles()){			
			String context = f.getName();
			if(context.contains(".access.log.gz")){
				context = context.replace(".access.log.gz", "");
				contexts.add(context);
			}
		}
		return contexts;
	}

	
	public Set<Triple> getTriples(String context, Integer type, 
			String date, Integer strategy, Integer bandwidth){	 
		Set<Triple> triples = null;
	    File f;
		try {
			if(strategy == 0){				
				if(date.equals(myDate)){				
					if(!contextTriples.containsKey(context)){
						f = new File(Constants.getContextPath(date, context.toString()));
						if(f.exists()) triples = SaveAndLoad.loadTripleMap(f);
						else triples = new TreeSet<Triple>();
						
						contextTriples.put(context, triples);
					}
					else triples = contextTriples.get(context);
				}else{
					f = new File(Constants.getContextPath(date, context.toString()));
					if(f.exists()) triples = SaveAndLoad.loadTripleMap(f);
					else triples = new TreeSet<Triple>();
				}
			}else{
				f = new File(Constants.getIndexPath(date, context, strategy, type, bandwidth));
				if(f.exists()) 
					triples = SaveAndLoad.loadTripleMap(f);
				else 
					triples = new TreeSet<Triple>();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return triples;	
	}
	
	public void copyData(String date, int strategy, int feature) throws IOException {
		Iterator<String> itDate = Constants.getDateArrayFromVersionUntilDate(Constants.getDataArray().indexOf(date)).iterator();
		Map<String, Set<String>> hMap = new HashMap<String, Set<String>>();
		
		while(itDate.hasNext()){
			String dt = itDate.next();
			//for (int feature = 0 ; feature < Constants.features.length; feature++){				
				int bandwidth_index = 0;					
				while(bandwidth_index < Constants.bandwidths.length){		
					int bandwidth = Constants.bandwidths[bandwidth_index];
					bandwidth_index ++;
					Set<String> cList = loadContexts(Constants.getContextPath(dt));
					
					hMap.put(dt, cList);
					
					File source = new File(Constants.getContextPath(dt));
					File target = new File(Constants.getIndexPath(dt, strategy, feature, bandwidth));
					
					copyDirectory(source, target);
					History.saveHistory(hMap, dt, strategy, feature, bandwidth);
				}
			}
		//}
	}
	
	public void copyDirectory(File sourceLocation , File targetLocation)
		    throws IOException {

		        if (sourceLocation.isDirectory()) {	
		        	if (!targetLocation.exists()) {
		                targetLocation.mkdirs();
		            }
		        }

	            File[] children = sourceLocation.listFiles();
	            for (int i=0; i<children.length; i++) {
	            	String path = targetLocation.getPath() + File.separator + children[i].getName();
	            	File target = new File(path);
	                Files.copy(children[i].toPath(), target.toPath(),StandardCopyOption.REPLACE_EXISTING);
	            }
		        
		}
	
}
	
	