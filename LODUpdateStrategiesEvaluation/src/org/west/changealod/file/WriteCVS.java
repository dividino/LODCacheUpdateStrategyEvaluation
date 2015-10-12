package org.west.changealod.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.atlas.lib.SetUtils;
import org.west.changealod.utility.Constants;


public class WriteCVS {
	
	public static void writeResultMap(Map<Integer, Map<Integer,Double>> map, String path) throws IOException {
		
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
		
		Iterator<Integer> itbds = map.keySet().iterator();
		while(itbds.hasNext()){
			Integer bd = itbds.next();
			out.append(bd.toString() + ";");
			Iterator<Integer> itFt = map.get(bd).keySet().iterator();
			while(itFt.hasNext()){
				int ft = itFt.next();
				out.append(map.get(bd).get(ft).toString().replace(".", ",") + ";");
			}
			out.append("\n");
		}	
		
		out.flush();
		out.close();		
	}
	
	public static void writeUpdateMap(Map<Integer, List<Integer>> map, String path) throws IOException {
		File f = new File(path);
		File outD = new File(f.getParent());
		if (!outD.exists()) {
			outD.mkdirs();
		}
		if(!f.exists()) f.createNewFile();
		
		FileWriter out = new FileWriter(f);
				
		out.append("bandwidth" + ";");
		out.append("size" + ";");
		out.append("limit" + ";");
		out.append("new-triples" + ";");
		out.append("deleted-triples" + ";");
		out.append("changed-triples" + ";");
		out.append("updated-triples" + ";");
		out.append("updated-new-triples" + ";");
		out.append("updated-deleted-triples" + ";");
		out.append("updated-changed-triples" + ";");
		
		out.append("updated-contexts" + ";");
		out.append("changed-contexts" + ";");				
		out.append("changed-contexts-updated" + ";");					
		out.append("\n");
		
		
		
		
		Iterator<Integer> it = map.keySet().iterator();
		while(it.hasNext()){
			Integer key = it.next();
			out.append(key.toString() + ";");
			Iterator<Integer> list = map.get(key).iterator();
			while(list.hasNext()){			
				out.append( list.next().toString().replace(".", ",") + ";");	
			}		
			out.append("\n");
		}
		out.flush();
		out.close();
		
	}
	
	public static void writeFeatureMap(Map<String, Map<Integer,Double>> map, String path) throws IOException {
		
		File f = new File(path);
		File outD = new File(f.getParent());
		if (!outD.exists()) {
			outD.mkdirs();
		}
		if(!f.exists()) f.createNewFile();
		
		FileWriter out = new FileWriter(f);
		
		String key = map.keySet().iterator().next();
		out.append(";");
		Iterator<Integer> itbds = map.get(key).keySet().iterator();
		while(itbds.hasNext()){
			Integer bd = itbds.next();
			out.append(Constants.features[bd] + ";");
		}		
		out.append("\n");
		
		Iterator<String> itCont = map.keySet().iterator();
		while(itCont.hasNext()){
			String cont= itCont.next();
			out.append(cont);
			Iterator<Integer> itFt = map.get(cont).keySet().iterator();
			while(itFt.hasNext()){
				int ft = itFt.next();
				out.append(";" + map.get(cont).get(ft).toString().replace(".", ","));
			}
			out.append("\n");
		}	
		
		out.flush();
		out.close();		
	}

	public static void writeContextMap(Map<Integer, Set<String>> map,
			String path) throws IOException {
		
		File f = new File(path);
		File outD = new File(f.getParent());
		if (!outD.exists()) {
			outD.mkdirs();
		}
		if(!f.exists()) f.createNewFile();
		
		FileWriter out = new FileWriter(f);
		
		Iterator<Integer> itft = map.keySet().iterator();
		while(itft.hasNext()){
			Integer ft= itft.next();
			out.append(Constants.features[ft]);
			Set<String> contexts = map.get(ft);
			out.append(";" + contexts.size());
			Iterator<String> itCont = contexts.iterator();
			while(itCont.hasNext()){
				String context = itCont.next();
				out.append(";" + context);
			}
			out.append("\n");			
		}	
		
		itft = map.keySet().iterator();
		if(itft.hasNext()){
			Integer ft= itft.next();
			String title = Constants.features[ft];
			Set<String> contexts = map.get(ft);
			while(itft.hasNext()){
				ft= itft.next();
				title = title + "AND" + Constants.features[ft]; 
				out.append(title);
				Set<String> tmpContexts = map.get(ft);
				Set<String> same = SetUtils.intersection(contexts, tmpContexts);
				out.append(";" + same.size());
				Iterator<String> itCont = same.iterator();
				while(itCont.hasNext()){
					String context = itCont.next();
					out.append(";" + context);
				}
				out.append("\n");
			}						
		}	
		
		
		itft = map.keySet().iterator();
		if(itft.hasNext()){
			Integer ft= itft.next();
			String title = Constants.features[ft];
			Set<String> contexts = map.get(ft);
			while(itft.hasNext()){
				ft= itft.next();
				title = title + "ANDNOT" + Constants.features[ft]; 
				out.append(title);
				Set<String> tmpContexts = map.get(ft);
				Set<String> same = SetUtils.difference(contexts, tmpContexts);
				out.append(";" + same.size());
				Iterator<String> itCont = same.iterator();
				while(itCont.hasNext()){
					String context = itCont.next();
					out.append(";" + context);
				}
				out.append("\n");
			}						
		}	
		
		
		out.flush();
		out.close();			
	}

	
}
