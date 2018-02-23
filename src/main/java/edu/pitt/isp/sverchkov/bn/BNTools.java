/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.bn;

import edu.pitt.isp.sverchkov.data.DataTable;
import edu.pitt.isp.sverchkov.data.DataTableImpl;
import edu.pitt.isp.sverchkov.graph.DAG;
import edu.pitt.isp.sverchkov.graph.GraphTools;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author YUS24
 */
public class BNTools {
        
    public static <N,V> DataTable<N,V> generate( final BayesNet<N,V> net, final int n ){
        return generate( net, n, new Random() );
    }
    
    public static <N,V> DataTable<N,V> generate( final BayesNet<N,V> net, final int n, Random random ){
        
        final List<N> variables = GraphTools.nodesInTopOrder( net );        
        final int m = variables.size();
        final DataTable<N,V> data = new DataTableImpl<>( variables );
        
        for( int i=0; i<n; i++ ){
            List<V> row = new ArrayList<>(m);
            Map<N,V> conditions = new HashMap<>();
            for( N variable : variables ){
                V value = sample( net, variable, conditions, random );
                row.add( value );
                conditions.put( variable, value );
            }
            data.addRow( row );
        }
        return data;
    }
    
    public static <N,V> V sample( final BayesNet<N,V> net, final N node, final Map<N,V> conditions, Random random ){
        
        Map<N,V> parentAssignment = new HashMap<>( conditions );
        parentAssignment.keySet().retainAll( net.parents(node) );
        
        double p = random.nextDouble();
        for( V value : net.values(node) ){
            final double q = net.probability( Collections.singletonMap( node, value), conditions);
            if( p < q )
                return value;
            else
                p -= q;
        }
        
        return null;
    }
    
    public static <N> Document toXML( final DAG<N> net ) throws ParserConfigurationException{
        return toXML( net, DocumentBuilderFactory.newInstance().newDocumentBuilder() );
    }
    
    public static <N> Document toXML( final DAG<N> net, final DocumentBuilder builder ){
        final Document doc = builder.newDocument();
        
        final Element docRoot = doc.createElement("dag");
        doc.appendChild( docRoot );
        
        for( N node : net ){
            final Element dNode = doc.createElement("node");
            dNode.setAttribute( "name", node.toString() );
            docRoot.appendChild( dNode );
            for( N parent : net.parents(node) ){
                final Element p = doc.createElement("parent");
                p.setAttribute( "name", parent.toString() );
                dNode.appendChild( p );
            }
        }
        
        return doc;
    }
}
