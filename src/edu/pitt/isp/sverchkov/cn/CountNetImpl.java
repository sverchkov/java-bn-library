/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.cn;

import edu.pitt.isp.sverchkov.bn.BNTools;
import edu.pitt.isp.sverchkov.data.DataTable;
import edu.pitt.isp.sverchkov.graph.DAG;
import edu.pitt.isp.sverchkov.graph.GraphTools;
import java.util.*;

/**
 *
 * @author YUS24
 */
public class CountNetImpl<N,V> implements CountNet<N,V> {
    
    private Map<N,Node> nodes;
    
    public CountNetImpl( DataTable<N,V> data, DAG<N> structure ){
        nodes = new LinkedHashMap<>();
        for( N node : GraphTools.nodesInTopOrder(structure) ){
            Node n = new Node( node, structure.parents( node ) );
            //
            nodes.put(node, n);
        }
    }
    
    @Override
    public Collection<V> values(N node) {
        return nodes.get( node ).counts.keySet();
    }

    @Override
    public int count(Map<N, V> outcomes, Map<N, V> conditions) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int size() {
        return nodes.size();
    }

    @Override
    public Collection<N> parents(N node) {
        return nodes.get(node).parents;
    }

    @Override
    public Iterator<N> iterator() {
        return nodes.keySet().iterator();
    }
    
    private class Node {
        final N name;
        Set<N> parents;
        Map<V,Map<Map<N,V>,Integer>> counts;        
        
        Node( N name, Collection<N> parents ){
            this.name = name;
            this.parents = new HashSet<>( parents );
            counts = new HashMap();
        }
        
        void add( Map<N,V> assignment ){
            final int n = nodes.size();
            for( int i=0; i<n; i++ ){
                V value = assignment.get( name );
                Map<Map<N,V>,Integer> countMap = counts.get( value );
                Map<N,V> parentAssignment = new HashMap<>(assignment);
                parentAssignment.keySet().retainAll(parents);
                if( null == countMap ){
                    countMap = new HashMap<>();
                    counts.put( value, countMap );
                }
                Integer count = countMap.get( parentAssignment );
                if( null == count || count == 0 )
                    countMap.put( parentAssignment, 1 );
                else
                    countMap.put( parentAssignment, count+1 );
            }
        }
    }
}
