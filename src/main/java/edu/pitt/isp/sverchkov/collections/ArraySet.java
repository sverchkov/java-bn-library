/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.collections;

import java.io.Serializable;
import java.util.*;

/**
 * Sorted array-based implementation of set
 * @author YUS24
 */
public class ArraySet<T> extends AbstractSet<T> implements Serializable {
    
    private final Comparator<? super T> comp;
    private final ArrayList<T> contents;
    
    public ArraySet( Collection<? extends T> things, Comparator<? super T> comparator){
        contents = new ArrayList<>( things );
        comp = comparator;
        Collections.sort(contents, comp);
    }

    @Override
    public Iterator<T> iterator() {
        return new ArraySetIterator();
    }

    @Override
    public int size() {
        return contents.size();
    }
    
    @Override
    public boolean add(T thing){
        int index = indexOf(thing);
        if( index < 0 ){
            contents.add(-index-1, thing);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean contains(Object o){
        return indexOf( (T)o ) >= 0;
    }
    
    public int indexOf( T thing ){
        return Collections.binarySearch(contents, thing, comp);
    }
    
    public T get( int index ){
        return contents.get(index);
    }
    
    private class ArraySetIterator implements Iterator<T>{
        private int index;
        
        ArraySetIterator(){
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return index < contents.size();
        }

        @Override
        public T next() {
            return contents.get(index++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }
}
