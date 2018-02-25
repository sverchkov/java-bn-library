/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.collections;

import java.util.Comparator;

/**
 *
 * @author YUS24
 */
public interface Tuple<First, Rest extends Tuple<?,?>> {
    First getFirst();
    Rest getRest();
    
    public static class CompareByFirst<Thing extends Comparable<Thing>> implements Comparator<Tuple<Thing,?>> {
        @Override
        public int compare(Tuple<Thing, ?> o1, Tuple<Thing, ?> o2) {
            return o1.getFirst().compareTo(o2.getFirst());
        }
    }
}
