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

import com.decisiontree.eval.Dispersion;
import com.decisiontree.eval.Entropy;

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
	protected Dispersion dispersion;

	public BinarySplit(Dispersion dispersion){
		this.dispersion = dispersion;
	}

	public BinarySplit(double noTuples, int noCls){
		this(new Entropy(), noTuples, noCls);
	}

	public BinarySplit(Dispersion dispersion, double noTuples, int noCls){
		init(noTuples, noCls);
		this.dispersion = dispersion;
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

			double avgEnt = dispersion.averageDispersion(left,right, noTuples);
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

	@Override
	public void init(double noTuples, int noCls) {
		this.noTuples = noTuples;
		this.noCls = noCls;
		this.threshold = Double.POSITIVE_INFINITY;
		this.localOptimal = Double.POSITIVE_INFINITY;

	}


}
