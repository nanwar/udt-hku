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

import org.apache.log4j.Logger;

import com.decisiontree.build.TreeNode;
import com.decisiontree.operation.SplitSearch;

/**
 * 
 * DecisionTree (Abstract class) - builds a decision tree for given dataset files.
 *
 * @author Smith Tsang
 * @since 0.8
 *
 */
public abstract class DecisionTree {
	
	protected static final Logger log = Logger.getLogger(DecisionTree.class);
	
	public static final String BUILD = "TIME"; 
	public static final String ACCUR = "ACCUR";
	public static final String XFOLD = "XFOLD";

	protected SplitSearch splitSearch;
	
	protected double nodeSize = 1;
	protected double purity = 0.99;
	
	/**
	 * Constructor by the algorithm (SplitSearch) to find the best split point
	 * @param splitSearch the SplitSearch object
	 */
	public DecisionTree(SplitSearch splitSearch){
		setSplitSearch(splitSearch);
	}
	
	/**
	 * Constructor by the algorithm (SplitSearch) to find the best split point and the pre-pruning parameter
	 * @param splitSearch the SplitSearch object 
	 * @param nodeSize the size of the node for the tree to stop
	 * @param purity the purity of the node for the tree to stop
	 */
	public DecisionTree(SplitSearch splitSearch, double nodeSize, double purity){
		this(splitSearch);
		setNodeSize(nodeSize);
		setPurity(purity);
	}

	/**
	 * Building and saving a decision tree - NOT IMPLEMENTED
	 * @param training the training dataset file
	 * @param path the path to store the decision tree
	 */
	public abstract void buildAndSaveTree(String training, String path);
	
	/**
	 * Building a deciison tree from the given training data file
	 * @param training the training dataset file
	 * @return the root node of th decision tree
	 */
	public abstract TreeNode buildTree(String training);
	
	/**
	 * Generate the decision tree by a set of training data and finding the accuracy 
	 * of testing the same set of data (Self-classifying)
	 * @param training
	 * @return
	 */
	public abstract double findAccuracy(String training);
	
	/**
	 * Generate the decision tree by a set of training data and finding the accuracy 
	 * of testing a set of testin data file
	 * @param training the training data file
	 * @param testing the testing data
	 * @return
	 */
	public abstract double findAccuracy(String training, String testing);
	
	
	/**
	 * Find the classification accuracy using the given tree stored in given path
	 * @param path the path of the tree
	 * @param testing the testing data file
	 * @return the classification accuracy in percentage 
	 */
	public abstract double findAccuracyByTree(String path, String testing);
	
	/**
	 * Find the cross-fold validataion accuracy
	 * @param training the training data file
	 * @return the cross-fold validation accuracy
	 */
	public abstract double crossFold(String training);

	/**
	 * Get the algorithm (SplitSearch) to find the best split point
	 * @return the SplitSearch object
	 */
	public SplitSearch getSplitSearch() {
		return splitSearch;
	}

	/**
	 * Set the algorithm (SplitSearch) to find the best split point
	 * @param splitSearch the splitSearch object
	 */
	public void setSplitSearch(SplitSearch splitSearch) {
		this.splitSearch = splitSearch;
	}

	/**
	 * Get the pruning node size during the building the decision
	 * @return the pruning node size
	 */
	public double getNodeSize() {
		return nodeSize;
	}

	/**
	 * Set the pruning node size during the building of decision tree
	 * @param nodeSize the pruning node size
	 */
	public void setNodeSize(double nodeSize) {
		this.nodeSize = nodeSize;
	}

	/**
	 * Get the pruning purity during the building of decision tree
	 * @return the pruning purity
	 */
	public double getPurity() {
		return purity;
	}

	/**
	 * Set the pruning purity during the building of decision tree
	 * @param purity the pruning purity
	 */
	public void setPurity(double purity) {
		this.purity = purity;
	}

}
