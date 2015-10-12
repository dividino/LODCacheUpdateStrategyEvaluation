package org.west.changealod.utility;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class Constants {

	/**
	 * 
	 * 
	 * @author Renata Dividino
	 * 
	 */


	public static Integer[] bandwidths = {0, 1, 2, 3, 4, 5, 10, 15, 20, 40, 60, 80, 100};
	//public static Integer[] bandwidths = {0, 5,15,40};
	public static Integer[] prob = {5,15,40,60,80,95};
	public static String[] strategies = {"SS", "IT", "CS", "SSMix", "ITMix", "CSMix"};
	public static String[] features = {"BiggestOnTop",  "SmallestOnTop", "PageRank", "ChangeRate-J", "ChangeRate-D", "Quantitative","Dynamics-J", "Dynamics-D", "Age", "Compound", "DynamicsH-Jaccard", "DynamicsH-TripleDistance"};
	public static String[] index_models = {"T", "S2T", "ECS2USU", "ECS2CONT"};
	
	private static int versions = 5;
	//public static int versions = 2;
	//public static int versions = 1;
	private static double change = 0.2;	
	private static double age = 10000;
	
	
	/**
	 * Public Paths
	 */

	private static List<String> DateArray = new ArrayList<String>();

	public static String path = "DYLDO";
	
 
	private static String dataPath = path +
		File.separator + "originalData";
	private static String dataHeaderPath = path +
			File.separator + "originalDataHeader";	
	private static String contextPath = path +
			File.separator + "context-split-header";	
	private static String dummyPath = path +		
			File.separator + "dummyData";
	private static String indexPath = path +			
			File.separator + "updated-data";
	public static String evalPath = path +			
			File.separator + "results-updated-data";
	private static String contextIntersectionPath = path +
			File.separator + "context-split-header-intersection";
	
	
	private static String testPath = path +
			File.separator + "test";
	
	//private static String dataName = "data-seedlist.nq.gz";
	private static String headerName = "headers.nx.gz";
	private static String logName = "access.log.gz";
	private static String dataName = "data.nq.gz";
	private static String historyMapName = "history.gz";
	//private static String tripleMapName = "triple.gz";
	
	private static Integer probability= 0;
	private static boolean includeProb = false;
	
	public static String geLogPath(){
		// TODO Auto-generated method stub
		return path + "log.txt";
	}
	
	public static void setProbability(Integer prob){
		probability = prob;
		includeProb = true;
	}
	
	public static void setDataArray(Set<String> dates){
		DateArray = new ArrayList<String>(dates);
		Collections.sort(DateArray);
	}
	public static List<String> getDateArray(){		
		return DateArray;
	}
	
	public static List<String> getDateArray(int init){
		return DateArray.subList(init, DateArray.size()); 
	}
	
	public static List<String> getDateArrayUntilVersion(int init){
		return DateArray.subList(init, init + (versions));
	}
	
	public static List<String> getDateArrayFromVersionUntilDate(int init){
		return DateArray.subList((init - versions + 1), init +1);		
	}
	
	public static String getHeaderPath(){
		return dataHeaderPath;
	}
	
	public static String getHeaderPath(String date){
		if(DateArray.contains(date))
			return dataHeaderPath + File.separator + date + File.separator + headerName;
		else return null;
	}

	public static String getAccessLogPath(String date){
		if(DateArray.contains(date))
			return dataHeaderPath + File.separator + date + File.separator + logName;
		else return null;
	}

	
	public static String getDataPath(String date){
		if(DateArray.contains(date))
			return dataPath + File.separator + date + File.separator + dataName;
		else return null;
	}
	
	public static String getDataHeaderPath(String date){
		if(DateArray.contains(date))
			return dataHeaderPath + File.separator + date + File.separator + dataName;
		else return null;
	}
	
	public static String getContextPath(){
		if(includeProb)
			return contextPath + "-" + probability ;
		else
			return contextPath ;
	}
	
	public static String getContextPath(String date){
		if(DateArray.contains(date))
			if(includeProb)
				return contextPath + "-" + probability + File.separator + date;
			else
				return contextPath + File.separator + date;
		else return null;
	}
	
	public static String getContextIntersectionPath(String date){
		if(DateArray.contains(date))
			return contextIntersectionPath + File.separator + date;
		else return null;
	}
	
	public static String getContextHeaderPath(String date, String context){
		if(DateArray.contains(date))
			return contextPath + File.separator + date + File.separator + 
					context + ".headers.nx.gz";
		else return null;
	}
	
	
	public static String getContextAccessLogPath(String date, String context){
		if(DateArray.contains(date))
			return contextPath + File.separator + date + File.separator + 
					context + ".access.log.gz";
		else return null;
	}
	
	public static String getContextPath(String date, String context){
		if(DateArray.contains(date))			
			if(includeProb)
				return contextPath + "-" + probability + File.separator + date + File.separator + 
						context + ".nq.gz";
			else
				return contextPath + File.separator + date + File.separator + 
					context + ".nq.gz";
		else return null;
	}
	
	
	public static String getIndexPath(int strategy){
		if(includeProb)	
			return Constants.indexPath  + "-" + probability + File.separator + Constants.strategies[strategy];
		else return Constants.indexPath+ File.separator + Constants.strategies[strategy];
	}
		
	public static String getIndexPath(String date, int strategy, int ft, int bd){
		if(includeProb)
			return indexPath  + "-" + probability + File.separator + strategies[strategy] + File.separator + date +
					File.separator + bd + File.separator + features[ft];
		else
			return indexPath  + File.separator + strategies[strategy] + File.separator + date +
				File.separator + bd + File.separator + features[ft];
				
	}
	
	public static String getIndexPath(String date, String context, int strategy, int ft, int bd){
		if(DateArray.contains(date)){
			if(includeProb)
				return indexPath + "-" + probability + File.separator  + strategies[strategy] +  File.separator + date + 
						File.separator +  bd + File.separator + features[ft] 
							+ File.separator  + context + ".nq.gz";
			else
			return indexPath + File.separator  + strategies[strategy] +  File.separator + date + 
					File.separator +  bd + File.separator + features[ft] 
						+ File.separator  + context + ".nq.gz";
		}		
		else return null;
		
		 
	}
	
	public static String getIndexPath(String date, int strategy, int bd){
		if(includeProb)
			return indexPath + "-" + probability + File.separator + strategies[strategy] +  File.separator + date + 
					File.separator + bd ;
		else
			return indexPath + File.separator + strategies[strategy] +  File.separator + date + 
					File.separator + bd ;		
		
	}
	public static String getEvalPath(String date, int strategy, int model){
		if(DateArray.contains(date))
			if(includeProb)
				return evalPath + "-" + probability + File.separator + strategies[strategy] + File.separator + date + 
						File.separator + index_models[model];			
			else return evalPath + File.separator + strategies[strategy] + File.separator + date + 
					File.separator + index_models[model];
		else return null;		
	}
	
	public static String getEvalPath(String date, int strategy){
		if(includeProb)
			return evalPath + "-" + probability + File.separator + strategies[strategy] +File.separator + date ;
		else
			return evalPath + File.separator + strategies[strategy] +File.separator + date ;
	}
		
	public static String getEvalPath(int strategy, int model){		
		if(includeProb)
			return evalPath + "-" + probability + File.separator + strategies[strategy]
					+ File.separator + "AVG" +  File.separator + index_models[model];
		else
			return evalPath + File.separator + strategies[strategy]
					+ File.separator + "AVG" +  File.separator + index_models[model];				
	}
	
	public static String getEvalPath(int strategy, int model, int feature){
		if(includeProb)
			return evalPath + "-" + probability + File.separator + strategies[strategy]
					+ File.separator + "AVG" +  File.separator + index_models[model];
		else
			return evalPath + File.separator + strategies[strategy]
					+ File.separator + "AVG" +  File.separator + index_models[model];				
	}
	
	public static String getEvalPath(int strategy){	
		if(includeProb)
			return evalPath + "-" + probability +File.separator + strategies[strategy] + File.separator + "AVG" ;
		else
			return evalPath +File.separator + strategies[strategy] + File.separator + "AVG" ;				
	}
	
	public static String getHistoryPath(String date, int strategy, int ft, int bd){		
		if(includeProb)
			return indexPath + "-" + probability + File.separator + strategies[strategy] +File.separator + date + 
					File.separator + bd + File.separator + features[ft] + 
					File.separator + historyMapName;
		else
			return indexPath + File.separator + strategies[strategy] +File.separator + date + 
					File.separator + bd + File.separator + features[ft] + 
					File.separator + historyMapName;			
	}
	
	public static String getFeaturePath(String date, int strategy, int ft, int bd){
		if(includeProb)
			return indexPath + "-" + probability + File.separator + strategies[strategy] + File.separator + date + 
					File.separator + bd + File.separator + features[ft] +  
					File.separator +
					features[ft] + ".gz";
		
		else
			return indexPath + File.separator + strategies[strategy] + File.separator + date + 
					File.separator + bd + File.separator + features[ft] +  
					File.separator +
					features[ft] + ".gz";
	}
	
	public static String getTestPath(String date, String context){
		if(DateArray.contains(date))
			if(includeProb)
				return testPath + "-" + probability + File.separator + date + File.separator + context ;//+ ".nq.gz";
			else
				return testPath + File.separator + date + File.separator + context ;//+ ".nq.gz";
		else return null;
	}	
	
	public static String getDummyPath(String date) {
		return dummyPath + File.separator + date + File.separator + dataName;
		
	}
	
	public static String getDummyPath(String date, String context){
		return dummyPath + File.separator + date + File.separator + 
					context;// + ".nq.gz";
	}
	
	public static int getVersions(){
		return versions;
	}

	public static Double getDefaultChangeValue() {
		return change;
	}

	public static Double getDefaultAge() {
		return age;
	}
	
	public static List<String> getDataArray(){
		return DateArray;
	}
	
	public static boolean existsNextDate(String date) {
		int index = DateArray.indexOf(date);
		if(index >= 0 && index < DateArray.size() -1)
			return true;
		else 
			return false;
	}
	
	public static String getNextDate(String date){
		
		int index = DateArray.indexOf(date);
		if(index >= 0 && index < DateArray.size() -1){
			if (DateArray.contains(date)){
				return DateArray.get(index + 1);
			}
		}
		return null;
	}
	public static boolean existsPreviousDate(String date) throws ParseException {
		
		int index = DateArray.lastIndexOf(date);
		if(index > 0)
		 	return true;
		else
			return false;
	}
	
	public static boolean existsPreviousMonth(String date) throws ParseException {
		
		SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd" );
		
		Date dt =  df.parse(date);
		Calendar cDate = Calendar.getInstance();
		cDate.setTime(dt);
		
		int month = cDate.get(Calendar.MONTH);
		int year = cDate.get(Calendar.YEAR);
		
		month = month +1;
		String lastdate;
		if(month <= 9)
			lastdate  = year + "-0" + month + "-";
		else
			lastdate = year + "-" + month + "-";
		
		String tmp="";
		int i = 0;
		while( i <DateArray.size()){
			tmp = DateArray.get(i);
			if (tmp.contains(lastdate))
				i = DateArray.size();
			else i++;
		}
		int index = DateArray.lastIndexOf(tmp);
		if(index > -1)
		 	return true;
		else
			return false;
	}

	public static String getPreviousDate(String date) {
		int index = DateArray.lastIndexOf(date);		
		if(index > 0 && index < DateArray.size())
			index = index -1;
	
		return DateArray.get(index);
	}
	public static String getPreviousDate(String date, int v) {
		int index = DateArray.lastIndexOf(date);		
		if(v!=0 && index >= (v -1) && index < DateArray.size())
				index = index - (v -1);
		
	
		return DateArray.get(index);
	}
	
	public static String getPreviousMonth(String date)throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd" );
		
		Date dt =  df.parse(date);
		Calendar cDate = Calendar.getInstance();
		cDate.setTime(dt);
		
		int month = cDate.get(Calendar.MONTH) +1;
				
		int year = cDate.get(Calendar.YEAR);
				
		String lastdate;
		if(month <= 9)
			lastdate  = year + "-0" + month + "-";
		else
			lastdate = year + "-" + month + "-";
		
		String tmp="";
		int i = 0;
		while( i <DateArray.size()){
			tmp = DateArray.get(i);
			if (tmp.contains(lastdate))
				i = DateArray.size();
			else i++;
		}
		int index = DateArray.lastIndexOf(tmp);
		return DateArray.get(index);
	}
	
	public static String getFeatureName(int ft){
		return features[ft];
	}
	
}
