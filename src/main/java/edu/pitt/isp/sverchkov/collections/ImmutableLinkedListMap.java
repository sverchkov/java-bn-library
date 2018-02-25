/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.collections;

import java.util.*;

/**
 * Linked-list based immutable map implementation. Not efficient for typical map
 * operations, but does save space.
 * @author YUS24
 */
final class ImmutableLinkedListMap<Key, Value> extends AbstractMap<Key, Value> {

    public static final ImmutableLinkedListMap EMPTY = new ImmutableLinkedListMap();
    private final EntrySet<Key,Value> entrySet;
    
    private ImmutableLinkedListMap() {
        entrySet = new EntrySet<>();
    }
    
    private ImmutableLinkedListMap( Node<Key,Value> h ) {
        entrySet = new EntrySet<>( h );
    }
    
    public ImmutableLinkedListMap<Key,Value> union( Map<?extends Key, ?extends Value> toAdd){
        Node<Key,Value> newHead = entrySet.head;
        if( null != toAdd )
            for( Entry<?extends Key, ?extends Value> entry : toAdd.entrySet() )
                if( !containsKey( entry.getKey() ) )
                    newHead = new Node<>( entry.getKey(), entry.getValue(), newHead );
        return newHead == entrySet.head ? this : new ImmutableLinkedListMap<>( newHead );
    }
    
    @Override
    public Set<Entry<Key, Value>> entrySet() {
        return entrySet;
    }
    
    private static final class EntrySet<K,V> extends AbstractSet<Entry<K,V>>{
        private final Node<K,V> head;
        
        private EntrySet(){
            head = null;
        }
        
        private EntrySet( Node<K,V> h ){
            head = h;
        }
        
        @Override
        public Iterator<Entry<K, V>> iterator() {
            return new Iter();
        }

        @Override
        public int size() {
            return null == head ? 0 : head.length;
        }
        
        private final class Iter implements Iterator<Entry<K,V>>{

            Node<K,V> ptr = head;
            
            @Override
            public boolean hasNext() {
                return null != ptr;
            }

            @Override
            public Entry<K, V> next() {
                Entry<K, V> e = ptr;
                ptr = ptr.next;
                return e;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove Not supported in read-only iterator.");
            }
        }
    }
    
    private static final class Node<K,V> implements Entry<K,V>{
        final K key;
        final V value;
        final Node<K,V> next;
        final int length; // Immutability implies constant tail length
        Node( K k, V v, Node<K,V> n ){
            key = k;
            value = v;
            next = n;
            length = null == next ? 1 : next.length+1;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException("setValue not supported in immutable entry.");
        }
    }
}