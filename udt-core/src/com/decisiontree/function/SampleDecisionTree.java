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
import com.decisiontree.build.SampleClassification;
import com.decisiontree.build.SampleTree;
import com.decisiontree.data.SampleDataSet;
import com.decisiontree.data.SampleDataSetInit;
import com.decisiontree.data.Tuple;
import com.decisiontree.operation.SplitSearch;
import com.decisiontree.param.GlobalParam;

/**
 * 
 * SampleDecisionTree - builds a decision tree for given interval-valued sampled-dstributed dataset 
 * 						files with distribution-based techniques.
 *
 * @author Smith Tsang
 * @since 0.8
 *
 */
public class SampleDecisionTree extends DecisionTree {
	
	private int noSamples = GlobalParam.DEFAULT_NO_SAMPLES;

	public SampleDecisionTree(SplitSearch splitSearch){
		super(splitSearch);
	}
	
	public SampleDecisionTree(SplitSearch splitSearch, int noSamples){
		this(splitSearch);
		setNoSamples(noSamples);
	}

	public SampleDecisionTree(SplitSearch splitSearch, double nodeSize, double pruningThreshold) {
		super(splitSearch, nodeSize, pruningThreshold);
	}
	
	public SampleDecisionTree(SplitSearch splitSearch, int noSamples, double nodeSize, double pruningThreshold) {
		this(splitSearch, nodeSize, pruningThreshold);
		setNoSamples(noSamples);
	}


	private SampleDataSet generateDataSet(String training, int noSamples){
		SampleDataSetInit init = new SampleDataSetInit(training, noSamples);
		return init.getDataSet();
	}
	
	@Override
	public void buildAndSaveTree(String training, String path) {
		// TODO Auto-generated method stub

	}

	@Override
	public TreeNode buildTree(String training) {
		SampleDataSet dataSet = generateDataSet(training, getNoSamples());
		
		SampleTree tree = new SampleTree(dataSet, getSplitSearch());
		
		tree.constructFinalTree(false); // TODO: allow print tree
		
		return tree.getRoot();
		
	}

	@Override
	public double crossFold(String training) {
		SampleDataSet dataSet = generateDataSet(training, getNoSamples());

		SampleClassification classification = new SampleClassification(dataSet, splitSearch);
		return classification.crossAllFold(nodeSize, purity);
	}

	@Override
	public double findAccuracy(String training) {
		SampleDataSet dataSet = generateDataSet(training, getNoSamples());
		
		SampleTree tree = new SampleTree(dataSet,splitSearch);

		tree.constructFinalTree(false);

		SampleClassification test = new SampleClassification(dataSet, splitSearch);

		List<Tuple> testSet =  dataSet.getData();

		return test.ClassifyAll(tree.getRoot(), testSet);

	}

	@Override
	public double findAccuracy(String training, String testing) {
		SampleDataSet dataSet = generateDataSet(training, getNoSamples());
		
		SampleTree tree = new SampleTree(dataSet,splitSearch);

		tree.constructFinalTree(false);
		
		SampleDataSet testDataSet = generateDataSet(testing, getNoSamples());
		SampleClassification test = new SampleClassification(dataSet, splitSearch);

		List<Tuple> testSet =  testDataSet.getData();

		return test.ClassifyAll(tree.getRoot(), testSet);

	}

	@Override
	public double findAccuracyByTree(String path, String testing) {
		// TODO Auto-generated method stub
		return 0;
	}


	public int getNoSamples() {
		return noSamples;
	}


	public void setNoSamples(int noSamples) {
		this.noSamples = noSamples;
	}

}
