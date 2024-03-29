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
package com.decisiontree.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.decisiontree.data.RangeAttrClass;
import com.decisiontree.data.Tuple;
import com.decisiontree.param.GlobalParam;

/**
 * 
 * SplitSearchUD -finding the best split point for a set of data (interval-valued) using interval-valued pruning technique.
 *
 * @author Smith Tsang
 * @since 0.8
 *
 */
public class SplitSearchUD implements SplitSearch{
	
	
	public RangeAttrClass [] generateRangeAttrClass(List<Tuple>data, int attr){

		int noTuples = data.size();
		RangeAttrClass [] attrClassSet = new RangeAttrClass[noTuples];
		for(int j = 0; j < noTuples; j++)
			attrClassSet[j] = new RangeAttrClass(data.get(j).getAttribute(attr),data.get(j).getCls(), data.get(j).getWeight());
		return attrClassSet;
	}

	public Histogram [] SegGen(List<Tuple> data, int noCls, int attr){

		int noTuples = data.size();

		RangeAttrClass [] attrClassSet = generateRangeAttrClass(data, attr);
		Arrays.sort(attrClassSet);


		double [] endPtSet = new double[noTuples*2];
		for(int  i =0 ; i < noTuples; i++){
			endPtSet[2*i] = attrClassSet[i].getStart(); 
			endPtSet[2*i +1] = attrClassSet[i].getEnd();
		}

		Arrays.sort(endPtSet);

		Histogram tempSegmentSet[] = new Histogram[noTuples*2];
		int attrClassPos = 0;
		int maxNoSegments =0;

//		int emptyInt = 0;
//		int homoInt = 0;
//		int heterInt = 0;

		double totalFrac = 0;
		double start, end;
		for( int i =1; i < noTuples*2; i++){
			start = endPtSet[i-1];
			end = endPtSet[i];

			if(end - start < 1E-10) continue;
			for( ; attrClassPos < noTuples && 
					attrClassSet[attrClassPos].getEnd() < start; attrClassPos++){};
			if( attrClassPos >= noTuples) break;
			int temp = attrClassPos;
			tempSegmentSet[maxNoSegments] = new Histogram(noCls);

			for( ;temp < noTuples && attrClassSet[temp].getStart() < end ; temp++){
				double left =attrClassSet[temp].getStart(), right = attrClassSet[temp].getEnd();

				if( start >= right) continue;
				if( end <= left) continue;

				if( start >= left && end <= right){
					left = start;
					right = end;
				}

				double frac = 0;
				frac =  attrClassSet[temp].getFrac(left,right);
				totalFrac += frac;
				if(frac <= 0) continue;

				tempSegmentSet[maxNoSegments].addCls(attrClassSet[temp].getCls(),frac * attrClassSet[temp].getWeight());
			}
			tempSegmentSet[maxNoSegments].setValue(end);
			maxNoSegments++;			

		}
	
//		Histogram segments[] = new Histogram[maxNoSegments];
		ArrayList<Histogram> segmentList = new ArrayList<Histogram>(maxNoSegments);

//		int singClsMerge = 0;
//		int mulClsMerge = 0;

//		int noSegments = countSegments;
//		int countSegments =0;
//		segments[0] = new Histogram(noCls);
//		segmentList.add(segments[0]);
//		segments[0].mergeHist(tempSegmentSet[0]);

		Histogram currSegment = new Histogram(noCls);
		currSegment.mergeHist(tempSegmentSet[0]);
		segmentList.add(/*segments[0]*/currSegment);
//		if(tempSegmentSet[0].mulCls()) heterInt++;
//		else homoInt++;
		for( int i =1; i < maxNoSegments ; i++){
			if(tempSegmentSet[i].empty())
				continue;
			else if(tempSegmentSet[i].mulCls()){
				if(currSegment.mulCls() && currSegment.checkDist(tempSegmentSet[i])){
					currSegment.mergeHist(tempSegmentSet[i]);
//					
				}
				else{
					currSegment = new Histogram(noCls);
					segmentList.add(currSegment);
					currSegment.mergeHist(tempSegmentSet[i]);
				}
			}
			else{
				if(!currSegment.mulCls() && currSegment.singleCls() == tempSegmentSet[i].singleCls()){
					currSegment.mergeHist(tempSegmentSet[i]);
				}
				else{
					currSegment = new Histogram(noCls);
					currSegment.mergeHist(tempSegmentSet[i]);
					segmentList.add(currSegment);
				}
			}
		}
		
//		for( int i =1; i < maxNoSegments ; i++){
//			if(tempSegmentSet[i].empty()){ /*emptyInt++; */continue;}
//			else if(tempSegmentSet[i].mulCls()){
////				heterInt++;
//				if(/*segments[countSegments]*/currSegment.mulCls() && /*segments[countSegments]*/currSegment.checkDist(tempSegmentSet[i])){
//					/*segments[countSegments]*/currSegment.mergeHist(tempSegmentSet[i]);
////					mulClsMerge++;
//				}
//				else{
//					/*segments[++countSegments] */currSegment = new Histogram(noCls);
//					segmentList.add(/*segments[countSegments]*/currSegment);
//					/*segments[countSegments]*/currSegment.mergeHist(tempSegmentSet[i]);
//				}
//			}
//			else{
////				homoInt++;
//				if(!segments[countSegments].mulCls() && segments[countSegments].singleCls() == tempSegmentSet[i].singleCls()){
//					segments[countSegments].mergeHist(tempSegmentSet[i]);
////					singClsMerge++;
//				}
//				else{
//					segments[++countSegments] = new Histogram(noCls);
//					segmentList.add(segments[countSegments]);
//					segments[countSegments].mergeHist(tempSegmentSet[i]);
//				}
//			}
//		}

		Histogram [] segments = new Histogram[segmentList.size()];
//		Histogram tempSegment = null;
		Iterator<Histogram> iter = segmentList.iterator();
		for(int i = 0; iter.hasNext(); i++)
			segments[i] = iter.next();
		
//		segmentList.toArray(segments);
		return segments;	

	}


	public SplitData findBestAttr(List<Tuple> data, int noCls, int noAttr) {

		SplitData splitData = new SplitData();
		splitData.setEnt(Double.POSITIVE_INFINITY);
	
		double totalTuples = Tuple.countWeightedTuples(data);

		BinarySplit binarySplit = new BinarySplit(totalTuples, noCls);	
	
		for(int i = 0 ; i < noAttr; i++){
			Histogram segmentSet []  = SegGen(data, noCls, i);
			int noSegments = segmentSet.length;
			if(noSegments <= 1){
				continue;
			}
			GlobalParam.addNoEntOnSamples(segmentSet.length);
			binarySplit.run(segmentSet);
			double localEnt = binarySplit.getEnt();
			
			if(splitData.getEnt() - localEnt > 1E-12){
				splitData.setEnt(localEnt);
				splitData.setSplit(binarySplit.getSplit());
				splitData.setAttrNum(i);
			}
		}
		log.debug("Best Split: " + splitData.getAttrNum() + ", " + splitData.getSplit() + ", " + splitData.getEnt());
		
		return splitData;
	}

}
