package com.decisiontree.eval;

public interface Dispersion {

	public double getDispersion(double [] dist, double distSize);

	public double averageDispersion(double [] left, double [] right, double noTuples);

	public double findLowerBound(double [] left, double [] right, double [] region, double noTuples, double noCls);

}
