/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.cn;

import edu.pitt.isp.sverchkov.graph.DAG;
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author YUS24
 */
public interface CountNet<N,V> extends DAG<N> {
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
    int count( Map<N,V> outcomes, Map<N,V> conditions );
}
