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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.decisiontree.data.PointAttribute;
import com.decisiontree.data.Range;
import com.decisiontree.data.RangeAttribute;
import com.decisiontree.param.GlobalParam;

/**
 * 
 * SampleDataGen - Generates interval-valued sampled distributed uncertain data from point-valued data.
 *
 * @author Smith Tsang
 * @version 28 May 2009
 *
 */
public class SampleDataGen extends RangeDataGen {

	private static Logger log = Logger.getLogger(SampleDataGen.class);
	
	private int noSamples;

	private long seed = GlobalParam.DEFAULT_SEED;

//	private boolean gaussian = true;

	public SampleDataGen(String input, int noSamples, boolean varies) {
		super(input, varies);
		setNoSamples(noSamples);
	}

	public SampleDataGen(String input, int noSamples, long seed, boolean varies) {
		super(input, varies);
		setNoSamples(noSamples);
		setSeed(seed);
	}
	
	public SampleDataGen(String input, int noSamples, int precision, long seed, boolean varies) {
		super(input, precision, varies);
		setNoSamples(noSamples);
		setSeed(seed);
	}
	
//	public SampleDataGen(String input, int noSamples, int precision,
//			boolean gaussian, long seed, boolean varies) {
//		super(input, precision, varies);
//		setNoSamples(noSamples);
//		setGaussian(gaussian);
//		setSeed(seed);
//	}

	private String getFileName(String input, int tupleNum, int attrNum){
		StringBuffer sb = new StringBuffer(input);
		sb.append(GlobalParam.SAMPLE_PATH);
		sb.append("T");
		sb.append(tupleNum);
		sb.append("A");
		sb.append(attrNum);
		return sb.toString();
		
	}

