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
package com.decisiontree.datagen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.decisiontree.data.PointAttribute;
import com.decisiontree.data.PointDataSet;
import com.decisiontree.data.PointDataSetInit;
import com.decisiontree.data.RangeAttribute;
import com.decisiontree.param.GlobalParam;

/**
 * 
 * RangeDataGen - Generates interval-valued uncertain data from point-valued data.
 *
 * @author Smith Tsang
 * @since 0.8
 *
 */
public class RangeDataGen implements DataGen{

	private static Logger log = Logger.getLogger(RangeDataGen.class);

	public static final int DEFAULT_PRECISION = 12;

	private boolean varies;

	private double[] attrRangeArray;
	
	private int[] precisionArray;

	protected PointDataSet dataSet;

	public RangeDataGen(String input, boolean varies) {
		PointDataSetInit init = new PointDataSetInit(input);
		dataSet = init.getDataSet();
		
		precisionArray = new int[dataSet.getNoAttr()];
		for (int i = 0; i < precisionArray.length; i++)
			setPrecision(i, DEFAULT_PRECISION);
		attrRangeArray = new double[dataSet.getNoAttr()];
		this.varies = varies;
	}

	public RangeDataGen(String input, int precision,
			boolean varies) {
		PointDataSetInit init = new PointDataSetInit(input);
		dataSet = init.getDataSet();
		this.precisionArray = new int[dataSet.getNoAttr()];
		for (int i = 0; i < precisionArray.length; i++)
			setPrecision(i, precision);
		attrRangeArray = new double[dataSet.getNoAttr()];
		this.varies = varies;
	}
	
	public int getPrecision(int pos) {
		if (pos < 0 || pos >= precisionArray.length) {
			log.error("Invalid position.");
			return -1;
		}
		return precisionArray[pos];
	}

	public void setPrecision(int pos, int decimalPlace) {
		if (pos < 0 || pos >= precisionArray.length) {
			log.error("Invalid position.");
			return;
		}
		precisionArray[pos] = decimalPlace;
	}
	

	public int[] getPrecisionArray(){
		return precisionArray;
	}

	public void setPrecisionArray(int[] precisionArray) {
		this.precisionArray = precisionArray;
	}

	public double getAttrRange(int pos) {
		if (pos < 0 || pos >= attrRangeArray.length) {
			log.error("Invalid position.");
			return -1;
		}
		return attrRangeArray[pos];
	}
	
	

	public void setAttrRange(int pos, double attrRange) {
		if (pos < 0 || pos >= attrRangeArray.length) {
			log.error("Invalid position.");
			return;
		}
		attrRangeArray[pos] = attrRange;
	}
	
	public double[] getAttrRangeArray() {
		return attrRangeArray;
	}

	public void setAttrRangeArray(double[] attrRangeArray) {
		this.attrRangeArray = attrRangeArray;
	}

	public int getNoAttr() {
		return dataSet.getNoAttr();
	}

	protected double roundingOff(double number, double decimalPlaces) {
		double value = 1.0;
		if (decimalPlaces > 0)
			value = Math.pow(10, decimalPlaces);
		double val = Math.rint(number * value) / value;
		return val;
	}

	protected int roundingToInt(double number) {
		int val = (int) (Math.rint(number) / 1.0);
		return val;
	}

	protected RangeAttribute genError(PointAttribute t, int pos) {

		double midVal = t.getValue();
		double left = 0;
		double right = 0;

		double varSize = 1.0;
		if (varies) {
			Random ran = new Random();
			do {
				varSize = ran.nextDouble();
			} while (varSize < 0.1);
		}

		left = midVal - getAttrRange(pos);
		right = midVal + getAttrRange(pos);	

		left = roundingOff(left, getPrecision(pos));
		right = roundingOff(right, getPrecision(pos));
		RangeAttribute r = new RangeAttribute(left, right);
		return r;

	}

