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
package com.decisiontree.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.decisiontree.datagen.RangeDataGen;
import com.decisiontree.datagen.SampleDataCleaner;
import com.decisiontree.datagen.SampleDataGen;
import com.decisiontree.eval.DispersionMeasure;
import com.decisiontree.file.FileUtil;
import com.decisiontree.function.DecisionTree;
import com.decisiontree.function.DecisionTreeFactory;
import com.decisiontree.function.PointDecisionTree;
import com.decisiontree.function.RangeAvgDecisionTree;
import com.decisiontree.function.RangeDecisionTree;
import com.decisiontree.function.SampleAvgDecisionTree;
import com.decisiontree.function.SampleDecisionTree;
import com.decisiontree.measure.Times;
import com.decisiontree.operation.SplitSearch;
import com.decisiontree.operation.SplitSearchFactory;
import com.decisiontree.param.GlobalParam;

/**
 *
 * UDTFunctions
 *
 * @author Smith Tsang
 * @since 0.9
 *
 */
public class UDTFunctions {
	
	public static Logger log = Logger.getLogger(UDTFunctions.class);
	
	/**
	 * Generate interval-valued data from point data in training dataset
	 * @param training the training dataset file
	 * @param width the interval width (relative to domain)
	 * @param varies whether the interval width varies
	 */
	private void generateData(String training, String nameFile,
			double width, boolean varies) {
		generateData(training, null, nameFile, width, varies);
	}

	/**
	 * Generate interval-valued data from point data in training and testing dataset 
	 * @param training the training dataset file
	 * @param testing the testing dataset file
	 * @param width the interval width (relative to domain)
	 * @param varies whether the interval width varies
	 */
	private void generateData(String training, String testing, String nameFile,
			double width, boolean varies) {
		
		log.info("Generating interval uncertain data");

		RangeDataGen gen = new RangeDataGen(training, nameFile, varies);
		double widths[] = new double[gen.getNoAttr()];

		for (int i = 0; i < gen.getNoAttr(); i++)
			widths[i] = width;

		if(testing == null)
			gen.storeGeneratedData(training, widths);
		else
			gen.storeGeneratedDataWithTest(training, testing, widths);
	}

	/**
	 * Generate interval-valued sample-distributed data from point data in training dataset
	 * @param training the training dataset file
	 * @param noSamples the number of samples
	 * @param width the interval width (relative to domain)
	 * @param seed the random-generate seed number
	 * @param varies whether the interval width varies
	 */
	private void generateData(String training, String nameFile, int noSamples,
			double width, long seed,  boolean varies) {
		generateData(training, null, noSamples, width, seed,  varies);
	}

	/**
	 * Generate interval-valued sample-distributed data from point data in training dataset
	 * @param training the training dataset file
	 * @param testing the testing dataset file
	 * @param noSamples the number of samples
	 * @param width the interval width (relative to domain)
	 * @param seed the random-generate seed number
	 * @param varies whether the interval width varies
	 */
	private void generateData(String training, String testing, String nameFile,
			int noSamples, double width, long seed, boolean varies) {
		
		log.info("Generating sampled interval uncertain data");

		SampleDataGen gen = new SampleDataGen(training, nameFile, noSamples, seed, varies);
		double widths[] = new double[gen.getNoAttr()];

		for (int i = 0; i < gen.getNoAttr(); i++)
			widths[i] = width;

		if(testing == null)
			gen.storeGeneratedData(training, widths);
		else 
			gen.storeGeneratedDataWithTest(training, testing, widths);
	}



