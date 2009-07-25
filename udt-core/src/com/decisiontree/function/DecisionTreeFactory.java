package com.decisiontree.function;

import com.decisiontree.operation.SplitSearch;

public class DecisionTreeFactory {

    public static DecisionTree createDecisionTree(String algorithm, SplitSearch splitSearch, double nodeSize, double pruningThreshold){
    	DecisionTree decisionTree = null;
    	if(algorithm.equals(SplitSearch.AVG))
    	 	decisionTree = new SampleAvgDecisionTree(splitSearch, nodeSize, pruningThreshold);
    	 		else if(algorithm.equals(SplitSearch.UDTUD))
    	 			decisionTree = new RangeDecisionTree(splitSearch, nodeSize, pruningThreshold);
    			else if (algorithm.equals(SplitSearch.POINT))
    				decisionTree = new PointDecisionTree(splitSearch, nodeSize, pruningThreshold);
    	 		else if(algorithm.equals(SplitSearch.AVGUD))
    	 			decisionTree = new RangeAvgDecisionTree(splitSearch, nodeSize, pruningThreshold);
    	 		else
    	 			decisionTree = new SampleDecisionTree(splitSearch, nodeSize, pruningThreshold);
		return decisionTree;
    	
    }
}
