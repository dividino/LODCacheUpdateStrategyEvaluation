package org.west.changealod.analysis;

import org.semanticweb.yars.nx.Node;

/**
 * A Triple stores 3 Nodes <subject, predicate, object>
 * 
 * @author Renata Dividino
 * @author André Kramer
 * 
 */
public class Triple implements Comparable<Object>{

	

	public Node subject;
	public Node predicate;
	public Node object;

	public Triple(Node s, Node p, Node o) {
		this.subject = s;
		this.predicate = p;
		this.object = o;
	}

	public Node getSubject(){
		return this.subject;
	}
	
	public Node getPredicate(){
		return this.predicate;
	}
	
	public Node getObject(){
		return this.object;
	}
	public String toString() {
		return subject.toN3() + " - " + predicate.toN3() + " - " + object.toN3();
	}

	 @Override
     public boolean equals(Object t){
        boolean sameSame = false;

        if (t != null && t instanceof Triple)
        {
            sameSame = ((Triple)t).subject.equals(this.subject)
    				&& ((Triple)t).predicate.equals(this.predicate)
    				&& ((Triple)t).object.equals(this.object);
        }

        return sameSame;
	   }

	@Override
	public int compareTo(Object t) {
		int same= 0;

		int sub = ((Triple)t).subject.compareTo(this.subject);
		int pred = ((Triple)t).predicate.compareTo(this.predicate);
		int obj = ((Triple)t).object.compareTo(this.object);
        
		if (t != null && t instanceof Triple){
        	if(sub==0){
        		if(pred==0){
        			if(obj == 0){
        				same = 0;
        			}else same = obj;
        		}
        		else same = pred;
        	}
        	else same = sub;
        }        
		return same;
	   }

	 
		
}	
