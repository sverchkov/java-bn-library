/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.data;

import java.util.*;

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
    private final List<VHelper> values;
    private final CountNode root;
    
    public ADTree( DataTable<A,V> data ){
        super( data.columnCount() );
        
        {
            final Map<A, Integer> attrLookup = new HashMap<>();
            final List<A> attrs = new ArrayList<>( data.variables() );
        
            int i=0;
            for( A attribute : attrs )
                attrLookup.put( attribute, i++ );
            
            // Set immutable
            attributeLookup = Collections.unmodifiableMap( attrLookup );
            attributes = Collections.unmodifiableList( attrs );
        }
        
        int[][] array = new int[data.rowCount()][m];        
        
        {
            final List<VHelper> v = new ArrayList<>( m );

            for( int i=0; i<m; i++ )
                v.add( new VHelper() );
        
            int r = 0;
            for( List<V> row : data ){
                for( int i=0; i<m; i++ ){
                    V value = row.get(i);
                    List<V> vlist = v.get(i).list;
                    Map<V,Integer> vmap = v.get(i).map;
                    if( ! vlist.contains(value) ){
                        vmap.put( value, vlist.size() );
                        vlist.add(value);
                        ++airities[i];
                    }
                    array[r][i] = vmap.get( value );
                }
            }
            
            // Set immutable
            for( ListIterator<VHelper> iter = v.listIterator(); iter.hasNext(); ){
                final VHelper h = iter.next();
                iter.set( new VHelper(
                        Collections.unmodifiableList( h.list ),
                        Collections.unmodifiableMap( h.map ) ) );
            }
            
            values = Collections.unmodifiableList( v );
        }
        
        // Build A-D tree
        root = new CountNode( m, array );
    }
    
    public List<V> values( A attribute ){
        return values.get( attributeLookup.get( attribute ) ).list;
    }
    
    public int count( Map<A,V> assignment ){
        final int[] a = new int[m];
        for( int i=0; i<m; i++ ){
            V value = assignment.get( attributes.get(i) );
            if( null != value )
                a[i] = values.get(i).map.get( value );
            else a[i] = -1;
        }
        return count( a, root );
    }
    
    public Map<V, Integer> counts( A attribute, Map<A,V> assignment ){
        
        // Translation to integers
        final int[] a = new int[m];
        for( int i=0; i<m; i++ ){
            V value = assignment.get( attributes.get(i) );
            if( null != value )
                a[i] = values.get(i).map.get( value );
            else a[i] = -1;
        }
        final int attr = attributeLookup.get( attribute );
        
        // Computation
        final int[] counts = counts( attr, a, root );
        
        // Translation out of integers
        final List<V> vlist = values.get(attr).list;
        final Map<V,Integer> result = new HashMap<>( vlist.size() );
        for( int i=0; i<airities[attr]; i++ )
            result.put( vlist.get(i), counts[i] );
        
        return result;
    }
    
    private class VHelper{
        private final List<V> list;
        private final Map<V,Integer> map;
        private VHelper(){
            list = new ArrayList();
            map = new HashMap();
        }
        private VHelper( List<V> list, Map<V,Integer> map ){
            this.list = list;
            this.map = map;
        }
    }
}
