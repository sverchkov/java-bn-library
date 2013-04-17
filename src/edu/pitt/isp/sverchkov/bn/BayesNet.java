/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.bn;

import java.util.Collection;
import java.util.Map;

/**
 *
 * @author YUS24
 */
public interface BayesNet<N,V> extends Iterable<N> {

    /**
     * @return the number of nodes in the network.
     */
    int size();
    
    /* * No need for a method to return the nodes since using itarable
     * @return the nodes in the network.
     */
    //Iterable<N> nodes();
    
    /**
     * @param node
     * @return The parents of the node.
     */
    Collection<N> parents( N node );
    
    /**
     * @param node
     * @return The values the node can take.
     */
    Collection<V> values( N node );
    
    /**
     * Returns the probability of the outcomes given the conditions
     * @param outcomes A node-value assignment of outcomes as a map
     * @param conditions A node-value assignment of conditions as a map
     * @return P( outcomes | conditions );
     */
    double probability( Map<N,V> outcomes, Map<N,V> conditions );
}
