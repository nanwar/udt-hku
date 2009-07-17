package com.decisiontree.eval;

import com.decisiontree.param.GlobalParam;

public class Entropy implements Dispersion{


	public double getDispersion(double [] dist, double distSize){

		double entropy = 0.0;

		for(int i = 0; i < dist.length ; i++)
			if(dist[i] > GlobalParam.DOUBLE_PRECISION)
				entropy += dist[i] * Math.log(dist[i]/distSize)/Math.log(2.0);

		return -1.0 * entropy/distSize;
	}

	public double averageDispersion(double [] left, double [] right, double noTuples){

		double leftSize = getDistSum(left);
		double rightSize = getDistSum(right);

		return (getDispersion(left, leftSize) * leftSize +
				getDispersion(right, rightSize) * rightSize) / noTuples;

	}

	private double getDistSum(double [] dist){
		double distSize =0 ;
		for(int i = 0; i < dist.length; i++)
			distSize += dist[i];
		return distSize;

	}


	public double findLowerBound(double [] left, double [] right, double [] region, double noTuples, double noCls){

		double leftSize = 0.0, rightSize = 0.0;
		for(int i = 0 ; i < noCls; i++){
			leftSize += left[i];
			rightSize += right[i] - region[i];
		}

		double ent = 0.0;
		for(int i = 0 ; i < noCls; i++){

			double leftT = left[i] + region[i];
			double logValueL = 0;
			if( leftT > GlobalParam.DOUBLE_PRECISION){
				logValueL = Math.log(leftT/(leftSize + region[i]))/Math.log(2.0);
			}
			double sumLeft = left[i] * logValueL;

			double logValueR = 0;
			if(right[i] > GlobalParam.DOUBLE_PRECISION)
				logValueR = Math.log(right[i]/(rightSize + region[i]))/Math.log(2.0);
			double sumRight = (right[i] - region[i]) * logValueR;


			double remain = 0;
			if(region[i] > GlobalParam.DOUBLE_PRECISION){
				if(logValueL > logValueR)
					remain = region[i] * logValueL;
				else remain = region[i] * logValueR;
			}

			ent +=  (sumLeft + sumRight + remain);
		}

		return -1.0 * ent/noTuples;
	}

}
