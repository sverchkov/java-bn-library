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
public class Triple<First,Second,Third> implements Tuple<First,Pair<Second,Third>> {
    public final First first;
    public final Second second;
    public final Third third;
    
    public Triple( First f, Second s, Third t ){
        first = f;
        second = s;
        third = t;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Triple<First, Second, Third> other = (Triple<First, Second, Third>) obj;
        if (!Objects.equals(this.first, other.first)) {
            return false;
        }
        if (!Objects.equals(this.second, other.second)) {
            return false;
        }
        if (!Objects.equals(this.third, other.third)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.first);
        hash = 97 * hash + Objects.hashCode(this.second);
        hash = 97 * hash + Objects.hashCode(this.third);
        return hash;
    }
    
    @Override
    public String toString(){
        return "("+first+", "+second+", "+third+")";
    }
    
    public Pair<First,Second> firstSecond(){
        return new Pair<>( first, second );
    }
    
    public Pair<First,Third> firstThird(){
        return new Pair<>( first, third );
    }
    
    public Pair<Second,Third> secondThird(){
        return new Pair<>( second, third );
    }

    @Override
    public First getFirst() {
        return first;
    }

    @Override
    public Pair<Second, Third> getRest() {
        return secondThird();
    }
    
}
