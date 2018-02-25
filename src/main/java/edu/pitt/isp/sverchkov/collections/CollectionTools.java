/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.collections;

import java.util.*;

/**
 *
 * @author YUS24
 */
public class CollectionTools {
    public static <K,V> Map<K,V> immutableMapUnion( final Map<? extends K,? extends V> ... ms ){
        ImmutableLinkedListMap<K,V> result = ImmutableLinkedListMap.EMPTY;
        for( Map<? extends K, ? extends V> m : ms ){
            result = result.union(m);
        }
        return result;
    }

    public static <K,V> Map<K,V> mutableMapUnion( Map<? extends K,? extends V> ... ms ){
        Map<K,V> result = null;
        for( Map<? extends K, ? extends V> m : ms ){
            if( null != m && !m.isEmpty() ){
                if( null == result ) result = new HashMap<>(m);
                else result.putAll(m);
            }
        }
        return null == result ? Collections.EMPTY_MAP : result;
    }

    /**
     * Determines whether the "superset" map is a superset of the "subset" map. E.g. {a->1, b->2} is a superset of {a->1} but not of {a->1, b->2, c->3} or {a->2}.
     * @param <Key>
     * @param <Value>
     * @param superset
     * @param subset
     * @return 
     */
    public static <Key,Value> boolean isSubset( Map<Key,Value> superset, Map<Key,Value> subset ){
        for( Map.Entry<Key,Value> entry : subset.entrySet() )
            if( !Objects.equals(entry.getValue(), superset.get(entry.getKey())))
                return false;
        return true;
    }
    
    public static <Key,Value> ArrayList<Value> applyMap( Map<? extends Key, ? extends Value> map, Key[] keys ){
        ArrayList<Value> result = new ArrayList<>();
        for( Key k : keys ) result.add( map.get(k) );
        return result;
    }

    public static <Key,Value> ArrayList<Value> applyMap( Map<? extends Key,Value> map, Iterable<? extends Key> keys ){
        ArrayList<Value> result = new ArrayList<>();
        for( Key k : keys ) result.add( map.get(k) );
        return result;
    }
    
    public static <Key,Value> Map<Key,Value> zipToMap( final Iterable<? extends Key> keys, final Iterable<? extends Value> values ){
        return zipToMap( keys, values, new HashMap<Key,Value>(), 0, false );
    }
    
    public static <Key,Value> Map<Key,Value> zipToMap( final Iterable<? extends Key> keys, final Iterable<? extends Value> values, int size ){
        return zipToMap( keys, values, new HashMap<Key,Value>(size), size, true );
    }
    
    private static <Key,Value> Map<Key,Value> zipToMap( final Iterable<? extends Key> keys, final Iterable<? extends Value> values, final Map<Key,Value> result, final int size, final boolean limitToSize ){
        int i = 0;
        Iterator<? extends Key> keyI = keys.iterator();
        Iterator<? extends Value> valueI = values.iterator();
        while( (!limitToSize || i++ < size) && keyI.hasNext() && valueI.hasNext() )
            result.put(keyI.next(), valueI.next() );
        return result;
    }

    public static <E> boolean haveCommon(Set<E> set1, Set<E> set2) {
        for (E e : set1) if (set2.contains(e)) return true;
        return false;
    }
}
