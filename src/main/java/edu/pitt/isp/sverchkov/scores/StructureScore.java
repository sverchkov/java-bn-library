/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.scores;

import edu.pitt.isp.sverchkov.cn.CountNet;
import java.util.Map;

/**
 *
 * @author YUS24
 */
public interface StructureScore {
    <Variable,Value> double score( CountNet<Variable,Value> net );
    <Variable,Value> double scoreVariable( CountNet<Variable,Value> net, Variable var );
    <Variable,Value> Map<Map<Variable,Value>,Double> scoreParentAssignments( CountNet<Variable,Value> net, Variable var );
    <Variable,Value> Map<Value,Double> scoreVariableValuesForParentAssignment( CountNet<Variable,Value> net, Variable var, Map<Variable,Value> parentAssignment );
}
