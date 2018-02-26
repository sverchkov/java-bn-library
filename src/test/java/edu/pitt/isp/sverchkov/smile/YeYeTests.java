package edu.pitt.isp.sverchkov.smile;

import smile.Network;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class YeYeTests {

    public static void testYuriyCode1(){
        Network net = new Network();
        net.readFile("finalModel_clean.xdsl");
        boolean convertIDs = false;
        SMILEBayesNet  smileNet = new SMILEBayesNet(net, convertIDs);
        List<String> nodes = new ArrayList<String>();
        nodes.add(new String("BC0021400"));
        Map<String, String> conditions = new HashMap<String, String>();
        conditions.put(new String("C2055125"), new String("P"));
        Map<List<String>,Double> conditionalProbs = smileNet.probabilities(nodes, conditions);
        Set<List<String>> keySet = conditionalProbs.keySet(); //the keySet lists all possible configurations
        for (List<String> oneList : keySet){
            for (int i=0; i<oneList.size(); i++){
                System.out.print(oneList.get(i)+"-");
            }
            System.out.println(conditionalProbs.get(oneList));
        }
    }


    public static void testYuriyCode2(){
        Network net = new Network();
        net.readFile("finalModel_clean.xdsl");
        boolean convertIDs = false;
        SMILEBayesNet  smileNet = new SMILEBayesNet(net, convertIDs);
        List<String> nodes = new ArrayList<String>();
        nodes.add(new String("BC0021400"));
        nodes.add(new String("C0420679"));
        Map<String, String> conditions = new HashMap<String, String>();
        conditions.put(new String("C2055125"), new String("P"));
        Map<List<String>,Double> conditionalProbs = smileNet.probabilities(nodes, conditions);
        Set<List<String>> keySet = conditionalProbs.keySet(); //the keySet lists all possible configurations
        for (List<String> oneList : keySet){
            for (int i=0; i<oneList.size(); i++){
                System.out.print(oneList.get(i)+"-");
            }
            System.out.println(conditionalProbs.get(oneList));
        }
    }

    public static void testYuriyCode3(){
        Network net = new Network();
        net.readFile("finalModel_clean.xdsl");
        boolean convertIDs = false;
        SMILEBayesNet  smileNet = new SMILEBayesNet(net, convertIDs);
        List<String> nodes = new ArrayList<String>();
        nodes.add(new String("BC0021400"));
        nodes.add(new String("C0420679"));
        Map<String, String> conditions = new HashMap<String, String>();
        // no condition are available
        Map<List<String>,Double> conditionalProbs = smileNet.probabilities(nodes, conditions);
        Set<List<String>> keySet = conditionalProbs.keySet(); //the keySet lists all possible configurations
        for (List<String> oneList : keySet){
            for (int i=0; i<oneList.size(); i++){
                System.out.print(oneList.get(i)+"-");
            }
            System.out.println(conditionalProbs.get(oneList));
        }
    }
}
