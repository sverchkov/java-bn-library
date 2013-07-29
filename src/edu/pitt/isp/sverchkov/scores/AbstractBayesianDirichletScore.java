/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.scores;

import edu.pitt.isp.sverchkov.cn.CountNet;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.special.Gamma;

/**
 *
 * @author YUS24
 */
public abstract class AbstractBayesianDirichletScore implements StructureScore {
    
    protected abstract <Variable,Value> Map<Value,Double> getAlphas( CountNet<Variable,Value> net, Variable var, Map<Variable,Value> parentAssignment );
    
    @Override
    public <Variable,Value> double score( CountNet<Variable,Value> net ){
        double lnProduct = 0;
        for( Variable v : net )
            lnProduct += scoreVariable( net, v );
        return lnProduct;
    }
    
    @Override
    public <Variable,Value> double scoreVariable( CountNet<Variable,Value> net, Variable v ){
        double lnProduct = 0;
        for( double d : scoreParentAssignments( net, v ).values() )
            lnProduct += d;
        return lnProduct;
    }
    
    @Override
    public <Variable,Value> Map<Map<Variable,Value>,Double> scoreParentAssignments( CountNet<Variable,Value> net, Variable v ){
        Map<Map<Variable,Value>,Double> results = new HashMap<>();
        for( Map<Variable,Value> parentAssignment : net.parentAssignment(v) ){
            
            double alpha = 0;
            int count = 0;
            
            for( double alphai : getAlphas( net, v, parentAssignment ).values() )
                alpha += alphai;
            for( int n : net.counts( v, parentAssignment ).values() )
                count += n;
            
            double lnProduct = Gamma.logGamma(alpha) - Gamma.logGamma( alpha + count );
            for( double score : scoreVariableValuesForParentAssignment( net, v, parentAssignment ).values() )
                lnProduct += score;
            
            results.put(parentAssignment, lnProduct);
        }
        return results;
    }
    
    @Override
    public <Variable,Value> Map<Value,Double> scoreVariableValuesForParentAssignment( CountNet<Variable,Value> net, Variable v, Map<Variable,Value> parentAssignment ){
        Map<Value,Double> results = new HashMap<>();
        Map<Value,Double> alphas = getAlphas( net, v, parentAssignment );
        Map<Value,Integer> counts = net.counts( v, parentAssignment );
        for( Value x : net.values(v) )
            results.put( x, Gamma.logGamma( alphas.get(x) + counts.get(x) ) - Gamma.logGamma( alphas.get( x ) ) );
        return results;
    }
}
