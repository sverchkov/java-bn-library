/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.cn;

import edu.pitt.isp.sverchkov.data.ADTree;
import edu.pitt.isp.sverchkov.data.DataTable;
import edu.pitt.isp.sverchkov.graph.DAG;
import edu.pitt.isp.sverchkov.graph.SimpleDAGImpl;
import java.util.*;

/**
 * AD-backed implementation of a count network (DAG + count queries)
 * @author YUS24
 */
public class CountNetImpl<N,V> implements CountNet<N,V> {
    
    private DAG<N> dag;
    private ADTree<N,V> counts;
    
    public CountNetImpl( ADTree<N,V> counts, DAG<N> structure ){
        this.counts = counts;
        dag = structure;
    }
    
    public CountNetImpl( DataTable<N,V> data, DAG<N> structure ){
        counts = new ADTree( data );
        dag = new SimpleDAGImpl( structure );
    }
    
    @Override
    public Collection<V> values(N node) {
        return counts.values(node);
    }

    @Override
    public Map<V, Integer> counts(N node, Map<N, V> conditions) {
        return counts.counts(node, conditions);
    }

    @Override
    public int size() {
        return dag.size();
    }

    @Override
    public Collection<N> parents(N node) {
        return dag.parents(node);
    }

    @Override
    public Iterator<N> iterator() {
        return dag.iterator();
    }
}
