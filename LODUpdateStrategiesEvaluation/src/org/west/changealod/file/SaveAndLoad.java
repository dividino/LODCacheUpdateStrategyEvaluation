package org.west.changealod.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.Callback;
import org.semanticweb.yars.nx.parser.NxParser;
import org.semanticweb.yars.stats.InputAnalyser;
import org.semanticweb.yars.util.CallbackNxBufferedWriter;
import org.west.changealod.analysis.Triple;
import org.west.changealod.analysis.callbacks.PLD2TripleCallback;
import org.west.changealod.analysis.callbacks.TripleCallback;

public class SaveAndLoad {

	/**
	 * Save and Load files 
	 * 
	 * @author Renata Dividino
	 * 
	 */
	static Logger logger = Logger.getLogger(SaveAndLoad.class);

	
	public static Map<String, Set<Triple>> loadPLDTripleMap(File file) throws FileNotFoundException, IOException{
		logger.info("Loading data:" + file.getName());		
		
		GZIPInputStream in = new GZIPInputStream(new FileInputStream(file));
		InputAnalyser input = new InputAnalyser(new NxParser(in));
		Callback cb = new PLD2TripleCallback();
		
		
		int size = 0;
		cb.startDocument();
		while (input.hasNext()) {
			cb.processStatement(input.next());
			size++;
			if (size % 500000 == 0) {
				logger.info(file.getPath() + " -> " + size + " statements processed");
			}
			
		}
		cb.endDocument();
		in.close();
		
		//logger.info("Read data done");
		return ((PLD2TripleCallback) cb).getMap();
	}
	
	public static Set<Triple> loadTripleMap(File file) throws FileNotFoundException, IOException{
		logger.info("Loading data:" + file.getPath());		
		
		GZIPInputStream in = new GZIPInputStream(new FileInputStream(file));
		InputAnalyser input = new InputAnalyser(new NxParser(in));
		Callback cb = new TripleCallback();
		
		
		int size = 0;
		cb.startDocument();
		while (input.hasNext()) {
			cb.processStatement(input.next());
			size++;
			if (size % 500000 == 0) {
				logger.info(file.getPath() + " -> " + size + " statements processed");
			}
			
		}
		cb.endDocument();
		
		in.close();
		//logger.info("Read data done");
		return ((TripleCallback) cb).getMap();
	}
	
	public static void saveTripleMap(Set<Triple> triples, String path, String context) throws FileNotFoundException, IOException{
		logger.info("Saving tripleMap: " + path);
		if(path!=null){
			File file = new File(path);
			File outD = new File(file.getParent());
			if (!outD.exists()) {
				outD.mkdirs();
			}
			
			FileOutputStream fileOut = new FileOutputStream(file);
			GZIPOutputStream gOut = new GZIPOutputStream(fileOut);
			//ObjectOutputStream ou = new ObjectOutputStream(gOut);
			    
			OutputStreamWriter converter = new OutputStreamWriter(gOut);
		    Callback cb = new CallbackNxBufferedWriter(new BufferedWriter(converter), true);
			
			cb.startDocument();
			
			Node cont = new org.semanticweb.yars.nx.Resource(context);			
			if(triples!=null){
				if(!triples.isEmpty()){
					Iterator<Triple> itTriple = triples.iterator();
					while(itTriple.hasNext()){
						Triple triple = itTriple.next();
						Node[] nx = {triple.getSubject(), triple.getPredicate(), triple.getObject(), cont };
						cb.processStatement(nx);
					}
				}
			}
			cb.endDocument();
			fileOut.close();
			gOut.close();
			converter.close();
		}	
		
		//logger.info("Save tripleMap done");
	}	
		
	
	
	
	
