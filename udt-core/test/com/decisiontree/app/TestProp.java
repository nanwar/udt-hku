package com.decisiontree.app;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.BasicConfigurator;

class TestProp {

	public static void main(String args[]){

		BasicConfigurator.configure();

		try {
			UDTApp.loadProp(new File("run.properties"));
			UDTApp.printProp();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


}
