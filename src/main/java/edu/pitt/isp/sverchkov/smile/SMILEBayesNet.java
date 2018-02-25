/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.smile;

import edu.pitt.isp.sverchkov.bn.BayesNet;
import edu.pitt.isp.sverchkov.bn.MutableBayesNet;
import edu.pitt.isp.sverchkov.collections.ArrayTools;
import edu.pitt.isp.sverchkov.collections.CollectionTools;
import edu.pitt.isp.sverchkov.combinatorics.Assignments;
import edu.pitt.isp.sverchkov.data.DataTable;
import edu.pitt.isp.sverchkov.exec.CompositeException;
import edu.pitt.isp.sverchkov.graph.AbstractValueDAG;
import edu.pitt.isp.sverchkov.graph.GraphTools;
import java.io.File;
import java.io.IOException;
import java.util.*;
import smile.Network;
import smile.SMILEException;
import smile.learning.BkKnowledge;
import smile.learning.DataSet;
import smile.learning.GreedyThickThinning;

/**
 *
 * @author YUS24
 */
public class SMILEBayesNet extends AbstractValueDAG<String,String> implements MutableBayesNet<String,String> {
    
    private static final int[] ALGOLIST = {
        Network.BayesianAlgorithmType.Lauritzen,
        Network.BayesianAlgorithmType.LBP,
        Network.BayesianAlgorithmType.AisSampling,
        Network.BayesianAlgorithmType.BackSampling,
        Network.BayesianAlgorithmType.EpisSampling,
        Network.BayesianAlgorithmType.HeuristicImportance,
        Network.BayesianAlgorithmType.LSampling,
        Network.BayesianAlgorithmType.SelfImportance,
        Network.BayesianAlgorithmType.Henrion,
        //Network.BayesianAlgorithmType.Pearl
    };
    
    public enum Algorithm {
        LAURITZEN(Network.BayesianAlgorithmType.Lauritzen),
        LBP(Network.BayesianAlgorithmType.LBP),
        IMPORTANCE(Network.BayesianAlgorithmType.HeuristicImportance);
        
        private final int smileInt;
        Algorithm( int smileInt ){
            this.smileInt = smileInt;
        }
    }
    
    private final Network net;
    private final boolean convertIDs;
    private boolean escalationInference = true;
    
    private SMILEBayesNet( Network net, boolean convertIDs ){
        this.net = net;
        this.convertIDs = convertIDs;
    }
    
    public SMILEBayesNet( boolean convert ){
        convertIDs = convert;
        net = new Network();
    }
    
    public SMILEBayesNet( File file, boolean convert ) throws IOException{
        this( convert );
        net.readFile( file.getCanonicalPath() );
    }
    
    public SMILEBayesNet( BayesNet<String,String> source, boolean convert ){
        this( convert );
        
        for( String node : source )
            p_addNode( node, new ArrayList<>( source.values(node) ) );
        
        for( String node : source )
            for( String parent : source.parents(node) )
                p_addArc( parent, node );
        
        for( String node : source ){
            
            for( Map<String,String> assignment : parentAssignments( node ) ){
                
                Map<String,Double> conditionalProbabilities = new HashMap<>();
                for( String value : source.values(node) ){
                    conditionalProbabilities.put( value, source.probability( Collections.singletonMap( node, value), assignment ) );
                }
                
                psetCPT( node, assignment, conditionalProbabilities );
            }            
        }
    }
    
    public void setInferenceAlgorithm( Algorithm a ){
        net.setBayesianAlgorithm( a.smileInt );
    }
    
    public static void save( BayesNet<String,String> net, File file ) throws IOException{
        SMILEBayesNet smileNet;
        if( net instanceof SMILEBayesNet )
            smileNet = (SMILEBayesNet) net;
        else
            smileNet = new SMILEBayesNet( net, true );
        smileNet.save( file );
    }
    
    @Override
    public int size() {
        return net.getNodeCount();
    }

    @Override
    public Collection<String> parents(String node) {
        return Arrays.asList( net.getParentIds( node ) );
    }

