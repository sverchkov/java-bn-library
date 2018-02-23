/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.graph;

import java.util.Collection;

/**
 *
 * @author YUS24
 */
public interface DAG<N> extends Iterable<N> {
    
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

}
