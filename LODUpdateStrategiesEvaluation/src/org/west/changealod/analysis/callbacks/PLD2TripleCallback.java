package org.west.changealod.analysis.callbacks;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.Callback;
import org.west.changealod.analysis.Triple;

import com.google.common.net.InternetDomainName;

/**
 * A Store Triple Callback stores all Triples belonging to a subject in a
 * TripleMap
 * 
 * @author Renata Dividino
 * 
 */
public class PLD2TripleCallback implements Callback{

	Map<String, Set<Triple>> map = new HashMap<String, Set<Triple>>();
	
	public PLD2TripleCallback() {
	}

	public Map<String, Set<Triple>> getMap(){
		return map;
	}
	public void startDocument() {
		map = new HashMap<String, Set<Triple>>();
	}

	
	public void endDocument() {
	}

	
	public void processStatement(Node[] nx) {
		Node cont = nx[3];		
		URI contextURI;
		try {
			contextURI = new URI(cont.toString());
			if (contextURI.getHost() != null && InternetDomainName.isValid(contextURI.getHost())) {
				InternetDomainName fromFullDomainName = InternetDomainName
						.from(contextURI.getHost());
				
				String pld = fromFullDomainName.name();
				
				if(!map.containsKey(pld))
					map.put(pld, new TreeSet<Triple>());
				
				Triple t = new Triple(nx[0], nx[1], nx[2]);
				map.get(pld).add(t);
			}				
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}		
	}
	
}
