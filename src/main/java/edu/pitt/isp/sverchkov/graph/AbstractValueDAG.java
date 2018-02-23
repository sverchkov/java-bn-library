/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.graph;

import edu.pitt.isp.sverchkov.combinatorics.Assignments;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * An abstract class that implements the parentAssignment method for a ValueDAG
 * @author YUS24
 */
public abstract class AbstractValueDAG<Node,Value> implements ValueDAG<Node,Value>{
    
    protected Map<Node,Assignments<Node,Value>> assignments = new HashMap<>();
    
    @Override
    public Assignments<Node, Value> parentAssignments(Node n) {
        Assignments<Node,Value> result = assignments.get(n);
        if( null == result ){
            Map<Node,Collection<Value>> parentMap = new HashMap<>();
            for( Node parent : parents( n ) )
                parentMap.put( parent, values(parent) );
            result = new Assignments<>( parentMap );
            assignments.put(n, result);
        }
        return result;
    }
}
