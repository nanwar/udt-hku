/**
 * Decision Tree Classification With Uncertain Data (UDT)
 * Copyright (C) 2009, The Database Group, 
 * Department of Computer Science, The University of Hong Kong
 * 
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
package com.decisiontree.operation;

import java.util.Arrays;
import java.util.List;

import com.decisiontree.data.SampleAttrClass;
import com.decisiontree.data.Tuple;
import com.decisiontree.param.GlobalParam;

/**
 * 
 * SplitSearchES - finding the best split point for a set of data using end-point sampling technique.
 *
 * @author Smith Tsang
 * @since 0.8
 *
 */
public class SplitSearchES extends SplitSearchGP {

	protected double [] getEndPtSet(SampleAttrClass [] attrClassSet, int pos){
		
		int noTuples = attrClassSet.length;
		int len = noTuples *2;
		double [] endPtSet = new double[len];
		for(int  i =0 ; i < noTuples; i++){
			endPtSet[2*i] = attrClassSet[i].getStart(); 
			endPtSet[2*i +1] = attrClassSet[i].getEnd();
		}

		Arrays.sort(endPtSet);
		
		log.debug("End Pts: " + endPtSet[0] + " " + endPtSet[1]);

		return endPtSet;

	}


	private Histogram [] SegGen(SampleAttrClass [] attrClassSet,  double [] endPtSet, int noCls){

		int noTuples = attrClassSet.length;
		int noEndPts = endPtSet.length;
//		int len = noTuples *2;
		int maxNoSegments = noEndPts -1;
		Histogram tempSegmentList [] = new Histogram[maxNoSegments];
		int noSampledSegements = 0;
		int previous = 0;
		for(int i = GlobalParam.SAMPLING -1 ; i < noEndPts; i += GlobalParam.SAMPLING){
			tempSegmentList[noSampledSegements++] = new Histogram(noCls,endPtSet[previous], endPtSet[i]);
			previous = i;
		}

		if(previous < noEndPts-1) tempSegmentList[noSampledSegements++] = new Histogram(noCls, endPtSet[previous], endPtSet[noEndPts-1]);

		maxNoSegments = noSampledSegements;
		
		int temp =0;
//		double prev = 0.0, cdist = 0.0, next = 0.0, diff = 0.0;
//		double size = 0;
		double next = 0.0;
		int prevPos = -1, currPos = -1, startPos = -1, endPos = -1;

	//	double histSize = 0, histSize2 =0;
		for(int i = 0; i < noTuples; i++){

	//		histSize2 += r[i].getCurFrac() * r[i].getWeight();
			for(;temp < maxNoSegments && attrClassSet[i].getStart() > tempSegmentList[temp].getEnd();temp++);
			if(temp >= maxNoSegments) break;

			startPos = attrClassSet[i].getStartPos();
			endPos = attrClassSet[i].getEndPos();
			
			currPos = startPos;
			prevPos = startPos; // prevPos;
			
			//Assume pos1 < pos2

			next = attrClassSet[i].getSampleValue(startPos+1); // need to add


			for(int rtemp = temp; rtemp < maxNoSegments && attrClassSet[i].getEnd() > tempSegmentList[rtemp].getStart(); rtemp++){
				if(tempSegmentList[rtemp].getEnd() < next) continue;
				
				currPos = attrClassSet[i].getNearSample(currPos+1, tempSegmentList[rtemp].getEnd());

			 	double frac = 0.0;
				if(currPos <= startPos) frac = 0.0;
				else if(currPos >= endPos) frac = attrClassSet[i].getFrac(prevPos, endPos); // need to add 
				else frac = attrClassSet[i].getFrac(prevPos, currPos);	
				
				tempSegmentList[rtemp].addCls(attrClassSet[i].getCls(), frac * attrClassSet[i].getWeight());
	//			histSize += frac * r[i].getWeight();
				if(currPos >= endPos || frac >= (1.0 - 1E-10)) break;
				next = attrClassSet[i].getSampleValue(currPos+1);
				prevPos = currPos;
			}

		}

		
	//	System.out.println("HistSize: " + histSize + " " + histSize2) ;	
		

		int noPrunedSegments = 0;
		if(tempSegmentList[0].empty()) {tempSegmentList[0] = null; noPrunedSegments++;}
		for(int i = 1; i < maxNoSegments; i++){
			if(tempSegmentList[i].empty()) {
				tempSegmentList[i] = tempSegmentList[i-1];
				tempSegmentList[i-1] = null; 
				noPrunedSegments++;
				continue;
			}
			if(tempSegmentList[i].mulCls() || tempSegmentList[i-1] == null) continue;
			if(tempSegmentList[i-1].singleCls() == tempSegmentList[i].singleCls()){
				tempSegmentList[i-1].mergeHist(tempSegmentList[i]);
				tempSegmentList[i] = tempSegmentList[i-1];
				tempSegmentList[i-1] = null;
				noPrunedSegments++;
			}
		}
		
		Histogram [] segmentSet = new Histogram[maxNoSegments - noPrunedSegments];
		int count = 0;
		for(int i = 0; i < maxNoSegments; i++)
			if(tempSegmentList[i] != null){
				segmentSet[count++] = tempSegmentList[i];
		 	}

		return segmentSet;	

	}
	
	@Override
	public SplitData findBestAttr(List<Tuple> data, int noCls, int noAttr) {
//		int min = -1;
		SplitData splitData = new SplitData();
		splitData.setEnt(Double.POSITIVE_INFINITY);
		double totalTuples = Tuple.countWeightedTuples(data);		
		log.debug("Total Tuples: " + totalTuples);
		
		BinarySplitES binarySplit = new BinarySplitES(totalTuples, noCls);

		Histogram allSegmentSet [][] = new Histogram[noAttr][];
		SampleAttrClass allAttrClassSet[][] = new SampleAttrClass[noAttr][];
		double allEndptSet [][] = new double[noAttr][];
		double allLowerBoundSet[][] = new double[noAttr][];

		for(int i = 0 ; i < noAttr; i++){
			allAttrClassSet[i] = getSampleAttrClass(data, i);
			allEndptSet[i] = getEndPtSet(allAttrClassSet[i], i);
			allSegmentSet[i]  = SegGen(allAttrClassSet[i], allEndptSet[i], noCls);
		
			log.debug("Histogram size: " + allSegmentSet[i].length);
//			Param.noEndPtIntervals += allSegmentSet[i].length;
			GlobalParam.addNoEndPtIntervals(allSegmentSet[i].length);
			
			allLowerBoundSet[i] = binarySplit.preProcess(allSegmentSet[i]);

			if(allSegmentSet[i].length == 1) continue;
			double localEnt = binarySplit.getEnt();
			if(localEnt < splitData.getEnt()){
				splitData.setEnt(localEnt);
				splitData.setSplit(binarySplit.getSplit());
				splitData.setAttrNum(i);
			}
		}
		for(int i = 0 ; i < noAttr; i++){

			binarySplit.run(allSegmentSet[i], allEndptSet[i], allLowerBoundSet[i], allAttrClassSet[i], splitData.getEnt());
			if(!binarySplit.isAttrPruned()){
				double localEnt = binarySplit.getEnt();
				
				if(splitData.getEnt() - localEnt > 1E-12){
					splitData.setEnt(localEnt);
					splitData.setSplit(binarySplit.getSplit());
					splitData.setAttrNum(i);
				}
			} 	
		}
		
		log.debug("Best Split: " + splitData.getAttrNum() + ", " + splitData.getSplit() + ", " + splitData.getEnt());
		
		return splitData;
		
	}


}


