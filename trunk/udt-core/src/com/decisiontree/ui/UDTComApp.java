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

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.decisiontree.datagen.RangeDataGen;
import com.decisiontree.datagen.SampleDataCleaner;
import com.decisiontree.datagen.SampleDataGen;
import com.decisiontree.eval.DispersionMeasure;
import com.decisiontree.function.DecisionTree;
import com.decisiontree.function.PointDecisionTree;
import com.decisiontree.function.RangeAvgDecisionTree;
import com.decisiontree.function.RangeDecisionTree;
import com.decisiontree.function.SampleAvgDecisionTree;
import com.decisiontree.function.SampleDecisionTree;
import com.decisiontree.measure.Times;
import com.decisiontree.operation.SplitSearch;
import com.decisiontree.operation.SplitSearchBP;
import com.decisiontree.operation.SplitSearchES;
import com.decisiontree.operation.SplitSearchFactory;
import com.decisiontree.operation.SplitSearchGP;
import com.decisiontree.operation.SplitSearchLP;
import com.decisiontree.operation.SplitSearchORI;
import com.decisiontree.operation.SplitSearchUD;
import com.decisiontree.operation.SplitSearchUnp;
import com.decisiontree.param.GlobalParam;

/**
 * 
 * UDTComApp - runs the main function of the UDT - Decision Tree for Uncertain Data.
 *
 * @author Smith Tsang
 * @since 0.8
 * @deprecated
 *
 */
class UDTComApp {

	private static Logger log = Logger.getLogger(UDTComApp.class);
	

	public static final String GEN = "GEN";
	public static final String BUILD = "BUILD";
	public static final String OVERALL = "OVERALL";
	public static final String CLEAN = "CLEAN";
	
	/**
	 * Generate interval-valued data from point data in training dataset
	 * @param training the training dataset file
	 * @param width the interval width (relative to domain)
	 * @param varies whether the interval width varies
	 */
	private static void generateData(String training,
			double width, boolean varies) {
		generateData(training, null, width, varies);
	}

	/**
	 * Generate interval-valued data from point data in training and testing dataset 
	 * @param training the training dataset file
	 * @param testing the testing dataset file
	 * @param width the interval width (relative to domain)
	 * @param varies whether the interval width varies
	 */
	private static void generateData(String training, String testing,
			double width, boolean varies) {
		
		log.info("Generating interval uncertain data");

		RangeDataGen gen = new RangeDataGen(training, varies);
		double widths[] = new double[gen.getNoAttr()];

		for (int i = 0; i < gen.getNoAttr(); i++)
			widths[i] = width;

		if(testing == null)
		gen.storeGeneratedData(training, widths);
		else
			gen.storeGeneratedDataWithTest(training, testing, widths);
	}

