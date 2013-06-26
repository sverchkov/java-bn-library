/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.bn;

import edu.pitt.isp.sverchkov.graph.DAG;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author YUS24
 */
public interface BayesNet<N,V> extends DAG<N> {
    
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
    
    /**
     * Returns the distribution P( nodes | conditions )
     * @param nodes A list of nodes (outcomes)
     * @param conditions A node-value assignment of conditions as a map
     * @return P( nodes | conditions ) as a map of lists to doubles where each
     * list is a unique possible assignment of 'nodes' to values
     */
    Map<? extends List<? extends V>, Double> probabilities( List<? extends N> nodes, Map<? extends N, ? extends V> conditions);
}