	@SuppressWarnings("unchecked")
	public static Map<String, Set<String>> loadHistory(File file) throws FileNotFoundException, IOException, ClassNotFoundException{
		logger.info("Loading history:" + file.getName());
		
		GZIPInputStream gIn = new GZIPInputStream(new FileInputStream(file));
		ObjectInputStream in = new ObjectInputStream(gIn);
		Map<String, Set<String>> hMap = (Map<String,Set<String>>) in.readObject();
		gIn.close();
		in.close();
		
		return hMap;
	}
	
	
	
	public static void saveHistory(Map<String, Set<String>> map , String path) throws IOException{		
		logger.info("Saving history: " + path);
		File outF = new File(path);
		File outD = new File(outF.getParent());
		if (!outD.exists()) {
			outD.mkdirs();
		}
		if(!outF.exists()) outF.createNewFile();


		FileOutputStream fileOut = new FileOutputStream(outF);
		GZIPOutputStream gOut = new GZIPOutputStream(fileOut);
		ObjectOutputStream ou = new ObjectOutputStream(gOut);
		ou.writeObject(map);
		ou.close();
		gOut.close();
		fileOut.close();
		//logger.info("Save history done");
	}
	

	public static void saveFeature(Map<String, Double> map, String path) throws IOException{
		logger.info("Saving feature: " + path);
		File outF = new File(path);
		File outD = new File(outF.getParent());
		if (!outD.exists()) {
			outD.mkdirs();
		}
		if(!outF.exists()) outF.createNewFile();

		FileOutputStream fileOut = new FileOutputStream(outF);
		GZIPOutputStream gOut = new GZIPOutputStream(fileOut);
		ObjectOutputStream ou = new ObjectOutputStream(gOut);
		ou.writeObject(map);
		ou.close();
		gOut.close();
		fileOut.close();

		//logger.info("Save feature done");
	}
	

	@SuppressWarnings("unchecked")
	public static Map<String, Double> loadFeature(File file) throws FileNotFoundException, IOException, ClassNotFoundException{
		logger.info("Loading index:" + file.getPath());
		
		GZIPInputStream gIn = new GZIPInputStream(new FileInputStream(file));
		ObjectInputStream in = new ObjectInputStream(gIn);
		Map<String, Double> map = (Map<String, Double>) in.readObject();
		
		gIn.close();in.close();		
		return map;
	}

	
	public static Map<Integer, Map<String, List<Double>>> loadQuality(File file)  throws FileNotFoundException, IOException, ClassNotFoundException{
		logger.info("Loading index:" + file.getPath());
		
		GZIPInputStream gIn = new GZIPInputStream(new FileInputStream(file));
		ObjectInputStream in = new ObjectInputStream(gIn);
		
		@SuppressWarnings("unchecked")
		Map<Integer, Map<String, List<Double>>> map = (Map<Integer, Map<String, List<Double>>>) in.readObject();
		gIn.close();
		in.close();
		return map;
	}
	
	public static void saveQuality(String path, double value) throws IOException{		
		
		logger.info("Saving");
		File outF = new File(path);
		File outD = new File(outF.getParent());
		if (!outD.exists()) {
			outD.mkdirs();
		}
		if(!outF.exists()) outF.createNewFile();
		
		FileOutputStream fileOut = new FileOutputStream(outF);
		GZIPOutputStream gOut = new GZIPOutputStream(fileOut);
		ObjectOutputStream ou = new ObjectOutputStream(gOut);
		ou.writeObject(value);
		ou.close();	
		//logger.info("Save done");
	}	
	
	
	public static void saveResultMap(Map<Integer, Map<Integer,Double>> map , String path) throws IOException{		
		logger.info("Saving " + path);
		File outF = new File(path);
		File outD = new File(outF.getParent());
		if (!outD.exists()) {
			outD.mkdirs();
		}
		if(!outF.exists()) outF.createNewFile();


		FileOutputStream fileOut = new FileOutputStream(outF);
		GZIPOutputStream gOut = new GZIPOutputStream(fileOut);
		ObjectOutputStream ou = new ObjectOutputStream(gOut);
		ou.writeObject(map);
		ou.close();	
		//logger.info("Save done");
	}
	