    @Override
    public Collection<String> values(String node) {
        if( convertIDs ){
            String[] values = net.getOutcomeIds(node);
            Collection<String> result = new ArrayList<>(values.length);
            for( String value : values )
                result.add( fromSMILEID( value ) );
            return result;
        }
        return Arrays.asList( net.getOutcomeIds(node) );
    }

    @Override
    public double probability(Map<?extends String, ?extends String> outcomes, Map<?extends String, ?extends String> conditions) {
        final int[]
                outcomeNodes = new int[outcomes.size()],
                outcomeValues = new int[outcomes.size()],
                conditionNodes = new int[conditions.size()],
                conditionValues = new int[conditions.size()];
        // Get handles for outcomes
        int i=0, j=0;
        for (String node : GraphTools.nodesInTopOrder(this)){
            final boolean
                    o = outcomes.containsKey( node ),
                    c = conditions.containsKey( node );
            if( o || c ){
                int n = safeNodeLookup( node );
                if (c){
                    conditionNodes[j] = n;
                    conditionValues[j] = safeValueLookup( n, toSMILE(conditions.get(node)) );
                    if (o && safeValueLookup( n, toSMILE( outcomes.get(node)) ) != conditionValues[j])
                        return 0;
                    ++j;
                } else if (o){
                    outcomeNodes[i] = n;
                    outcomeValues[i] = safeValueLookup( n, toSMILE(outcomes.get(node)) );
                    ++i;
                }
            }
        }
        
        final int[]
                nodes = new int[i+j],
                values = new int[i+j];
        System.arraycopy(outcomeNodes, 0, nodes, 0, i);
        System.arraycopy(conditionNodes, 0, nodes, i, j);
        System.arraycopy(outcomeValues, 0, values, 0, i);
        System.arraycopy(conditionValues, 0, values, i, j);
        // Call recursive probability computation
        return probability( nodes, values, i );
    }
    
    private int safeNodeLookup( String node ){
        int result = -1;
        SMILEException ex = null;
        try {
            result = net.getNode(node);
        } catch (SMILEException e) {
            ex = null;
        }
        if (result < 0 || null != ex)
            throw new IllegalArgumentException( "Could not find node "+node+" in SMILE model.", ex );
        return result;
    }
    
    private int safeValueLookup( int node, String value ){
        int result = -1;
        SMILEException ex = null;
        try {
            result = ArrayTools.firstIndexOf(value, net.getOutcomeIds(node));
        } catch (SMILEException e) {
            ex = e;
        }
        if (result < 0 || null != ex )
            throw new IllegalArgumentException(
                "Could not find value "+value+
                " for node "+net.getNodeId(node)+
                " in SMILE model. Valid values are: "+
                Arrays.toString( net.getOutcomeIds(node) ), ex );
        return result;
    }
    
    private double probability( int[] nodes, int[] values, int conditioningBoundary ){
        double lnP = 0;
        net.clearAllEvidence();
        for (int i=nodes.length-1; i >= 0; i--){
            if( i < conditioningBoundary )
                lnP += Math.log( getProbabilitiesForTarget( nodes[i] )[values[i]] );
            try {
                net.setEvidence(nodes[i], values[i]);
            } catch (SMILEException e) {
                throw new WrException("Error setting node "+nodes[i]+" to value "+values[i],e);
            }
        }
        return Math.exp(lnP);
    }
    /*@Override
    public double probability(Map<?extends String, ?extends String> outcomes, Map<?extends String, ?extends String> conditions) {
        
        Map<String,String> o = new HashMap<>(outcomes.size());
        for( Map.Entry<?extends String,?extends String> entry : outcomes.entrySet() )
            o.put( entry.getKey(), toSMILE(entry.getValue()) );
        Map<String,String> c = new HashMap<>(conditions.size());
        for( Map.Entry<?extends String,?extends String> entry : conditions.entrySet() )
            c.put( entry.getKey(), toSMILE(entry.getValue()) );
        
        // If there is only one outcome this is a simple query
        if( o.size() == 1 ){
            Map.Entry<String,String> entry = o.entrySet().iterator().next();
            return probability( entry.getKey(), entry.getValue(), c );
        }
        
        // Otherwise we need to use the product rule
        Map<String,String> cond = new HashMap<>( c );
        double logResult = 0;
        for( Map.Entry<String,String> entry : o.entrySet() ){
            logResult += Math.log( probability( entry.getKey(), entry.getValue(), cond ) );
            cond.put( entry.getKey(), entry.getValue() );
        }
        
        return Math.exp(logResult);
    }*/
    
