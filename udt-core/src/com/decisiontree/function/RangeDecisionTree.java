/**
 * Decision Tree Classification With Uncertain Data (UDT)
 * Copyright (C) 2009, The Database Group, 
 * Department of Computer Science, The University of Hong Kong
 * 
 * This file is part of UDT.
 *
 * UDT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UDT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.decisiontree.function;

import java.util.List;

import com.decisiontree.build.TreeNode;
import com.decisiontree.build.RangeClassification;
import com.decisiontree.build.RangeTree;
import com.decisiontree.data.RangeDataSet;
import com.decisiontree.data.RangeDataSetInit;
import com.decisiontree.data.Tuple;
import com.decisiontree.operation.SplitSearch;

/**
 * 
 * RangeDecisionTree - builds a decision tree for given interval-valued dataset files with distrubtion-based technique.
 *
 * @author Smith Tsang
 * @since 0.8
 *
 */
public class RangeDecisionTree extends DecisionTree {
	
	
	public RangeDecisionTree(SplitSearch splitSearch){
		super(splitSearch);
	}

	
	public RangeDecisionTree(SplitSearch splitSearch, double nodeSize, double pruningThreshold) {
		super(splitSearch, nodeSize, pruningThreshold);
	}
	
	private RangeDataSet generateDataSet(String training){
		RangeDataSetInit init = new RangeDataSetInit(training);
		return init.getDataSet();
	}
	
	@Override
	public void buildAndSaveTree(String training, String path) {
		// TODO Auto-generated method stub

	}

	@Override
	public TreeNode buildTree(String training) {
		RangeDataSet dataSet = generateDataSet(training);
		
		RangeTree tree = new RangeTree(dataSet, getSplitSearch());
		
		tree.constructFinalTree(false); // TODO: allow print tree
		
		return tree.getRoot();
		
	}

	@Override
	public double crossFold(String training) {
		RangeDataSet dataSet = generateDataSet(training);

		RangeClassification classification = new RangeClassification(dataSet, splitSearch);
		return classification.crossAllFold(nodeSize, purity);
	}

	@Override
	public double findAccuracy(String training) {
		RangeDataSet dataSet = generateDataSet(training);
		
		RangeTree tree = new RangeTree(dataSet,splitSearch);

		tree.constructFinalTree(false);

		RangeClassification test = new RangeClassification(dataSet, splitSearch);

		List<Tuple> testSet =  dataSet.getData();

		return test.ClassifyAll(tree.getRoot(), testSet);

	}

	@Override
	public double findAccuracy(String training, String testing) {
		RangeDataSet dataSet = generateDataSet(training);
		
		RangeTree tree = new RangeTree(dataSet,splitSearch);

		tree.constructFinalTree(false);
		
		RangeDataSet testDB = generateDataSet(testing);
		RangeClassification test = new RangeClassification(dataSet, splitSearch);

		List<Tuple> testSet =  testDB.getData();

		return test.ClassifyAll(tree.getRoot(), testSet);

	}


	@Override
	public double findAccuracyByTree(String path, String testing) {
		// TODO Auto-generated method stub
		return 0;
	}

}
