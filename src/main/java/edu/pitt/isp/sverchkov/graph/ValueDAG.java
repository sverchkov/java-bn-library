/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.graph;

import edu.pitt.isp.sverchkov.combinatorics.Assignments;
import java.util.Collection;

/**
 *
 * @author YUS24
 */
public interface ValueDAG<Node,Value> extends DAG<Node> {
    /**
     * @param node
     * @return The values the node can take.
     */
    Collection<Value> values( Node node );
    
    /**
     * @param node This node.
     * @return The iterator over assignments of this node's parents to values.
     */
    Assignments<Node,Value> parentAssignments( Node node );
}
