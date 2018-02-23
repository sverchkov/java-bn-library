/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.cn;

import edu.pitt.isp.sverchkov.graph.ValueDAG;
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author YUS24
 */
public interface CountNet<N,V> extends ValueDAG<N,V> {
    /**
     * Returns the count distribution of a node N subject to the conditions
     * @param node A node
     * @param conditions A node-value assignment of conditions as a map
     * @return Map: Value : Count( node = value, conditions )
     */
    Map<V, Integer> counts( N node, Map<N,V> conditions );
}