    @Override
    public Map<List<String>,Double> probabilities( List<? extends String> nodes, Map<? extends String, ? extends String> conditions ){
        
        final int[]
                vars = new int[conditions.size()+nodes.size()],
                vals = new int[conditions.size()+nodes.size()];
        final String[]
                nodeArray = new String[nodes.size()];
        
        // Translate to handles
        {
            int i=0;
            for( Map.Entry<? extends String,? extends String> entry : conditions.entrySet() ){
                
                vars[i] = ArrayTools.firstIndexOf( entry.getKey(), net.getAllNodeIds() );
                if( vars[i] < 0 )
                    throw new RuntimeException( "Could not find node "+entry.getKey()+" in SMILE model." );
                
                vals[i] = ArrayTools.firstIndexOf( entry.getValue(), net.getOutcomeIds( vars[i] ) );
                if( vals[i] < 0 )
                    throw new RuntimeException(
                            "Could not find value "+entry.getValue()+
                            " for node "+entry.getKey()+
                            " in SMILE model. Valid values are: "+
                            Arrays.toString( net.getOutcomeIds( vars[i] ) ) );
                
                ++i;
            }
            // Get node handles
            final String[] nodeIDs = net.getAllNodeIds();
            int k=0;
            for( int j=0; j<nodeIDs.length; j++ )
                if( nodes.contains( nodeIDs[j] ) ){
                    nodeArray[k] = nodeIDs[j]; // For assignment iterator
                    vars[i++] = j; // For call to probabilities
                }
            if( i < vars.length )
                throw new IllegalArgumentException( "Was not able to find handles for all of "+nodes+"!" );
        }
        
        // Query BN
        double[] pArray;
        try{
            pArray = probabilities( vars, vals, conditions.size() );
        }catch( WrException e ){
            throw new WrException( "Failed to get P( "+nodes+" | "+conditions+" ).", e);
        }

        // Map result back
        String[][] valueArray = new String[nodeArray.length][];
        for( int k = 0; k < nodeArray.length; k++ ){
            valueArray[k] = net.getOutcomeIds( nodeArray[k] );
        }
        
        Map<List<String>,Double> result = new HashMap<>();
        {
            int index = 0;
            for( Map<String,String> assignment : new Assignments<>( nodeArray, valueArray ) ){
                result.put( CollectionTools.applyMap( assignment, nodeArray ), pArray[index++] );
            }
        }
                
        return result;
    }
    
    private double[] probabilities( final int[] nodes, final int[] vals, final int conditionPoint ){
        
        // Base case
        if( conditionPoint >= nodes.length ) return new double[] {1.0};
        
        // Recursive case

        // Get last node
        double[] single = probabilities( nodes[conditionPoint], nodes, vals, conditionPoint );

        // Compute result array size
        int blockSize = 1;
        double[] result = new double[blockSize * single.length];
        
        // Multiply probabilities
        for( int v=0; v<single.length; v++ ){
            vals[conditionPoint] = v;
            double[] subresult = probabilities( nodes, vals, conditionPoint+1 );
            for( int i=0; i<subresult.length; i++ )
            result[v*blockSize+i] = subresult[i] * single[v];
        }
        
        return result;
    }
    
