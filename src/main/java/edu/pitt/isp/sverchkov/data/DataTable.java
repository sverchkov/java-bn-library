/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.data;

import java.util.List;

/**
 *
 * @author YUS24
 */
public interface DataTable<N,V> extends Iterable<List<V>> {
    List<N> variables();
    int columnCount();
    int rowCount();
    void addRow( List<? extends V> row );
}
