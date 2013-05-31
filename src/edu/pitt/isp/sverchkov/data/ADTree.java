/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An implementation of a static AD tree based on Moore & Lee 1998 (mostly)
 * 
 * Indexing of the variables works backwards:
 * (a_i can't have children a_i ... a_m)
 * ... not that that difference is visible in the API.
 * @author user
 */
public class ADTree<A,V> extends ADTreeHelper{
    
    private final Map<A,Integer> attributeLookup;
    private final List<A> attributes;
    private final List<Map<V,Integer>> valueLookup;
    private final List<List<V>> values;
    private final CountNode root;
    
    public ADTree( DataTable<A,V> data ){
        super( data.columnCount() );
        
        attributeLookup = new HashMap<>();
        
        attributes = new ArrayList<>( data.variables() );
        
        {
            int i=0;
            for( A attribute : attributes )
                attributeLookup.put( attribute, i++ );
        }
        
        valueLookup = new ArrayList<>( m );
        values = new ArrayList<>( m );
        
        for( int i=0; i<m; i++ ){
            valueLookup.add( new HashMap<V,Integer>() );
            values.add( new ArrayList<V>() );
        }
        
        int[][] array = new int[data.rowCount()][m];        
        
        {
            int r = 0;
            for( List<V> row : data ){
                for( int i=0; i<m; i++ ){
                    V value = row.get(i);
                    List<V> vlist = values.get(i);
                    Map<V,Integer> vmap = valueLookup.get(i);
                    if( ! vlist.contains(value) ){
                        vmap.put( value, vlist.size() );
                        vlist.add(value);
                        ++airities[i];
                    }
                    array[r][i] = vmap.get( value );
                }
            }
        }
        
        // Build A-D tree
        root = new CountNode( 0, array );
    }
    
    public int count( Map<A,V> assignment ){
        int[] a = new int[m];
        for( int i=0; i<m; i++ ){
            V value = assignment.get( attributes.get(i) );
            if( null != value )
                a[i] = valueLookup.get(i).get( value );
            else a[i] = -1;
        }
        return count( a, root );
    }    
}
