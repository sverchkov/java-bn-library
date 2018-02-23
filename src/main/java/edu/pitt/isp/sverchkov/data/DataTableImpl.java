/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.data;

import java.util.*;

/**
 *
 * @author YUS24
 */
public class DataTableImpl<N,V> implements DataTable<N,V> {
    
    private final List<N> variables;
    private final List<List<V>> rows;
    
    public DataTableImpl( List<? extends N> vars ){
        variables = Collections.unmodifiableList( new ArrayList<>( vars ) );
        rows = new ArrayList<>();
    }

    @Override
    public List<N> variables() {
        return variables;
    }

    @Override
    public int columnCount() {
        return variables.size();
    }

    @Override
    public int rowCount() {
        return rows.size();
    }

    @Override
    public void addRow(List<? extends V> row) {
        final int
                m = row.size(),
                w = columnCount();
        
        if( m != w )
            throw new IllegalArgumentException( "Tried to insert a row of length "+m+" into a table of width "+w+"." );
        
        rows.add( Collections.unmodifiableList( new ArrayList<>( row ) ) );
    }

    @Override
    public Iterator<List<V>> iterator() {
        return Collections.unmodifiableList( rows ).listIterator();
    }
    
}
