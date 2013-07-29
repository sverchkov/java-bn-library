/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.scores;

import edu.pitt.isp.sverchkov.cn.CountNet;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author YUS24
 */
public class K2 extends AbstractBayesianDirichletScore {
    
    private static final K2 instance = new K2();
    public static K2 getInstance(){
        return instance;
    }
    private K2(){}

    @Override
    protected <Variable, Value> Map<Value, Double> getAlphas(CountNet<Variable, Value> net, Variable var, Map<Variable, Value> parentAssignment) {
        Map<Value,Double> results = new HashMap<>();
        for( Value x : net.values(var) ) results.put( x, 1.0 );
        return results;
    }
}
