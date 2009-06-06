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
 * BinarySplit - Finds the binary split point of an attribute using the basic technique.
 *
 * @author Smith Tsang
 * @since 0.8
 *
 */
public class BinarySplit implements Split{

	protected double noTuples;	
	protected int noCls;
	protected double localOptimal;
	protected double threshold;

	public BinarySplit(double noTuples, int noCls){
		this.noTuples =noTuples;
		this.noCls = noCls;
		this.threshold = Double.POSITIVE_INFINITY;
		this.localOptimal = Double.POSITIVE_INFINITY;
	}

	public void run(Histogram [] segments){
	
		int noSegments = segments.length;
		double left[] = new double[noCls];
		double right[] = new double[noCls];
		for(int i = 0; i < noCls; i++){
			left[i] = 0.0;
			for(int j = 0; j < noSegments; j++){
				right[i] += segments[j].getCls(i);
			}
		}
		int min = -1;
		double minEnt = Double.POSITIVE_INFINITY;

		for(int j = 0 ; j < noSegments-1; j++){
			for(int i = 0; i  <noCls; i++){
				left[i] += segments[j].getCls(i);			
				right[i] -= segments[j].getCls(i);
			}

			double avgEnt = avgEntropy(left,right);
			if(minEnt - avgEnt > 1E-12){
				min = j;
				minEnt = avgEnt;
			}
		}

		threshold = minEnt;
		if(min != -1)
			localOptimal = segments[min].getValue();
			

	}

	public double getEnt(){
		return threshold;
	}

	public double getSplit(){
		return localOptimal;
	}


	public double entropy(double [] dist, double distSize){
		
		double ent = 0.0;
		
		for(int i = 0; i < dist.length ; i++)
			if(dist[i] > 1E-12)
				ent += dist[i] * Math.log(dist[i]/distSize)/Math.log(2.0);

		return -1.0 * ent/distSize;
	}
	
	public double avgEntropy(double [] left, double [] right){

		double leftSize = 0.0, rightSize = 0.0;
		for(int i = 0; i < left.length; i++)
			leftSize += left[i];
		for(int i = 0; i < right.length; i++)
			rightSize += right[i];
		
		return (entropy(left, leftSize) * leftSize + entropy(right, rightSize) * rightSize) / noTuples;
		
	}

}
