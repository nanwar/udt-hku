package com.decisiontree.build;

public class TreeUtil {
	// TODO

	public static boolean isSingleCls(double [] clsDist){
		int count = 0;
		for(int i = 0; i < clsDist.length; i++){
			if(clsDist[i] > 0) count++;
			if(count > 1) return true;
		}
		return false;
	}
	
	public static int findMajorityCls(double [] clsDist){
		int max = 0;
		for(int i = 1 ; i < clsDist.length ; i++){
			if(clsDist[max] < clsDist[i])
				max = i;
		}
		return max;
	}
	
	public static double findError(double [] clsDist, int majorityCls){

		double count =0.0;
		for(int i = 0 ; i < clsDist.length ; i++)
			if(i != majorityCls)
				count += clsDist[i];

		return count;

	}
	
	
	
}