	@Override
	public void storeGeneratedData(String input, double[] range)  {
		BufferedWriter writer = null;
		BufferedReader reader = null;
		
		try {
			writer = new BufferedWriter(new FileWriter(input
					+ GlobalParam.SAMPLE_FILE));

			int noTuples = dataSet.getNoTuples();
			int noAttr = dataSet.getNoAttr();

			for (int k = 0; k < noAttr; k++) {
				if (!dataSet.isContinuous(k))
					continue;
				if (range != null) {
					setAttrRange(k, range[k] * dataSet.getDomainSize(k));
				}
			}

//			if (gaussian) {
			File pdf = new File(input + GlobalParam.SAMPLE_PATH);
			pdf.mkdir();
//			}
			reader = new BufferedReader(new FileReader(input
					+ GlobalParam.POINT_FILE));
			String data = "";
			for (int i = 0; (data = reader.readLine()) != null && i < noTuples; i++) {
				String dataArray[] = data.split(GlobalParam.SEPERATOR);
				for (int k = 0; k < noAttr; k++) {
					if (!dataSet.isContinuous(k)) {
						writer.write(dataArray[k] + GlobalParam.SEPERATOR);
						continue;
					}
					PointAttribute t = new PointAttribute(Double.parseDouble(dataArray[k]));
					RangeAttribute rt = genError(t, k);
			
//					if (gaussian) {
						Range rg = createPDF(getFileName(input, i,k),
								rt.getStart(), rt.getEnd());
						writer.write((rg.getStart() - 0.01) + GlobalParam.TO
								+ (rg.getEnd() + 0.01) + GlobalParam.SEPERATOR);
//					} else {
//						writer.write(rt.getStart() + Param.TO + rt.getEnd()+ Param.SEPERATOR);
//					}
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

	@Override
	protected void storeGeneratedTestData(String test, double[] range) {
	
		BufferedWriter writer = null;
		BufferedReader reader = null;
		try {
			writer = new BufferedWriter(new FileWriter(test
					+ GlobalParam.SAMPLE_FILE));

			int noAttr = dataSet.getNoAttr();

			File pdf = new File(test + GlobalParam.SAMPLE_PATH);
			pdf.mkdir();
			reader = new BufferedReader(new FileReader(test
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
				
//					if (gaussian) {
						Range rg = createPDF(getFileName(test , i , k), rt
								.getAbsStart(), rt.getAbsEnd());
						writer.write((rg.getStart() - 0.01) + GlobalParam.TO
								+ (rg.getEnd() + 0.01) + GlobalParam.SEPERATOR);
//					} else {
//
//						writer.write(rt.getAbsStart() + Param.TO + rt.getAbsEnd()
//										+ Param.SEPERATOR);
//					}

				}
				writer.write(dataArray[dataArray.length - 1]);
				writer.newLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	public static byte[] doubleToByte(double d) {
		byte[] b = new byte[8];
		long l = Double.doubleToRawLongBits(d);
		for (int i = 0; i < 8; i++) {
			b[i] = new Long(l).byteValue();
			l = l >> 8;
		}
		return b;
	}

	public Range createPDF(String filename, double start, double end) {
		
		BufferedOutputStream writer = null;
		try {
			writer = new BufferedOutputStream(
					new FileOutputStream(filename));
			double value = 0;
			Random r = new Random(seed++);
			
			double samples[] = new double[noSamples];
			for (int i = 0; i < noSamples; i++) {
				do
					value = r.nextGaussian();
				while (value >= 2 || value <= -2);
				samples[i] = value;
			}
			Arrays.sort(samples);
			byte[] b;
			for (int i = 0; i < noSamples; i++) {
				double data = start + (2 + samples[i]) * (end - start) / 4;
				b = doubleToByte(data);
				writer.write(b, 0, 8);
				double cdist = (i + 1) * 1.0 / noSamples;
				b = doubleToByte(cdist);
				writer.write(b, 0, 8);
			}

			Range rg = new Range(start + (2 + samples[0]) * (end - start) / 4,
					start + (2 + samples[noSamples - 1]) * (end - start) / 4);

			writer.close();
			return rg;
		} catch (Exception e) {
			System.out.println(e);
		} finally{
			try {
				if(writer != null) writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public void setNoSamples(int noSamples) {
		this.noSamples = noSamples;
	}

	public int getNoSamples() {
		return noSamples;
	}

//	public boolean isGaussian() {
//		return gaussian;
//	}
//
//	public void setGaussian(boolean gaussian) {
//		this.gaussian = gaussian;
//	}

	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
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
				System.out.println("\t-d <training>\t\tSpecify the training data for generation.");
				System.out
						.println("\t-t <testing>\t\tSpecify the testing data for generation. [default: no testing data]");
				System.out.println();
				System.out.println("Operations:");
				System.out.println("\t-n <noSamples>\t\tNo of samples of each numerical pdf used [default = 100]");
				System.out.println("\t-p <IntSize>\t\tUncertain interval size [default: 0.1]");
				System.out.println("\t-h\t\t\tHelp options");
				System.exit(1);
			}

			//default values
//			boolean gaussian = true;
			String training = "null";
			String testing = "null";
			boolean test = false;
			boolean varies = false;
			double width = GlobalParam.DEFAULT_WIDTH;
//			double stdev = 0.025;
			int noSamples = GlobalParam.DEFAULT_NO_SAMPLES;
			
			long seed = GlobalParam.DEFAULT_SEED;

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

				if (args[i].equals("-n")) {
					noSamples = Integer.parseInt(args[++i]);
				}

				if (args[i].equals("-p")) {
					width = Double.parseDouble(args[++i]);
				}

				if (args[i].equals("-s")) {
//					randomSeed = false;
					seed = Long.parseLong(args[++i]);
				}

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
			SampleDataGen gen = new SampleDataGen(training, noSamples, seed, varies);
			log.info("Initialization...");
			double range[] = new double[gen.getNoAttr()];

			for (int i = 0; i < gen.getNoAttr(); i++)
				range[i] = width;

			gen.storeGeneratedData(training, range);

			if (test)
				gen.storeGeneratedTestData(testing, range);
			//	System.out.println("...finished!");
			//	System.out.println("=====================================================");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

	// public static void main(String args[]){
	// try{
	//
	// boolean help = false;
	// // System.out.println("Welcome to Program for UDT - Data Generator");
	// //
	// System.out.println("=====================================================");
	//
	// for(int i = 0; i < args.length;i++)
	// if(args[i].equals("-h"))
	// help = true;
	//
	// if(help || args.length < 1){
	// System.out.println("Usage: java DataGen [-d <training>]");
	// System.out.println();
	// System.out.println("Options:");
	// System.out.println();
	// System.out.println("Data Type:");
	// System.out.println("\t-G\t\t\tGaussian Distribution");
	// System.out.println("\t-U\t\t\tUniform Distribution");
	// System.out.println();
	// System.out.println("Data Sets:");
	// System.out.println("\t-d <training>\t\tSpecify the training data for
	// generation.");
	// System.out.println("\t-t <testing>\t\tSpecify the testing data for
	// generation. [default: no testing data]");
	// System.out.println();
	// System.out.println("Operations:");
	// System.out.println("\t-n <noSamples>\t\tNo of samples of each numerical
	// pdf used [default = 100]");
	// System.out.println("\t-p <IntSize>\t\tUncertain interval size [default:
	// 0.1]");
	// System.out.println("\t-h\t\t\tHelp options");
	// System.exit(1);
	// }
	//
	// //default values
	// boolean gaussian = true;
	// String training = "null";
	// String testing = "null";
	// boolean test = false;
	// boolean varies = false;
	// double stdev = 0.025;
	// int noSamples = 100;
	//			
	//			
	// for(int i = 0; i < args.length; i++){
	// if(args[i].equals("-G")){
	// gaussian = true;
	// continue;
	// }
	// if(args[i].equals("-U")){
	// gaussian = false;
	// continue;
	// }
	//
	// if(args[i].equals("-d")){
	// training = args[++i];
	// continue;
	// }
	//
	// if(args[i].equals("-t")){
	// test = true;
	// testing = args[++i];
	// continue;
	// }
	//
	// if(args[i].equals("-n")){
	// noSamples = Integer.parseInt(args[++i]);
	// }
	//
	// if(args[i].equals("-p")){
	// stdev = Double.parseDouble(args[++i])/4;
	// }
	//				
	// if(args[i].equals("-s")){
	// randomSeed = false;
	// SEED = Long.parseLong(args[++i]);
	// }
	//
	// }
	//
	// if(training.equals("null")){
	// System.out.println("Please input training set using -d option.");
	// System.exit(1);
	// }
	//
	// if(!gaussian) stdev = stdev*4;
	//			
	// // System.out.print("Generating..." +(int)(stdev*400) + "%");
	// // System.out.println("Generation Mode: \t" + (gaussian? "GAUSSIAN" :
	// "UNIFORM"));
	// // if(gaussian)System.out.println("Uncertain Region Size: \t" + stdev*400
	// + "%");
	// // else System.out.println("Uncertain Region Length: \t+-" + stdev+
	// "units");
	// // if(gaussian)System.out.println("No of Samples: \t\t" + NOSAMPLES );
	// DataGen.GAUSSIAN = gaussian;
	// DataGen gen = new DataGen(training, noSamples, varies);
	// double range [] = new double[gen.getNoAttr()];
	//
	// for(int i = 0; i< gen.getNoAttr(); i++)
	// range[i] = stdev;
	//			
	// gen.storeErrorTerms(training, range);
	//
	// if(test) gen.storeTestError(testing, range);
	// // System.out.println("...finished!");
	// //
	// System.out.println("=====================================================");
	// }
	// catch(Exception e){
	// System.out.println(e);
	// System.exit(1);
	// }
	//
	// }

}