    /**
     * Computes a conditional distribution node | conditions
     * @param node the SMILE handle of the node
     * @param cNodes the SMILE handles of nodes to condition on
     * @param cVals the SMILE handles the values of nodes to condition on
     * @param conditions the number of conditions to actually condition on (excess ignored)
     * @return Array representing p( node | first 'conditions' cNodes = cVals )
     */
    private double[] probabilities( int node, int[] cNodes, int [] cVals, int conditions ){
        net.clearAllEvidence();
        
        for( int i=0; i < conditions; i++ )
            try{
                net.setEvidence( cNodes[i], cVals[i] );
            }catch( SMILEException e ){
                StringBuilder sb = new StringBuilder();
                sb
                    .append("Failed to set SMILE evidence node handle ")
                    .append(cNodes[i])
                    .append(" to value handle ").append(cVals[i])
                    .append(" based on node handle list ")
                    .append(Arrays.toString(cNodes))
                    .append(" and value handle list ")
                    .append(Arrays.toString(cVals))
                    .append('.');
                for( int j=0; j<conditions; j++ )
                    sb.append(" Definition of evidence node ")
                        .append(cNodes[j]).append(" is ")
                        .append(Arrays.toString( net.getNodeDefinition( cNodes[j] ) ))
                        .append('.');
                throw new WrException(sb.toString(), e);
                //System.err.println( "Couldn't set "+net.getNodeId(cNodes[i])+" ("+cNodes[i]+") to "+net.getOutcomeId( cNodes[i], cVals[i] )+" ("+cVals[i]+")");
            }        
        try{
            return getProbabilitiesForTarget( node );
        }catch( SMILEException e ){
            throw new WrException(
                    "Failed to get probability distribution for node (handle="+node+
                    ") given first "+conditions+
                    " conditions based on node handle list "+Arrays.toString(cNodes)+
                    " and value handle list "+Arrays.toString(cVals)+".", e);
        }
    }
    
    /**
     * Computes P( node = value | conditions ), assumes that all strings have been converted to their notation in the SMILE net.
     * @param node
     * @param value
     * @param conditions Map representation of variable-value assignments.
     * @return P( node = value | conditions )
     */
    private double probability( String node, String value, Map<String,String> conditions ){
                
        // Check if this is just a CPT lookup
        if( new HashSet( parents( node ) ).equals( conditions.keySet() ) ){
            // Compute position in node definition
            // return
        }
            
        // Otherwise do inference
        net.clearAllEvidence();
        
        for( Map.Entry<String,String> entry : conditions.entrySet() ){
            net.setEvidence( entry.getKey(), entry.getValue() );
        }
        
        final int valueIndex = ArrayTools.firstIndexOf(value, net.getOutcomeIds(node));
        if( valueIndex < 0 ) throw new IllegalArgumentException(node+" does not have value "+value);
        return getProbabilitiesForTarget(node)[valueIndex];
    }
    
    @Override
    public Iterator<String> iterator() {
        return Arrays.asList( net.getAllNodeIds() ).iterator();
    }
    
    /*
    public List<Float> getNodeCoordinates( String node ){
        Float[] coords = new Float[2];
        Rectangle pos = net.getNodePosition(node);
        coords[0] = new Float(pos.getMinX());
        coords[1] = new Float(pos.getMinY());
        return Arrays.asList(coords);
    }*/
    
    /**
     * Encode the name into a SMILE-friendly format.
     * @param name
     * @return corresponding SMILE ID
     */
    public static String toSMILEID( String name ){
        StringBuilder result = new StringBuilder("ID_");
        for( char c : name.toCharArray() ){
            result.append( Character.isLetterOrDigit(c)? c : String.format("_%2x", (byte)c) );
        }
        return result.toString();
    }

    /**
     * Decode edu.pitt.isp.sverchkov.smile-friendly IDs to user-readable names
     * @param id
     * @return corresponding readable name
     */
    public static String fromSMILEID( String id ){
        StringBuilder result = new StringBuilder();
        for( int i = 3; i < id.length(); i++ )
            if( id.charAt(i) == '_' ){
                result.append( (char) Byte.parseByte(id.substring(i+1, i+3), 16) );
                i+=2;
            } else result.append(id.charAt(i));
        return result.toString();
    }

