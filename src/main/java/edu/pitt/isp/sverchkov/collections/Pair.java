/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.collections;

import java.util.Objects;

/**
 *
 * @author YUS24
 */
public class Pair<First,Second> implements Tuple<First,Bucket<Second>> {
    
    public final First first;
    public final Second second;
    
    public Pair( First f, Second s ){
        first = f;
        second = s;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pair<First, Second> other = (Pair<First, Second>) obj;
        if (!Objects.equals(this.first, other.first)) {
            return false;
        }
        if (!Objects.equals(this.second, other.second)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.first);
        hash = 53 * hash + Objects.hashCode(this.second);
        return hash;
    }
    
    
    @Override
    public String toString(){
        return "("+first.toString()+", "+second.toString()+")";
    }

    @Override
    public First getFirst() {
        return first;
    }

    @Override
    public Bucket<Second> getRest() {
        return new Bucket<>(second);
    }
}