	/**
	 * 
	 * Generate interval-valued sample-distributed data from point data in training dataset
	 * @param training the training dataset file
	 * @param noSamples the number of samples
	 * @param width the interval width (relative to domain)
	 * @param seed the random-generate seed number
	 * @param varies whether the interval width varies
	 */
	private static void generateData(String training, int noSamples,
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
	private static void generateData(String training, String testing,
			int noSamples, double width, long seed, boolean varies) {
		
		log.info("Generating sampled interval uncertain data");

		SampleDataGen gen = new SampleDataGen(training, noSamples, seed, varies);
		double widths[] = new double[gen.getNoAttr()];

		for (int i = 0; i < gen.getNoAttr(); i++)
			widths[i] = width;

		if(testing == null)
			gen.storeGeneratedData(training, widths);
		else 
			gen.storeGeneratedDataWithTest(training, testing, widths);
	}


	/**
	 * Running the Console program
	 * @param args console program arguments
	 */
	public static void main(String args []){

		try{
			
			PropertyConfigurator.configure(GlobalParam.LOG_FILE);
			String [] infoMessage = 
					{
					"UDT Version 0.85  Copyright (C) 2009 Database Group, ",
					"Department of COmputer Science, The University of Hong Kong ",
					"Please use class com.decisiontree.app.UDTApp instead!",
					"For more details, Please refer to UPDATE."
					};
			
			for(int i = 0; i < infoMessage.length; i++){
				log.info(infoMessage[i]);
				System.out.println(infoMessage[i]);
				System.exit(0);
			}
				
			log.info("Starting...");		
	
			boolean help = false;
	
			for(int i = 0; i < args.length;i++)
				if(args[i].equals("-h") || args[i].equals("-help"))
					help = true;
	
			if(help || args.length < 1){
				System.out.println("Usage: Please refer to the README for instructions.");
				System.exit(1);
			}
			log.info("Configure...");
			
			// default values
			String training = null;
			String testing = null;
			int noSamples = GlobalParam.DEFAULT_NO_SAMPLES;
			double nodeSize = GlobalParam.DEFAULT_NODESIZE;
			double pruningThreshold = GlobalParam.DEFAULT_THRESHOLD;
			
			String mode = BUILD;
			String type = DecisionTree.BUILD;
			String algorithm = SplitSearch.AVG;
			
			double width = GlobalParam.DEFAULT_WIDTH;
			
			long seed = GlobalParam.DEFAULT_SEED;
			
			boolean varies = false; //NOT SUPPORT IN THIS VERSION
			
			int trials = 1;
			boolean saveToFile = false;
			String resultFileName = GlobalParam.RESULT_FILE;
		
			for(int i = 0; i < args.length; i++){
				String param = args[i];
				if( i+1 < args.length ){
					String value = args[i+1];
					if(param.equals("-mode") || param.equals("-m")){
						if(value.equalsIgnoreCase("gen"))
							mode = GEN;
						else if(value.equalsIgnoreCase("overall"))
							mode = OVERALL;
						else if(value.equalsIgnoreCase("build"))
							mode = BUILD;
						else if(value.equalsIgnoreCase("clean"))
							mode = CLEAN;
					}
					else if(param.equals("-dataset") || param.equals("-d")){
						training = value;
						continue;
					}
	
					else if(param.equals("-test") || param.equals("-t")){
						testing = value;
						continue;
					}
					
					else if(param.equals("-sample") || args[i].equals("-s")){
						noSamples = Integer.parseInt(value);
					}
					
					else if(param.equals("-algorithm") || param.equals("-a")){
						if(value.equalsIgnoreCase("udt")) 
							algorithm = SplitSearch.UDT;
						else if(value.equalsIgnoreCase("udtbp"))
							algorithm = SplitSearch.UDTBP;
						else if(value.equalsIgnoreCase("udtlp"))
							algorithm = SplitSearch.UDTLP;
						else if(value.equalsIgnoreCase("udtgp"))
							algorithm = SplitSearch.UDTGP;
						else if(value.equalsIgnoreCase("udtes"))
							algorithm = SplitSearch.UDTES;
						else if(value.equalsIgnoreCase("avg")) 
							algorithm = SplitSearch.AVG;
						else if(value.equalsIgnoreCase("udtud"))
							algorithm = SplitSearch.UDTUD;
						else if(value.equalsIgnoreCase("avgud"))
							algorithm = SplitSearch.AVGUD;
						else if(value.equalsIgnoreCase("point"))
							algorithm = SplitSearch.POINT;
					}
					
					
					if(mode.equals(BUILD) || mode.equals(OVERALL)){
						if(param.equals("-type") || param.equals("-y")){
			
								if(value.equalsIgnoreCase("xfold"))
									type = DecisionTree.XFOLD;
								else if (value.equalsIgnoreCase("accuracy"))
									type = DecisionTree.ACCUR;
								else type = DecisionTree.BUILD;
							
						}
						
						else if(param.equals("-nodesize") || param.equals("-n")){
							nodeSize = Double.parseDouble(value);
							if(nodeSize < GlobalParam.DEFAULT_NODESIZE) nodeSize = GlobalParam.DEFAULT_NODESIZE;
						}
						else if(param.equals("-purity") || param.equals("-h")){
							pruningThreshold = Double.parseDouble(value);
							if(pruningThreshold > GlobalParam.DEFAULT_THRESHOLD) pruningThreshold = GlobalParam.DEFAULT_THRESHOLD;
						}
	
					
					}
					if(mode.equals(GEN) || mode.equals(OVERALL)){
						if (param.equals("-width") || param.equals("-p")) {
							width = Double.parseDouble(value);
						}
						else if(param.equals("-seed") || param.equals("-e")){
							seed = Integer.parseInt(value);
						}
					}	
					
					if(mode.equals(OVERALL)){
						if (param.equals("-trial") || param.equals("-l"))
							trials = Integer.parseInt(value);
						if (param.equals("-save") || param.equals("-v")){
							saveToFile = true;
							resultFileName = value;
						}
					}
					
				}
	
			}
			
			// if training data file is not specified
			if(training == null){
				log.error("No training set is specified."); 
				System.out.println("Please input training set using -d or -dataset option."); 
				System.exit(1);
			}
				
	
			if(mode.equals(GEN) ){
				System.out.println("You are running generate mode.");
				System.out.println("The generate data would be stored in the same folder of the source data file.");
				log.info("Generating Uncertain Data...");
				if(algorithm.equals(SplitSearch.POINT))
					log.info("No Uncertain Data Generation Required.");
				if(algorithm.equals(SplitSearch.UDTUD) || algorithm.equals(SplitSearch.AVGUD)){
					if(testing == null)
						generateData(training, width, varies);
					else generateData(training, testing, width, varies);
				}
				else{
					if(testing == null)
						generateData(training, noSamples, width, seed, varies);
					else generateData(training, testing, noSamples, width, seed, varies);
				}
			}
			
			if(mode.equals(BUILD) ){
				System.out.println("You are running build mode.");
				SplitSearch splitSearch = SplitSearchFactory.createSplitSearch(algorithm, DispersionMeasure.ENTROPY);

				
				final Times start = new Times();
				DecisionTree decisionTree = null;
				if(algorithm.equals(SplitSearch.AVG))
					decisionTree = new SampleAvgDecisionTree(splitSearch, nodeSize, pruningThreshold);
				else if(algorithm.equals(SplitSearch.UDTUD))
					decisionTree = new RangeDecisionTree(splitSearch, nodeSize, pruningThreshold);
				else if (algorithm.equals(SplitSearch.POINT))
					decisionTree = new PointDecisionTree(splitSearch, nodeSize, pruningThreshold);
				else if(algorithm.equals(SplitSearch.AVGUD))
					decisionTree = new RangeAvgDecisionTree(splitSearch, nodeSize, pruningThreshold);
				else
					decisionTree = new SampleDecisionTree(splitSearch, nodeSize, pruningThreshold);
				
				
				if(type.equals(DecisionTree.BUILD)){
					log.info("Timing...");

					decisionTree.buildTree(training);
					final Times end = new Times();

					System.out.println("Building Time: " );
					end.difference(start).printTime();
					System.out.println("No of Entropy Calculation: " + GlobalParam.getNoEntCal());
					
				}else if(type.equals(DecisionTree.ACCUR)){
					log.info("Finding Accuracy...");
					double accuracy = 0.0;
					
					if(training == null)
						accuracy = decisionTree.findAccuracy(training);
					else accuracy = decisionTree.findAccuracy(training, testing);
					
					accuracy = Math.rint(accuracy *10000)/10000;
					System.out.println("Testing Accuracy: " + accuracy);
				}else if(type.equals(DecisionTree.XFOLD)){
					log.info("Finding Accuracy by crossfold");
					double accuracy = decisionTree.crossFold(training);
					
					accuracy = Math.rint(accuracy *10000)/10000;
					System.out.println("Cross-Fold Accuracy: " + accuracy);
				}
	
			}
			if(mode.equals(CLEAN)){
				System.out.println("You are running clean mode. The operation cannot be rollbacks.");
				SampleDataCleaner cleaner = new SampleDataCleaner();
				if(testing == null)
					cleaner.cleanGeneratedData(training);
				else cleaner.cleanGeneratedDataWithTest(training, testing);
				
				System.out.println("Data is cleaned successfully.");
				
			}
			
			if( mode.equals(OVERALL)){ // OVERALL Allows multiple trials and file save for data.
				System.out.println("You are running overall mode. Reminded that the generated data would NOT be cleaned.");
				BufferedWriter writer = null;
				if(saveToFile){
					File resultFile = new File(resultFileName);
					if(!resultFile.exists()){
						if(resultFile.getParent() != null){
							resultFile.getParentFile().mkdirs();
						}
						resultFile.createNewFile();
					}
					writer = new BufferedWriter(new FileWriter(resultFileName));
					writer.write("Decision Tree Build Type = " + type );
					writer.newLine();
					writer.write("Algorithm,Trail,NoEntropyCal,UserTime,SystemTime");
					writer.newLine();
				}
				for(int i = 0 ; i < trials ; i++){
				
					log.info("Generating Uncertain Data...");

					if(algorithm.equals(SplitSearch.POINT))
						log.info("No Uncertain Data Generation Required.");
					else if(testing == null){
						if(algorithm.equals(SplitSearch.UDTUD) || algorithm.equals(SplitSearch.AVGUD))
							generateData(training, width, varies);
						else generateData(training, noSamples, width, seed+i, varies);
					}
					else{
						if(algorithm.equals(SplitSearch.UDTUD) || algorithm.equals(SplitSearch.AVGUD))
							generateData(training, testing, width, varies);
						else generateData(training, testing, noSamples, width, seed+i, varies);
					}
					
					SplitSearch splitSearch = SplitSearchFactory.createSplitSearch(algorithm, DispersionMeasure.ENTROPY);
					
					final Times start = new Times();
					DecisionTree decisionTree = null;
					if(algorithm.equals(SplitSearch.AVG))
						decisionTree = new SampleAvgDecisionTree(splitSearch, nodeSize, pruningThreshold);
					else if(algorithm.equals(SplitSearch.UDTUD))
						decisionTree = new RangeDecisionTree(splitSearch, nodeSize, pruningThreshold);
					else if (algorithm.equals(SplitSearch.POINT))
						decisionTree = new PointDecisionTree(splitSearch, nodeSize, pruningThreshold);
					else if(algorithm.equals(SplitSearch.AVGUD))
						decisionTree = new RangeAvgDecisionTree(splitSearch, nodeSize, pruningThreshold);
					else
						decisionTree = new SampleDecisionTree(splitSearch, nodeSize, pruningThreshold);


					
					if(type.equals(DecisionTree.BUILD)){
						log.info("Timing...");
						
						decisionTree.buildTree(training);
						final Times end = new Times();
						
						if(saveToFile){ // Save to File
							writer.write(algorithm + "," + i + "," + GlobalParam.getNoEntCal() +"," +
									end.getUserTime() + "," + end.getSystemTime());
							writer.newLine();
							
						}else{
							// Printing result to console
							System.out.println("Building Time: " );
							end.difference(start).printTime();
							System.out.println("No of Entropy Calculation: " + GlobalParam.getNoEntCal());
						}	
						
					}else if(type.equals(DecisionTree.ACCUR)){
						log.info("Finding Accuracy...");
						double accuracy = 0.0;
						
						if(training == null)
							accuracy = decisionTree.findAccuracy(training);
						else accuracy = decisionTree.findAccuracy(training, testing);

						accuracy = Math.rint(accuracy *10000)/10000;
						
						if(saveToFile){ // Save to File
							writer.write(algorithm + "," + i + "," + GlobalParam.getNoEntCal() + "," +
									accuracy);
							writer.newLine();
						}else System.out.println("Testing Accuracy: " + accuracy);
					}else if(type.equals(DecisionTree.XFOLD)){
						log.info("Finding Accuracy by crossfold");
						double accuracy = decisionTree.crossFold(training);
						accuracy = Math.rint(accuracy *10000)/10000;
						if(saveToFile){ // Save to File
							writer.write(algorithm + "," + i + "," + GlobalParam.getNoEntCal() + "," +
									accuracy);
							writer.newLine();
						}else{
							System.out.println("Cross-Fold Accuracy: " + accuracy);
						}

					}
					GlobalParam.clearStoredValues();
				}
				if(saveToFile){
					System.out.println("The result data is saved in " + resultFileName +".");
					writer.close();
				}
			}

			log.info("The program terminates successfully.");
			System.out.println("The job has finished successfully.");
		}catch(Exception e){
			e.printStackTrace();
			log.info("Internal Errors. The Program Terminates");
			System.out.println("Internal Errors. Please check if your input is incorrect.");
			System.exit(1);
		}
	}

}
