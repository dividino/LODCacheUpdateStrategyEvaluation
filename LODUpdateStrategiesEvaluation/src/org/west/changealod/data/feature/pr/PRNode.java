package org.west.changealod.data.feature.pr;

import java.io.Serializable;
import java.util.Set;

public class PRNode implements Serializable{
	
	/**
	 * Adapted 	 * 
	 */
    
	private static final long serialVersionUID = 1L;
	private Set<String> links;
    private String name;
    //private Node node;
    private Double score = 0.0;
    private Double old = 0.0;
    
    public PRNode(String name, Set<String> set) {
        this.name = name;
        this.links = set;
    }
    
    public int getTotalLinks() {
        return this.links.size();
    }
    public boolean isLinked(PRNode node) {
        
          for ( String link : this.links )
          {
              if(node.getName().equals(link))
              {
                  return true;
              }
          }
        return false;
    }
    
    public String getName(){
    	return this.name;
    }
    
    public Double getScore(){
    	return this.score;
    }
   
    public void setScore(Double score){
    	this.score = score;
    }
    
    public Double getOldScore(){
    	return this.old;
    }
    
    public void setOldScore(Double old){
    	this.old = old;
    }
}