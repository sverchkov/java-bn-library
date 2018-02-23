package edu.pitt.isp.sverchkov.combinatorics;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A tool for iterating over all possible key-value assignments given a set
 * of values for each key.
 *
 * @author YUS24
 */
public class Assignments<K,V> implements Iterable<Map<K,V>> {

    private List<K> keys;
    private List<List<V>> values;

    public Assignments( K[] keys, V[][] values ){
        this.keys = Collections.unmodifiableList( Arrays.asList(keys) );
        
        List<List<V>> tmp = new ArrayList<>(values.length);
        for( V[] valueArray : values )
            tmp.add( Collections.unmodifiableList( Arrays.asList(valueArray) ) );
        
        this.values = Collections.unmodifiableList(tmp);
    }

    public Assignments( List<K> keys, List<? extends List<? extends V>> values ){

        this.keys = Collections.unmodifiableList( new ArrayList<>(keys) );

        List<List<V>> tmp = new ArrayList<>(values.size());
        for( List<? extends V> valueList : values )
            tmp.add( Collections.unmodifiableList( new ArrayList<>(valueList)) );
        this.values = Collections.unmodifiableList(tmp);
    }

    public Assignments( Map<K,? extends Collection<? extends V>> setMap ){
        keys = new ArrayList<>();
        values = new ArrayList<>();
        
        for( Map.Entry<K, ? extends Collection<? extends V>> entry : setMap.entrySet() ){
            keys.add( entry.getKey() );
            values.add( Collections.unmodifiableList( new ArrayList<>(entry.getValue()) ) );
        }

        keys = Collections.unmodifiableList(keys);
        values = Collections.unmodifiableList(values);
    }

    @Override
    public Iterator<Map<K, V>> iterator() {
        return new AIterator();
    }

    private class AIterator implements Iterator<Map<K,V>> {

        private int[] indeces;
        private boolean valid;

        private AIterator(){
            indeces = new int[keys.size()];
            Arrays.fill(indeces, 0);
            valid = true;
        }

        @Override
        public boolean hasNext() {
            return valid;
        }

        @Override
        public Map<K, V> next() {
            if( !valid ) return null;

            Map<K,V> result = new HashMap<>();
            // Record result
            for( int bit = 0; bit < indeces.length; bit++ )
                result.put( keys.get(bit), values.get(bit).get(indeces[bit]) );

            // Update counter
            int bit;
            for( bit = 0; bit < indeces.length; bit++ )
                if( ++indeces[bit] < values.get(bit).size() )
                    break;
                else
                    indeces[bit] = 0;

            // Check if an overflow occured
            valid = bit < indeces.length;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove not supported for this iterator.");
        }
    }
}
