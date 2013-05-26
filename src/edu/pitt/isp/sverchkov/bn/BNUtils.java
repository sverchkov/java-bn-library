/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.bn;

import edu.pitt.isp.sverchkov.data.DataTable;
import edu.pitt.isp.sverchkov.data.DataTableImpl;
import java.util.*;

/**
 *
 * @author YUS24
 */
public class BNUtils {
    
    public static <N> List<N> nodesInTopOrder( BayesNet<N,?> net ){
        List<N> result = new ArrayList<>(net.size());
        
        // Get a local representation of the graph
        Map<N,Set<N>> graphMap = new HashMap<>();
        for( N node : net ){
            Set<N> parents = new HashSet<>( net.parents( node ) );
            graphMap.put( node, parents );
        }
        
        // Init parentless set
        Set<N> orphans = new HashSet<>();
        {
            Set<N> removeSet = new HashSet<>();
            for( Map.Entry<N,Set<N>> node : graphMap.entrySet() )
                if( node.getValue().isEmpty() )
                    orphans.add( node.getKey() );
            
            graphMap.keySet().removeAll( removeSet );
        }
        
        // The meat
        while( !orphans.isEmpty() ){
            
            // Get an orphan node
            N node = orphans.iterator().next();
            orphans.remove( node );
            result.add( node );
            
            // Remove the node and its edges from the graph
            graphMap.remove( node );
            for( Map.Entry<N,Set<N>> entry : graphMap.entrySet() ){
                entry.getValue().remove( node );
                if( entry.getValue().isEmpty() ){
                    orphans.add( entry.getKey() );
                }
            }
        }
        
        // Optional sanity check could go here
        
        return result;
    }
    
    public static <N,V> DataTable<N,V> generate( final BayesNet<N,V> net, final int n ){
        return generate( net, n, new Random() );
    }
    
    public static <N,V> DataTable<N,V> generate( final BayesNet<N,V> net, final int n, Random random ){
        
        final List<N> variables = nodesInTopOrder( net );        
        final int m = variables.size();
        final DataTable<N,V> data = new DataTableImpl<>( variables );
        
        for( int i=0; i<n; i++ ){
            List<V> row = new ArrayList<>(m);
            Map<N,V> conditions = new HashMap<>();
            for( N variable : variables ){
                V value = sample( net, variable, conditions, random );
                row.add( value );
                conditions.put( variable, value );
            }
            data.addRow( row );
        }
        return data;
    }
    
    public static <N,V> V sample( final BayesNet<N,V> net, final N node, final Map<N,V> conditions, Random random ){
        
        Map<N,V> parentAssignment = new HashMap<>( conditions );
        parentAssignment.keySet().retainAll( net.parents(node) );
        
        double p = random.nextDouble();
        for( V value : net.values(node) ){
            final double q = net.probability( Collections.singletonMap( node, value), conditions);
            if( p < q )
                return value;
            else
                p -= q;
        }
        
        return null;
    }
}
