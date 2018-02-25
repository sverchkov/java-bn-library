/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author YUS24
 */
public class ArrayTools {
    
    /**
     * @param target
     * @param array
     * @return true iff the array contains the target
     */
    public static boolean contains( Object target, Object array[] ){
        return firstIndexOf( target, array ) != -1;
    }
    
    /**
     * Finds the first index of a target in an array (using equals)
     * @param target
     * @param array
     * @return the first index of an object in the array for which target.equals( object ) returns true.
     * If target is null returns the first index of a null element.
     * Returns -1 if the array is null or if the target is not found in the array.
     */
    public static int firstIndexOf( Object target, Object array[] ){
        if( array != null )
            if( target == null ){
                for( int i=0; i<array.length; i++ )
                    if( array[i] == null ) return i;
            }else{
                for( int i=0; i<array.length; i++ )
                    if( target.equals( array[i] ) ) return i;
            }
        return -1;
    }
    
    /**
     * Finds the first index of a target in an array (using equals).
     * The index is determined by iterating over the iterable and counting elements, starting at 0.
     * I.e. the first element has index 0, the second 1, and so on.
     * If iteration blocks or never finishes, this method will block or never finish.
     * This method is meant to be used mainly with finite lists and other finite order-preserving iterable collections.
     * @param target
     * @param array
     * @return the first index of an object in the array for which target.equals( object ) returns true.
     * If target is null returns the first index of a null element.
     * Returns -1 if the array is null or if the target is not found in the array.
     */
    public static int firstIndexOf( Object target, Iterable array ){
        if( array != null ){
            int i=0;
            if( target == null )
                for( Object o : array ){
                    if( o == null ) return i;
                    ++i;
                }
            else
                for( Object o : array ){
                    if( target.equals(o) ) return i;
                    ++i;
                }
        }
        return -1;
    }
    
    /**
     * Returns the rectangular dimensions of a rectangular 2-dimensional array of ints.
     * @param array
     * @return The dimensions as a two-element array <tt>dim</tt>, such that <tt>array</tt> is <tt>int[dim[0]][dim[1]]</tt>.
     * @throws IllegalArgumentException if the array is jagged.
     */
    public static int[] rectangularDimensions( int[][] array ){
        final int m = array.length;
        
        int n = 0, i=0;
        do{
            n = array[i++].length;
        }while( i < m && n == array[i].length );
        if( i < m )
            throw new IllegalArgumentException("Array must be rectangular.");

        return new int[] {m,n};
    }
    
    /**
     * Transposes an array of ints.
     * @param array an RxC array
     * @return a CxR array such that <tt>result[i][j] == array[j][i]</tt>.
     * @throws IllegalArgumentException if the array is jagged.
     */
    public static int[][] transpose( int[][] array ){
        final int[] dim = rectangularDimensions( array );
        final int[][] result = new int[dim[1]][dim[0]];
        for( int r = 0; r < dim[0]; r++ )
            for( int c = 0; c < dim[0]; c++ )
                result[c][r] = array[r][c];
        return result;
    }
    
    public static double[] primitiveArray( final Collection<Double> collection ){
        return primitiveArray( collection, collection.size() );
    }
    
    public static double[] primitiveArray( final Iterable<Double> iterable, final int size ){
        double[] result = new double[size];
        int i=0;
        for( double d : iterable )
            if( i < size ) result[i++] = d;
            else break;
        return result;
    }
    
    public static List<Double> wrapperList( final double[] array ){
        List<Double> result = new ArrayList<>( array.length );
        for( double d : array )
            result.add(d);
        return result;
    }
}