	/**
	 * Generate data mode with different algorithms and settings
	 * @param training the training dataset file
	 * @param testing the testing dataset file
	 * @param nameFile the property file
	 * @param algorithm the algorithm
	 * @param noSamples the number of samples
	 * @param width the width of the interval
	 * @param seed the seed of the random guassian samples
	 * @param varies whether the interval width varies
	 */
    public void generateMode(String training, String testing, String nameFile, String algorithm, int noSamples, double width, long seed, boolean varies){
//		System.out.println("You are running generate mode.");
//		System.out.println("The generate data would be stored in the same folder of the source data file.");
		log.info("Generating Uncertain Data...");
		if(algorithm.equals(SplitSearch.POINT))
			log.info("No Uncertain Data Generation Required.");
		if(algorithm.equals(SplitSearch.UDTUD) || algorithm.equals(SplitSearch.AVGUD)){
			if(testing == null)
				generateData(training, nameFile, width, varies);
			else 
			generateData(training, testing, nameFile, width, varies);
		}
		else{
			if(testing == null)
				generateData(training, testing, noSamples, width, seed, varies);
			else 
			generateData(training, testing, nameFile, noSamples, width, seed, varies);
		}
    }
    
 
    

    
    public String buildMode(String training, String testing, String nameFile, String algorithm, String type,  double nodeSize, double pruningThreshold){

    	// Currently using entropy
    	SplitSearch splitSearch = SplitSearchFactory.createSplitSearch(algorithm, DispersionMeasure.ENTROPY); 
    	if(splitSearch == null){
    		log.error("Incorrect algorithm specified.");
    		return null;
    	}
		
		final Times start = new Times();

		DecisionTree decisionTree = DecisionTreeFactory.createDecisionTree(algorithm, splitSearch, nodeSize, pruningThreshold);
		
		String result = "";

		if(type.equals(DecisionTree.BUILD)){
			log.info("Timing...");

			decisionTree.buildTree(training, nameFile);
			final Times end = new Times();

			System.out.println("Building Time: " );
			end.difference(start).printTime();
			
			result = GlobalParam.getNoEntCal() +"," + end.getUserTime() + "," + end.getSystemTime();
			
//			System.out.println("No of Entropy Calculation: " + GlobalParam.getNoEntCal());
			
		}else if(type.equals(DecisionTree.ACCUR)){
			log.info("Finding Accuracy...");
			double accuracy = 0.0;
			
			if(training == null)
				accuracy = decisionTree.findAccuracy(training, nameFile);
			else accuracy = decisionTree.findAccuracy(training, testing, nameFile);
			
			accuracy = Math.rint(accuracy *10000)/10000;
			result = GlobalParam.getNoEntCal() + "," +accuracy;
//			System.out.println("Testing Accuracy: " + accuracy);
//			return accuracy + "";
		}else if(type.equals(DecisionTree.XFOLD)){
			log.info("Finding Accuracy by crossfold");
			double accuracy = decisionTree.crossFold(training, nameFile);
			
			accuracy = Math.rint(accuracy *10000)/10000;
			result = GlobalParam.getNoEntCal() + "," + accuracy;
//			System.out.println("Cross-Fold Accuracy: " + accuracy);
			
		}
		return result;

    }

    public void cleanMode(String training, String testing){
    	
//		System.out.println("You are running clean mode. The operation cannot be rollbacks.");
		SampleDataCleaner cleaner = new SampleDataCleaner();
		if(testing == null)
			cleaner.cleanGeneratedData(training);
		else cleaner.cleanGeneratedDataWithTest(training, testing);
		
//		System.out.println("Data is cleaned successfully.");

    }
    
    public String overallMode(String training, String testing, String nameFile,String algorithm, String type, int noSamples, double width, long seed, boolean varies,
    		double nodeSize, double pruningThreshold/*, int trial*/){
    	
		generateMode(training,testing,nameFile,algorithm,noSamples,width,seed,varies);
		String result = buildMode(training, testing, nameFile, algorithm, type, nodeSize, pruningThreshold);
		GlobalParam.clearStoredValues();
		
		return result;
//		return algorithm + "," + trial + "," +result;
    
    }
    
    public List<String> overallMode(String training, String testing, String nameFile, String algorithm, String type, int noSamples, double width, long seed, boolean varies,
    		double nodeSize, double pruningThreshold, int noTrials){
    	List<String> resultList = new ArrayList<String>(noTrials);
    	for(int i =0; i < noTrials; i++)
    		resultList.add(algorithm + ","+ i + "," +
    				overallMode(training,testing,nameFile,algorithm,type,noSamples,width,seed+i,varies,nodeSize,pruningThreshold));
    		
    	return resultList;
    	
    }
    
    
    public void overallMode(String training, String testing, String nameFile, String algorithm, String type, int noSamples, double width, long seed, boolean varies,
    		double nodeSize, double pruningThreshold, int noTrials, String resultFileName) throws IOException{
//    	System.out.println("You are running overall mode. Reminded that the generated data would NOT be cleaned.");
		BufferedWriter writer = null;
//		if(saveToFile){
		FileUtil.createFileWithDirectory(resultFileName);
		writer = new BufferedWriter(new FileWriter(resultFileName));
		writer.write("Decision Tree Build Type = " + type );
		writer.newLine();
		writer.write("Algorithm,Trail,NoEntropyCal,UserTime,SystemTime");
		writer.newLine();
//		}
		for(int i = 0 ; i < noTrials ; i++){
			writer.write(algorithm + "," + i + "," + 
					overallMode(training,testing, nameFile, algorithm,type,noSamples,width,seed+i,varies,nodeSize,pruningThreshold));
			writer.newLine();
		}
		
//		if(saveToFile){
//			System.out.println("The result data is saved in " + resultFileName +".");
		if(writer != null) 
			writer.close();
//		}
    }

}
