package com.decisiontree.operation;

import com.decisiontree.eval.Dispersion;

public abstract class AbstractSplitSearch implements SplitSearch {

	private Dispersion dispersion;
	private Split split;

	public Dispersion getDispersion() {
		return dispersion;
	}

	public void setDispersion(Dispersion dispersion) {
		this.dispersion = dispersion;
	}

	protected Split getSplit(){
		return split;
	}


}