	public void storeGeneratedData(String input, double[] width){
		BufferedWriter writer = null;
		BufferedReader reader = null;
		
		try {
			writer = new BufferedWriter(new FileWriter(input
					+ GlobalParam.RANGE_FILE));

			int noAttr = dataSet.getNoAttr();

			for (int k = 0; k < noAttr; k++) {
				if (!dataSet.isContinuous(k))
					continue;
				if (width != null) {
					
					log.info("Interval Width: " + width[k]);
					setAttrRange(k, width[k] * dataSet.getDomainSize(k));
				}

			}
			
			reader = new BufferedReader(new FileReader(input
					+ GlobalParam.POINT_FILE));
			String data = "";
			for (int i = 0; (data = reader.readLine()) != null; i++) {
				String dataArray[] = data.split(GlobalParam.SEPERATOR);
				for (int k = 0; k < noAttr; k++) {
					if (!dataSet.isContinuous(k)) {
						writer.write(dataArray[k] + GlobalParam.SEPERATOR);
						continue;
					}
					PointAttribute t = new PointAttribute(Double.parseDouble(dataArray[k]));
					RangeAttribute rt = genError(t, k);

					writer.write(rt.getAbsStart() + GlobalParam.TO + rt.getAbsEnd() + GlobalParam.SEPERATOR);
				}
				writer.write(dataArray[dataArray.length - 1]);
				writer.newLine();
			}

		} catch (IOException e) {
			e.printStackTrace();
			log.error("Cannot read or write dataset files. Please try again.");
			System.exit(1);
		}finally{
			try {
				if(writer != null) writer.close();
				if(reader != null) reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
		}

	}

	protected void storeGeneratedTestData(String test, double[] width) {
		
		BufferedWriter writer = null;
		BufferedReader reader = null;
		try {
			writer = new BufferedWriter(new FileWriter(test
					+ GlobalParam.RANGE_FILE));

			int noAttr = dataSet.getNoAttr();

			File pdf = new File(test + GlobalParam.SAMPLE_PATH);
			pdf.mkdir();
			reader = new BufferedReader(new FileReader(test + GlobalParam.POINT_FILE));
			String data = "";
			for (int i = 0; (data = reader.readLine()) != null; i++) {
				String dataArray[] = data.split(GlobalParam.SEPERATOR);
				for (int k = 0; k < noAttr; k++) {
					if (!dataSet.isContinuous(k)) {
						writer.write(dataArray[k] + GlobalParam.SEPERATOR);
						continue;
					}
					PointAttribute t = new PointAttribute(Double.parseDouble(dataArray[k]));
					RangeAttribute rt = genError(t, k);

					writer.write(rt.getAbsStart() + GlobalParam.TO + rt.getAbsEnd() + GlobalParam.SEPERATOR);

				}
				writer.write(dataArray[dataArray.length - 1]);
				writer.newLine();
			}

		} catch (IOException e) {
			e.printStackTrace();
			log.error("Cannot read or write dataset testing files. Please try again.");
			System.exit(1);
		} finally{
			try {
				if(writer != null) writer.close();
				if(reader != null) reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	public void storeGeneratedDataWithTest(String training, String testing, double[] width) {
		storeGeneratedData(training, width);
		if(testing != null) storeGeneratedTestData(testing, width);
	}



	public boolean isVaries() {
		return varies;
	}

	public void setVaries(boolean varies) {
		this.varies = varies;
	}
	
	public static void main(String args[]) {
		
	    BasicConfigurator.configure();
	    Logger.getRootLogger().setLevel(Level.INFO);

		try {

			boolean help = false;
			//	System.out.println("Welcome to Program for UDT - Data Generator");
			//	System.out.println("=====================================================");

			for (int i = 0; i < args.length; i++)
				if (args[i].equals("-h"))
					help = true;

			if (help || args.length < 1) {
				System.out.println("Usage: java DataGen [-d <training>]");
				System.out.println();
				System.out.println("Options:");
				System.out.println();
//				System.out.println("Data Type:");
//				System.out.println("\t-G\t\t\tGaussian Distribution");
//				System.out.println("\t-U\t\t\tUniform Distribution");
//				System.out.println();
				System.out.println("Data Sets:");
				System.out
						.println("\t-d <training>\t\tSpecify the training data for generation.");
				System.out
						.println("\t-t <testing>\t\tSpecify the testing data for generation. [default: no testing data]");
				System.out.println();
				System.out.println("Operations:");
//				System.out
//						.println("\t-n <noSamples>\t\tNo of samples of each numerical pdf used [default = 100]");
				System.out
						.println("\t-p <IntSize>\t\tUncertain interval size [default: 0.1]");
				System.out.println("\t-h\t\t\tHelp options");
				System.exit(1);
			}

			//default values
//			boolean gaussian = true;
			String training = "null";
			String testing = "null";
			boolean test = false;
			boolean varies = false;
			double width = 0.1;
//			double stdev = 0.025;
//			int noSamples = 100;

			for (int i = 0; i < args.length; i++) {
//				if (args[i].equals("-G")) {
//					gaussian = true;
//					continue;
//				}
//				if (args[i].equals("-U")) {
//					gaussian = false;
//					continue;
//				}

				if (args[i].equals("-d")) {
					training = args[++i];
					continue;
				}

				if (args[i].equals("-t")) {
					test = true;
					testing = args[++i];
					continue;
				}

//				if (args[i].equals("-n")) {
//					noSamples = Integer.parseInt(args[++i]);
//				}

				if (args[i].equals("-p")) {
					width = Double.parseDouble(args[++i]);
				}

//				if (args[i].equals("-s")) {
//					randomSeed = false;
//					SEED = Long.parseLong(args[++i]);
//				}

			}

			if (training.equals("null")) {
				System.out
						.println("Please input training set using -d option.");
				System.exit(1);
			}
//
//			if (!gaussian)
//				stdev = stdev * 4;

			//	System.out.print("Generating..." +(int)(stdev*400) + "%");
			//	System.out.println("Generation Mode: \t" + (gaussian? "GAUSSIAN" : "UNIFORM"));
			//	if(gaussian)System.out.println("Uncertain Region Size: \t" + stdev*400 + "%");
			//	else System.out.println("Uncertain Region Length: \t+-" + stdev+ "units");
			//	if(gaussian)System.out.println("No of Samples: \t\t" + NOSAMPLES );
//			RangeDataGen.GAUSSIAN = gaussian;
			log.info("Start generation...");
			RangeDataGen gen = new RangeDataGen(training, varies);
			log.info("Initialization...");
			double range[] = new double[gen.getNoAttr()];

			for (int i = 0; i < gen.getNoAttr(); i++)
				range[i] = width;

			if(! test) gen.storeGeneratedData(training, range);
			else
//			if (test)
				gen.storeGeneratedDataWithTest(training,testing, range);
			//	System.out.println("...finished!");
			//	System.out.println("=====================================================");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}


}
