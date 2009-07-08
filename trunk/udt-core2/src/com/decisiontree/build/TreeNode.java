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
package com.decisiontree.build;

import java.util.Iterator;
import java.util.List;

import com.decisiontree.data.Tuple;

/**
 * 
 * DreeNode - Represents a decision tree nodes. It could be either internal node or leaf node.
 *
 * @author Smith Tsang
 * @since 0.8
 *
 */
public class TreeNode{

	public static final int INTERAL = 0;
	public static final int LEAF = 1;

	private List<Tuple> data;
	private int noTuples;
	private double weightedNoTuples;


	private TreeNode parent;
	private TreeNode [] children;
	private int noChildren;
	
	/**
	 * Node Type - internal node or leaf node
	 */
	private int type; 
	private int attrNum; 
	private double split;

	private double entropy;
	private double [] clsDist;
	private int height;
	private int noCls;

	private boolean sameCls;

	private int cls; //only for leaf node

	public TreeNode(){
		noChildren = -1;
		parent = null;
		data = null;
	}

	public TreeNode(List<Tuple> data, TreeNode parent, int noCls){
		setNoCls(noCls);
		setTuple(data);
		setClsDist();
		countTuples();
		setEntropy();
		setParent(parent);
		noChildren = -1;
		if(parent == null) height = 0;
	}

	public void setNoCls(int noCls){
		this.noCls = noCls;
	}

	public void setType(int type){
		this.type = type;
	}

	public int getType(){
		return type;
	}

	public void setTuple(List<Tuple> data){
		this.data = data;
		noTuples = data.size();
	}

	public void countTuples(){
		weightedNoTuples = 0;
		Iterator<Tuple> iter = data.iterator();
		while(iter.hasNext()){
			weightedNoTuples += iter.next().getWeight();
		}
	}

	public void setAttrNum(int i){
		this.attrNum = i;
	}

	public void setParent(TreeNode parent){
		this.parent = parent;
	}

	public void setNoChild(int noChild){
		if(noChild < 0) return;
		this.noChildren = noChild;
		children = new TreeNode[noChild];
	}
	
	public void addChild(TreeNode child, int pos){
		if(pos < 0 && pos >= noChildren) return;
		children[pos] = child;
	}

	public List<Tuple> getData(){
		return data;
	}

	public int getAttrNum(){
		return attrNum;
	}

	public int getNoChildren(){
		return noChildren;
	}

	public int getCls(){
		return cls;
	}

	public TreeNode getChild(int pos){
		if(pos < 0 && pos >= noChildren) return null;
		return children[pos];
	}
	
	public TreeNode getParent(){
		return parent;
	}
	
	public double getSplit(){
		return split;
	}
        public void setSplit(double split){
		this.split = split;
	}
	
	public void setCls(int cls){
		this.cls = cls;
	}


	public double getWeightedNoTuples(){
		return weightedNoTuples;
	}

	public int getNoTuples(){
		return noTuples;
	}


	public boolean isEmptyNode(){
		return (data == null);
	}

	public double getPurity(){

		return clsDist[cls]/weightedNoTuples;

	}


	public int majorityCls(){
		
		return cls;
	}
	
	public void setClsDist(){

		clsDist = new double[noCls];
		for(int i =0; i< noCls; i++){
			clsDist[i] = 0;
		}
		
		Tuple tuple = null;
		Iterator<Tuple> iter = data.iterator();
		while(iter.hasNext()){
			tuple = iter.next();
			clsDist[tuple.getCls()] += tuple.getWeight();
		}

		int max = 0,count =0;
		for(int i = 0 ; i < noCls ; i++){
			if(clsDist[max] < clsDist[i])
				max = i;
			if(clsDist[i] > 0)
				count++;
		}
		
		this.cls = max;

		sameCls = (count <= 1);
	}

	public void setEntropy(){
		
		double ent = 0;

		for(int i =0; i< noCls; i++)
			ent += clsDist[i]
					* Math.log(clsDist[i]/weightedNoTuples);
		
		entropy = -1.0 * ent / Math.log(2.0)/ weightedNoTuples;
	}

	public boolean isSingleCls(){
		return sameCls;
	}

	public double getEnt(){
		return entropy;
	}

	public double getError(){
		
		double count =0.0;
		for(int i = 0 ; i < clsDist.length ; i++)
			if(i != cls)
				count += clsDist[i];

		return count;
		
	}

	public void setHeight(int height){
		this.height = height;
	}

	public int getHeight(){
		return height;
	}
	public double [] getClsDist(){
		return clsDist;	
	}

	public void clearChildren(){
		children = null;
		noChildren = -1;
	}
}