	public static void saveAverageMap(Map<Integer, List<Integer>> map , String path) throws IOException{		
		logger.info("Saving");
		File outF = new File(path);
		File outD = new File(outF.getParent());
		if (!outD.exists()) {
			outD.mkdirs();
		}
		if(!outF.exists()) outF.createNewFile();


		FileOutputStream fileOut = new FileOutputStream(outF);
		GZIPOutputStream gOut = new GZIPOutputStream(fileOut);
		ObjectOutputStream ou = new ObjectOutputStream(gOut);
		ou.writeObject(map);
		ou.close();	
		//logger.info("Save done");
	}
	
	@SuppressWarnings({ "resource", "unchecked" })
	public static Map<Integer, Map<Integer, Double>> loadResultMap(File file) throws IOException, ClassNotFoundException{		
		logger.info("Loading index:" + file.getPath());
		
		GZIPInputStream gIn = new GZIPInputStream(new FileInputStream(file));		
		ObjectInputStream in = new ObjectInputStream(gIn);
		Map<Integer, Map<Integer, Double>> value = ( Map<Integer, Map<Integer, Double>> ) in.readObject();
		gIn.close(); in.close();
		return value;
	}
	
	@SuppressWarnings({ "resource", "unchecked" })
	public static List<Integer> loadStat(File file) throws IOException, ClassNotFoundException{		
		logger.info("Loading index:" + file.getPath());
		
		GZIPInputStream gIn = new GZIPInputStream(new FileInputStream(file));		
		ObjectInputStream in = new ObjectInputStream(gIn);
		List<Integer> value = (List<Integer> ) in.readObject();
		gIn.close(); in.close();
		return value;
	}
	
	@SuppressWarnings("resource")
	public static Double loadPR(File file) throws IOException, ClassNotFoundException{		
		logger.info("Loading index:" + file.getPath());
	
		GZIPInputStream gIn = new GZIPInputStream(new FileInputStream(file));
		ObjectInputStream in = new ObjectInputStream(gIn);
		Double value = (Double) in.readObject();
		gIn.close();in.close();
		return value;
	}
	

	@SuppressWarnings({ "unchecked", "resource" })
	public static Map<Integer, Map<Integer, Double>> loadResultMap(String path) throws FileNotFoundException, IOException, ClassNotFoundException {
		File file = new File(path);
		logger.info("Loading index:" + file.getPath());
		
		GZIPInputStream gIn = new GZIPInputStream(new FileInputStream(file));
		ObjectInputStream in = new ObjectInputStream(gIn);
		return (Map<Integer, Map<Integer, Double>>) in.readObject();
	}

	public static void saveStat(String path, 
			int totalChangedTriples, 
			int totalUpdatedTriples, int totalUpdatedChangedTriples
			) throws IOException {
		
		List<Integer> stat = new ArrayList<Integer>();
		stat.add(totalChangedTriples);
		stat.add(totalUpdatedTriples); 
		stat.add(totalUpdatedChangedTriples);		
		
		File outF = new File(path);// + File.separator + "stat.gz");
		File outD = new File(outF.getParent());
		if (!outD.exists()) {
			outD.mkdirs();
		}
		if(!outF.exists()) outF.createNewFile();

		logger.info("Saving:" + outF.getPath());
		FileOutputStream fileOut = new FileOutputStream(outF);
		GZIPOutputStream gOut = new GZIPOutputStream(fileOut);
		ObjectOutputStream ou = new ObjectOutputStream(gOut);
		ou.writeObject(stat);
		ou.close();	
		logger.info("Save done");		
	}

	
	
