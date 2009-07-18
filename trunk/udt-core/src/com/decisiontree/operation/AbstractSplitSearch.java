package com.decisiontree.operation;


public abstract class AbstractSplitSearch implements SplitSearch {

//	private Dispersion dispersion;
	private Split split;
	protected int noCls;
	
	
	public AbstractSplitSearch(Split split) {
//		this.dispersion = dispersion;
		this.split = split;
	}

	public void setSplit(Split split) {
		this.split = split;
	}

	protected Split getSplit(){
		return split;
	}
	
//	public Dispersion getDispersion() {
//		return dispersion;
//	}
//
//	public void setDispersion(Dispersion dispersion) {
//		this.dispersion = dispersion;
//	}

	@Override
	public double findDispersion(double [] dist, double distSize) {
		return getSplit().getDispersionMeasure().getDispersion(dist, distSize);
	}




}
