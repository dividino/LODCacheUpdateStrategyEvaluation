package org.west.changealod.data.feature.pr;

import java.util.List;

/**
 * Page Rank adapted  * 
 * 
 */

public class PageRank {
    public List<PRNode> nodeList;
    private static final double tDelta = 0.001;
    private double tPropability = 0.85;

    public PageRank(List<PRNode> nodeList) {
        this.nodeList = nodeList;
    }
    
    public List<PRNode> getNodeList(){
    	return nodeList;
    }
    
    public void calculatePR() {        
        init();
        while(!hasTerminatingAccuracy())
        {
            iteratePagerank();
        }       
    } 
    
    
    private boolean hasTerminatingAccuracy() {
        double absSumChangesWithLastIteration = 0;
          for ( PRNode node : nodeList )
          {
              absSumChangesWithLastIteration += 
            		  Math.abs(node.getScore() - node.getOldScore());
          }
        if(absSumChangesWithLastIteration < tDelta)
        {
            return true;
        }
        return false;
    }
    private void init() {
      double score = 1.0 / nodeList.size();
      for (PRNode node : nodeList )
      {
          node.setScore(score);
      }
    }

    private void iteratePagerank() {
      for ( PRNode node : nodeList )
      {
          node.setOldScore(node.getScore());
          double x = this.getTeleportPRValue(node);
          double y = this.getPagesPRValue(node);
          node.setScore(x + y);
      }
    }
    private double getTeleportPRValue(PRNode node) {
        return (1.0 - tPropability)/getTotalNodes();
    }

    private double getPagesPRValue(PRNode node) {
        double linkedPRValue = getLinkedPRValue(node);
        return tPropability * linkedPRValue;
    }

    private double getLinkedPRValue(PRNode node) {
      double linkedPRValue = 0;
      for (PRNode nd : nodeList )
      {
          if(nd.isLinked(node))
          {
              linkedPRValue += nd.getOldScore() / (double) nd.getTotalLinks();
          }
      }
      return linkedPRValue;
    }    

    
    private int getTotalNodes() {
        return nodeList.size();
    }
    
    /*private void printPageRankSum() {
        double sumPagerank = 0;
          for ( Knoten andereKnoten : this.knotenList )
          {
              sumPagerank += andereKnoten.pageRankNew;
          }
        System.out.println("Summe Pagerank:" + sumPagerank);
    }
    
    private double getNonLinkedPRValue(PRNode node) {
          double nonLinkedPRValue = 0;
          for ( PRNode nd : nodeList )
          {
              if(nd.hasNoOutgoingLinks())
              {
                  nonLinkedPagerankValue += andereKnoten.pageRankOld / this.getAnzahlKnoten();
              }
          }
          //System.out.println("nonLinkedPagerankValue: " + nonLinkedPagerankValue);
        return nonLinkedPagerankValue;
    }*/
}