	public static void saveStat(String path, 
			int size, int limit, int totalNewTriples, int totalDeletedTriples, int totalChangedTriples, 
			int totalUpdatedTriples, int totalUpdatedNewTriples, int totalUpdatedDeletedTriples, int totalUpdatedChangedTriples, 
			int totalUpdatedContexts, int totalChangedContexts, int totalUpdatedChangedContexts
			) throws IOException {
		
		List<Integer> stat = new ArrayList<Integer>();
		stat.add(size);
		stat.add(limit);
		stat.add(totalNewTriples); stat.add(totalDeletedTriples);
		stat.add(totalChangedTriples);
		stat.add(totalUpdatedTriples); 
		stat.add(totalUpdatedNewTriples);
		stat.add(totalUpdatedDeletedTriples);
		stat.add(totalUpdatedChangedTriples);
		stat.add(totalUpdatedContexts);
		stat.add(totalChangedContexts); 
		stat.add(totalUpdatedChangedContexts);
		
		
		File outF = new File(path);// + File.separator + "stat.gz");
		File outD = new File(outF.getParent());
		if (!outD.exists()) {
			outD.mkdirs();
		}
		if(!outF.exists()) outF.createNewFile();

		logger.info("Saving:" + outF.getPath());
		FileOutputStream fileOut = new FileOutputStream(outF);
		GZIPOutputStream gOut = new GZIPOutputStream(fileOut);
		ObjectOutputStream ou = new ObjectOutputStream(gOut);
		ou.writeObject(stat);
		ou.close();	
		//logger.info("Save done");		
	}

	
	public static void saveStatChanges(String path,
			Map<String, Integer> totalChangedTriplePerContext) throws IOException {
		
		File outF = new File(path);// + File.separator + "statChange.gz");
		File outD = new File(outF.getParent());
		if (!outD.exists()) {
			outD.mkdirs();
		}
		if(!outF.exists()) outF.createNewFile();

		logger.info("Saving:" + outF.getPath());

		FileOutputStream fileOut = new FileOutputStream(outF);
		GZIPOutputStream gOut = new GZIPOutputStream(fileOut);
		ObjectOutputStream ou = new ObjectOutputStream(gOut);
		ou.writeObject(totalChangedTriplePerContext);
		ou.close();	
		//logger.info("Save done");
		
	}
	@SuppressWarnings({ "unchecked", "resource" })
	public static Map<String, Integer> loadStatChanges(File file) throws FileNotFoundException, IOException, ClassNotFoundException{
		logger.info("Loading index:" + file.getPath());
		
		GZIPInputStream gIn = new GZIPInputStream(new FileInputStream(file));
		ObjectInputStream in = new ObjectInputStream(gIn);
		Map<String, Integer> value = (Map<String, Integer>) in.readObject();
		gIn.close();in.close();
		return value;
	}




}

/*	public static void saveTripleMap(Set<Triple> triples, String path, Node context) throws FileNotFoundException, IOException{
		logger.info("Saving tripleMap: " + path);
		if(path!=null){
			File file = new File(path);
			File outD = new File(file.getParent());
			if (!outD.exists()) {
				outD.mkdirs();
			}
			
			FileOutputStream fileOut = new FileOutputStream(file);
			GZIPOutputStream gOut = new GZIPOutputStream(fileOut);
			//ObjectOutputStream ou = new ObjectOutputStream(gOut);
			    
			OutputStreamWriter converter = new OutputStreamWriter(gOut);
		    Callback cb = new CallbackNxBufferedWriter(new BufferedWriter(converter), true);
			
			cb.startDocument();
			
			
			if(triples!=null){
				if(!triples.isEmpty()){
					Iterator<Triple> itTriple = triples.iterator();
					while(itTriple.hasNext()){
						Triple triple = itTriple.next();
						Node[] nx = {triple.getSubject(), triple.getPredicate(), triple.getObject(), context };
						cb.processStatement(nx);
					}
				}
			}
			cb.endDocument();
			fileOut.close();
			gOut.close();
			converter.close();
		}	
		
		//logger.info("Save tripleMap done");
	}*/
