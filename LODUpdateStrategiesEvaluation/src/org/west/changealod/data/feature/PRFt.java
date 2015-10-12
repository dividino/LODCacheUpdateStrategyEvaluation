package org.west.changealod.data.feature;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.yars.nx.Resource;
import org.west.changealod.analysis.Triple;
import org.west.changealod.data.feature.pr.PRNode;
import org.west.changealod.data.feature.pr.PageRank;
import org.west.changealod.file.LoadVersion;
import org.west.changealod.utility.Constants;

import com.google.common.net.InternetDomainName;


/**
 * Extract context's Page Rank
 * 
 * @author Renata Dividino
 * 
 */


public class PRFt extends Feature{
		
	public PRFt(LoadVersion load){
		super(load);
	}
	
	
		
	public void extractFt(Map<String, Set<String>> hMap, String date, int type, int strategy, int bd){
		logger.info("Extracting page rank");
		Map<String, Set<String>> olinks = new HashMap<String, Set<String>>();
		
		Set<String> cList ;
		if(strategy==0)	cList = load.loadContexts(Constants.getContextPath(date));
		else cList = load.loadContexts(Constants.getIndexPath(date, strategy, type, bd));
		
		
		
		Iterator<String> itCont = cList.iterator();		
		
		while(itCont.hasNext()){
			String context = itCont.next();			
			Set<Triple> triples = new TreeSet<Triple>(load.getTriples(context, type, date, strategy, bd));
			Iterator<Triple> itTriple = triples.iterator();
			while(itTriple.hasNext()){
				Triple t= itTriple.next();				
				URI from = null; URI to = null;	String cfrom = ""; String cto = "";
				
				if ((t.getSubject() instanceof Resource) && (t.getObject() instanceof Resource)) {					
					try {
						from = new URI(((Resource)(t.getSubject())).toString());
						to = new URI(((Resource)(t.getObject())).toString());
						if(from!=null && to!=null){
							cfrom = getPLD(from);
							cto = getPLD(to);					    
							if(cfrom!=null && cto !=null){
								if(!cfrom.equals(cto)){
									if(cList.contains(cfrom)){
										if(!olinks.containsKey(cfrom)) olinks.put(cfrom, new TreeSet<String>());
										olinks.get(cfrom).add(cto);
									}
								}
							}
						}						
					} catch (URISyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
				}				
			}		
			fMap.put(context, 0.0);
		}
	
		
		logger.info("extracting links done");
		List<PRNode> nodes = new ArrayList<PRNode>();
		itCont = olinks.keySet().iterator();
		while(itCont.hasNext()){
			String context = itCont.next();
			nodes.add(new PRNode(context, olinks.get(context)));
		}
		
		logger.info("starting pr");
		 PageRank pr = new PageRank(nodes);
		 pr.calculatePR();
		 
		 Iterator<PRNode> itNodes = pr.getNodeList().iterator();
		 while(itNodes.hasNext()){
		 	 PRNode node = itNodes.next();
		 	 if(cList.contains(node.getName()))
		 		 fMap.put(node.getName(), node.getScore());
		 }
		 logger.info("extracting pr done");
	}
	
	
	private String getPLD(URI context) {
		String host = context.getHost();
		
		if (host != null && InternetDomainName.isValid(host)) {
			InternetDomainName fromFullDomainName = InternetDomainName
					.from(host);
			//InternetDomainName fromTopPrivateDomainName = fromFullDomainName
					//.topPrivateDomain();
			return fromFullDomainName.name();
		}
		return null; //context.toString().substring(0, context.toString().indexOf("#"));
		
		
		/*String cto = null;
		if(to!=null){
			if(to.getFragment()!=null){		
				if(!to.getFragment().isEmpty()){
					cto  = to.toString().replace(to.getFragment(), "");
					if(cto.endsWith("#")) cto = cto.substring(0, cto.length() -1);
				}
				else{
					if(to.toString().lastIndexOf("/") >= 0)
						cto = to.toString().substring(0, to.toString().lastIndexOf("/"));
					else 
						cto = null;
				}
			}
			else{
				if(to.toString().lastIndexOf("/") >= 0)
					cto = to.toString().substring(0, to.toString().lastIndexOf("/"));
				else 
					cto = null;
			}
		}		
		return cto;*/
	}
}	
