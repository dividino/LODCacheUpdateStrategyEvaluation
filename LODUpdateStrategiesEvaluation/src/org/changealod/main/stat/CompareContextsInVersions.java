package org.changealod.main.stat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.west.changealod.file.LoadVersion;
import org.west.changealod.file.SaveAndLoad;
import org.west.changealod.utility.Constants;

import com.google.common.collect.Sets;


/**
 * Compare version : check for new/deleted/maintained contexts
 * 
 * @author Renata Dividino
 * 
 */

public class CompareContextsInVersions {	
	
	static Logger logger = Logger.getLogger(CompareContextsInVersions.class);	
	
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
        
      //set array with all dates(snapshots) of the dataset
		Constants.setDataArray(load.getDates(Constants.getContextPath()));
				
				
		Iterator<String> itDates = Constants.getDateArray().iterator();
		
		
		Map<String, List<String>> compareSucc = new HashMap<String, List<String>>();
		Map<String, List<String>> compareV1 = new HashMap<String, List<String>>();
		
		for(int i=0; i<10;i++){itDates.next();};
		if(itDates.hasNext()){
					
			String date = itDates.next();
			Set<String> set = load.loadContexts(Constants.getContextPath(date));
			
			String date_v1 = date;
			Set<String> set_v1 = new TreeSet<String>(set);
			
			do{
				for(int i=0; i<10;i++){itDates.next();};
				String date_v2 = itDates.next();			
				Set<String> set_v2 = load.loadContexts(Constants.getContextPath(date_v2));
				
				compare(set_v1,date_v1,set_v2, date_v2, compareSucc);
				compare(set,date,set_v2, date_v2, compareV1);
				date_v1 = date_v2;
				set_v1 = new TreeSet<String>(set_v2);
				set_v2.clear();
				
			}while(itDates.hasNext());
		
			String path1 = Constants.evalPath + File.separator + "compSuccSummary.csv";
			String path2 = Constants.evalPath + File.separator + "compV1Summary.csv";
			write(path1, compareSucc); write(path2, compareV1);
			
		}		
		
	}

	private static void write(String path, Map<String, List<String>> res) throws IOException {
		
		File f = new File(path);
		File outD = new File(f.getParent());
		if (!outD.exists()) {
			outD.mkdirs();
		}
		if(!f.exists()) f.createNewFile();
		
		FileWriter out = new FileWriter(f);
		out.append("Date;");
		out.append("Size;");
		out.append("Size-Next;");
		out.append("New-Contexts;");
		out.append("Deleted-Contexts;");
		out.append("Maintained-Contexts;");
		out.append("Maintained-and-Changed;");
		out.append("Maintained-and-NotChanged");		
		out.append("\n");
		
		Iterator<String> it = res.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			out.append(key + ";");
			Iterator<String> list = res.get(key).iterator();
			while(list.hasNext()){			
				out.append(list.next() + ";");				
			}		
			out.append("\n");
		}
		out.flush();
		out.close();
	}

	private static void compare(Set<String> set1, String date1, Set<String> set2, String date2, Map<String, List<String>> compMap) throws FileNotFoundException, IOException {		  
		 
		Set<String> maintained = Sets.intersection(set1, set2);
		Integer maintained_contexts = maintained.size();
		Integer new_contexts = Sets.difference(set2, set1).size();
		Integer deleted_contexts = Sets.difference(set1, set2).size();
		
			
		Integer notchanged = 0;
		Iterator<String> it = maintained.iterator();
		while(it.hasNext()){
			String context = it.next();
			
			Set<Triple> cont1 = SaveAndLoad.loadTripleMap(new File(Constants.getContextPath(date1, context)));
			Set<Triple> cont2 = SaveAndLoad.loadTripleMap(new File(Constants.getContextPath(date2, context)));
			
			if(cont1.size() == cont2.size()){
				if(Sets.intersection(cont1, cont2).size() == Sets.union(cont1, cont2).size()){
					notchanged ++;					
				}		
			}
		}
		
		
		Integer changed = maintained_contexts - notchanged;
		
		List<String> res = new ArrayList<String>();
		res.add(Integer.toString(set1.size()));
		res.add(Integer.toString(set2.size()));
		res.add(new_contexts.toString());
		res.add(deleted_contexts.toString());
		res.add(maintained_contexts.toString());
		res.add(changed.toString());
		res.add(notchanged.toString());
		
		compMap.put(date2, res);
		
		
	}
}