    private static DataSet setupDS4Learning( DataTable<String, String> table ){
        
        DataSet ds = new DataSet();        
        final List<String> variables = table.variables();
        
        // Set-up
        Map<String,Map<String,Integer>> lookupTable = new HashMap<>( variables.size() );

        for( String var : variables ){
            ds.addIntVariable( var );
            lookupTable.put( var, new HashMap<String,Integer>() );
        }
            
        {
            String[][] varValues = new String[variables.size()][];
            int[] varValueNums = new int[variables.size()];
            for( List<String> row : table )
                for( int i = 0; i < varValues.length; i++ ){
                    String value = row.get(i);
                    Map<String,Integer> subTable = lookupTable.get( variables.get(i) );
                    Integer index = subTable.get( value );
                    if( null == index )
                        subTable.put( value, varValueNums[i]++ );
                }
            
            for( int i = 0; i < varValues.length; i++ ){
                varValues[i] = new String[ varValueNums[i] ];
                for( Map.Entry<String,Integer> entry : lookupTable.get( variables.get(i) ).entrySet() )
                    varValues[i][entry.getValue()] = entry.getKey();
            }
            
            for( int i = 0; i< varValues.length; i++ )
                ds.setStateNames( i, varValues[i] );
        }
        
        // Enter data
        {
            int r = 0;
            for( List<String> row : table ){
                ds.addEmptyRecord();
                for( int i = 0; i < variables.size(); i++ )
                    ds.setInt( i, r, lookupTable.get(variables.get(i)).get(row.get(i)) );
                ++r;
            }
            //System.out.println("Rows: "+r+" actual "+ds.getRecordCount());
        }
        
        return ds;
    }
    
    private static SMILEBayesNet runAlgo( final DataSet ds, final GreedyThickThinning algo, final int maxParents ){
        
        algo.setMaxParents( maxParents );
        algo.setPriorsMethod( GreedyThickThinning.PriorsType.K2 );
        
        Network net = algo.learn(ds);
        
        return new SMILEBayesNet( net, false );

    }
    
    public static SMILEBayesNet learnWithTiers(DataTable<String, String> table, int maxParents, List<List<String>> nodeTiers) {
        final DataSet ds = setupDS4Learning( table );
        
        GreedyThickThinning algo = new GreedyThickThinning();
        
        if( nodeTiers != null ){
            BkKnowledge bk = new BkKnowledge();
            bk.matchData(ds);
            int i = 0;
            for( List<String> nodeTier : nodeTiers ){
                ++i;
                for( String node : nodeTier )
                    if( table.variables().contains( node ) ){
                        bk.setTier( node, i );
                    }
            }
            algo.setBkKnowledge(bk);
        }
        
        return runAlgo( ds, algo, maxParents );
    }

    public static SMILEBayesNet learn( DataTable<String,String> table, int maxParents, List<String> order ){ // TODO add algorithm selection
        
        final DataSet ds = setupDS4Learning( table );
        
        // Learn network
        
        GreedyThickThinning algo = new GreedyThickThinning();
        
        if( order != null ){
            BkKnowledge bk = new BkKnowledge();
            bk.matchData(ds);
            int i = 1;
            for( String node : order )
                if( table.variables().contains( node ) ){
                    bk.setTier( node, i++ );
                }
            algo.setBkKnowledge(bk);
        }
        
        return runAlgo( ds, algo, maxParents );

    }

    @Override
    public void setCPT(String node, Map<String, String> parentAssignment, Map<String, Double> conditionalProbabilities) {
        psetCPT( node, parentAssignment, conditionalProbabilities );
    }

    private void psetCPT(String node, Map<String, String> parentAssignment, Map<String, Double> conditionalProbabilities) {
        
        final double[] definition = net.getNodeDefinition(node);
        
        final double[] source = cptRowAsArray( node, conditionalProbabilities );
        final int offset = cptRowOffset( node, parentAssignment );
        
        System.arraycopy( source, 0, definition, offset, source.length );       
        
        net.setNodeDefinition(node, definition);
    }

    private void save(File file) throws IOException {
        net.writeFile( file.getCanonicalPath() );
    }

    private String toSMILE( String value ) {
        return convertIDs ? toSMILEID( value ) : value;
    }
    
    private String fromSMILE( String value ) {
        return convertIDs ? fromSMILEID( value ) : value;
    }
    
