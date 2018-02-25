/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.collections;

import java.io.Serializable;
import java.util.*;

/**
 * Sorted-array-based implementation of a map
 * @author YUS24
 */
public class ArrayMap<Key extends Comparable<Key>,Value> extends AbstractMap<Key,Value> implements Serializable {
    
    private final Comparator<Map.Entry<Key,Value>> keyComp = new keyComparator();

    private class keyComparator implements Comparator<Map.Entry<Key,Value>>, Serializable {
        @Override
        public int compare(Entry<Key, Value> o1, Entry<Key, Value> o2) {
            return o1.getKey().compareTo(o2.getKey());
        }
    };
    
    public final ArraySet<Map.Entry<Key,Value>> entries;
    
    public ArrayMap(){
        entries = new ArraySet<>( Collections.EMPTY_SET, keyComp );
    };
    
    public Map.Entry<Key,Value> get( int i ){
        return entries.get(i);
    }
    
    @Override
    public Value put( Key k, Value v ){
        Map.Entry<Key,Value> newEntry = new AbstractMap.SimpleEntry<>(k,v);
        int index = entries.indexOf(newEntry);
        if( index >= 0 ){
            Map.Entry<Key,Value> mapEntry = entries.get(index);
            Value returnVal = mapEntry.getValue();
            mapEntry.setValue(v);
            return returnVal;
        }//else:
        entries.add(newEntry);
        return null;
    }

    @Override
    public Set<Map.Entry<Key,Value>> entrySet() {
        return entries;
    }    
}
