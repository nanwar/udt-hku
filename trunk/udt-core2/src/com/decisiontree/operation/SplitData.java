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
package com.decisiontree.operation;

/**
 * 
 * SplitData - Stores the split information, including attribute, split point and the correpsonding entropy
 *
 * @author Smith Tsang
 * @since 0.8
 *
 */
public class SplitData{
	private int attrNum;
	private double split;
	private double entropy;

	/**
	 * Default Constructor 
	 */
	public SplitData(){
		entropy = Double.POSITIVE_INFINITY;
	}
	
	/**
	 * Constructor with attribute number, split point and entropy
	 * @param attrNum the attribute number
	 * @param split the split point
	 * @param entropy the entropy
	 */
	public SplitData(int attrNum, double split, double entropy){
		this.attrNum = attrNum;
		this.split = split;
		this.entropy = entropy;
	}

	/**
	 * Set the split point corresponding to the attribute
	 * @param split the split point
	 */
	public void setSplit(double split){
		this.split = split;
	}

	/**
	 * Set the attribute number
	 * @param attrNum the attribute number
	 */
	public void setAttrNum(int attrNum){
		this.attrNum = attrNum;
	}
	
	/**
	 * Get the split point correponding to the attribute
	 * @return the split point
	 */
	public double getSplit(){
		return split;
	}
	
	/**
	 * Get the attribute number
	 * @return the attribute number
	 */
	public int getAttrNum(){
		return attrNum;
	}

	/**
	 * Set the entropy of the split point
	 * @param ent the entropy
	 */
	public void setEnt(double ent){
		entropy = ent;
	}

	/**
	 * Gett the entropy of the split point
	 * @return the entropy
	 */
	public double getEnt(){
		return entropy;
	}

	/**
	 * Check if it is a valid split point
	 * @return whether the split point is valid
	 */
	public boolean isValidSplit(){
		return entropy != Double.POSITIVE_INFINITY;
	}

}
