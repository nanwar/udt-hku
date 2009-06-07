/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.decisiontree.param;

import com.decisiontree.datagen.DataGen;
import com.decisiontree.operation.SplitSearch;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author Smith
 */
public class SwingParam {

    public static String INVALID = "Invalid!";

    private static final Map<String, String> algorithmMap  = new TreeMap<String,String>();
    static{
        algorithmMap.put(SplitSearch.UDT, "Building UDT without pruning.");
        algorithmMap.put(SplitSearch.UDTBP, "Building UDT with Basic interval pruning.");
        algorithmMap.put(SplitSearch.UDTLP, "Building UDT with Local interval pruning.");
        algorithmMap.put(SplitSearch.UDTGP, "Building UDT with Global interval pruning.");
        algorithmMap.put(SplitSearch.UDTES, "Building UDT with End-point sampling pruning.");
        algorithmMap.put(SplitSearch.AVG, "Building UDT with Averaging.");
        algorithmMap.put(SplitSearch.UDTUD, "Building UDT for uniform intervals.");
        algorithmMap.put(SplitSearch.AVGUD, "Building UDT for uniform intervals with Avearging.");
        algorithmMap.put(SplitSearch.POINT, "Building UDT on point data.");
    }

    private static final Map<String, String> dataGenMap = new TreeMap<String, String>();
    static{
            dataGenMap.put(DataGen.SAMPLE_GEN, "Generate uncertain data with Gaussian samples.");
            dataGenMap.put(DataGen.RANGE_GEN, "Generate uncertian data with Uniform intervals.");
    }

    public static Set<String> getAlgorithms(){
        return algorithmMap.keySet();
    }

    public static String getAlgorithmDescription(String key){
        return algorithmMap.get(key);
    }

    public static Set<String> getDataGens(){
        return dataGenMap.keySet();
    }

    public static String getDataGenDescription(String key){
        return dataGenMap.get(key);
    }

}
