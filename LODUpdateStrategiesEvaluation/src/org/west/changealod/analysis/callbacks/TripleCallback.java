package org.west.changealod.analysis.callbacks;

import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.Callback;
import org.west.changealod.analysis.Triple;

/**
 * A Store Triple Callback stores all Triples
 * 
 * @author André Kramer
 * @author Renata Dividino
 * 
 */
public class TripleCallback implements Callback{

	Set<Triple> map;
	
	public TripleCallback() {
	}

	public Set<Triple> getMap(){
		return map;
	}
	public void startDocument() {
		map = new TreeSet<Triple>();
	}

	
	public void endDocument() {
	}

	
	public void processStatement(Node[] nx) {
		Triple t = new Triple(nx[0], nx[1], nx[2]);
		map.add(t);
	}	
}