    private int cptRowOffset( String node, Map<String,String> parentAssignment ){
        
        String[] values = net.getOutcomeIds(node);
        
        int offset = 0;
        int stepsize = values.length;
        
        String[] parents = net.getParentIds(node);
        for( int p = parents.length-1; p >= 0; p-- ){
            String[] vals = net.getOutcomeIds( parents[p] );            
            offset += stepsize * ArrayTools.firstIndexOf( toSMILE( parentAssignment.get( parents[p] ) ), vals );
            stepsize *= vals.length;
        }
        return offset;
    }
    
    private double[] cptRowAsArray( String node, Map<String,Double> conditionalProbabilities) {
        double[] result = new double[conditionalProbabilities.size()];
        String[] values = net.getOutcomeIds(node);
        
        for( Map.Entry<String,Double> entry : conditionalProbabilities.entrySet() )
            result[ ArrayTools.firstIndexOf( toSMILE(entry.getKey()), values ) ] = entry.getValue();
        
        return result;
    }

    @Override
    public void addNode(String node, List<String> values) {
        p_addNode( node, values );
    }
    
    private void p_addNode(String node, List<String> values) {
        
        final int size = values.size();
        
        net.addNode( Network.NodeType.Cpt, node );

        for( int i = 0; i < size; i++ )
            net.insertOutcome(node, i, toSMILE(values.get(i)));
        for( int i = 0; i < net.getOutcomeCount(node); i++ )
            if( !values.contains( fromSMILE( net.getOutcomeId(node, i) ) ) )
                net.deleteOutcome(node, i--);
    }

    @Override
    public void addArc(String parent, String child) {
        if( !parents( child ).contains(parent) )
            p_addArc( parent, child );
    }
    
    private void p_addArc(String parent, String child) {
        net.addArc(parent, child);
        assignments.remove(child);
    }

    @Override
    public void removeArc(String parent, String child) {
        
        // Get the new parent assignments
        Collection<String> removedParentStates = values(parent);
        Assignments<String,String> newAssignments;
        Map<String,Collection<String>> parentMap = new HashMap<>();
        for( String p : parents( child ) )
            if( !p.equals(parent) )
                parentMap.put( p, values(p) );
        newAssignments = new Assignments<>( parentMap );
        
        // Get the new probabilities by marginalizing away the removed parent
        Map<Map<String,String>,Map<String,Double>> newPs = new HashMap<>();
        for( Map<String,String> assignment : newAssignments ){
            Map<String,Double> cptRow = new HashMap<>();
            for( String v : values(child) ) cptRow.put( v, 0.0 );
            Map<String,String> a = new HashMap<>( assignment );
            for( String pv : removedParentStates ){
                a.put( parent, pv );
                for( Map.Entry<List<String>,Double> entry : probabilities( Collections.singletonList( child ), a).entrySet() ){
                    String v = entry.getKey().get(0);
                    cptRow.put(v, cptRow.get(v) + entry.getValue() );
                }
            }
            for( Map.Entry<String,Double> entry : cptRow.entrySet() )
                entry.setValue( entry.getValue()/removedParentStates.size() );
            newPs.put( assignment, cptRow );
        }
        
        // Change the underlying network
        net.deleteArc(parent, child);
        // Update assignment cacher
        assignments.put(child, newAssignments);
        // Update cpts
        for( Map.Entry<Map<String,String>,Map<String,Double>> entry : newPs.entrySet() )
            setCPT( child, entry.getKey(), entry.getValue() );
    }
    
    private double[] getProbabilitiesForTarget(String node){
        return getProbabilitiesForTarget(net.getNode(node));
    }
    private double[] getProbabilitiesForTarget(int node) {
        net.clearAllTargets();
        net.setTarget( node, true);
        
        double[] result = null;
        
        if (escalationInference) {
            CompositeException compex = CompositeException.EMPTY;
            for( int algo : ALGOLIST )
                try{
                    net.setBayesianAlgorithm(algo);
                    net.updateBeliefs();
                    result = net.getNodeValue(node);                    
                    break;
                } catch (SMILEException e) {
                    compex = compex.and(e);
                }
            if (null==result) throw compex;
        } else {
            net.updateBeliefs();
            result = net.getNodeValue(node);
        }
        return result;
    }

    
    public static class WrException extends RuntimeException {
        WrException( String message, Throwable e ){
            super( message, e );
        }
    }
}
