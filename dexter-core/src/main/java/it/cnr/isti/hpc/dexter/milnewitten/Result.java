/**
 *  Copyright 2013 Diego Ceccarelli
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package it.cnr.isti.hpc.dexter.milnewitten;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 * Created on May 2, 2013
 */
/*
 *    Result.java
 *    Copyright (C) 2007 David Milne, d.n.milne@gmail.com
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

import java.text.DecimalFormat;
import java.util.Collection;

/**
 * @author David Milne
 *
 *Generates statistics (recall, precision, f-measure) by comparing a collection of items to another that is considered ground-truth
 * @param <E> The type of element that ground truth and data collections will contain. 
 */
public class Result<E> {
	
	
	
	private int itemsInGroundTruth ;
	private int itemsFound ;
	private int itemsCorrect ;
	private DecimalFormat f = new DecimalFormat("#0.00%") ;
	
	/**
	 * Initializes an empty result, to which intermediate results will be appended.
	 */
	public Result() {
		this.itemsInGroundTruth = 0 ;
		this.itemsFound = 0 ;
		this.itemsCorrect = 0 ;
	}
	
	/**
	 * Initializes a new result, given a collection of data items and a collection of ground truth items.
	 * 
	 * @param data	the data to be compared to ground truth
	 * @param groundTruth the ground truth.
	 */
	public Result(Collection<E> data, Collection<E> groundTruth) {
		
		this.itemsInGroundTruth = groundTruth.size();
		this.itemsFound = data.size() ;
		this.itemsCorrect = 0 ;
		
		for (Object item:data) {
			if (groundTruth.contains(item))
				itemsCorrect++ ;
		}
	}
	

	
	/**
	 * @return the proportion of correct items over all ground truth items.
	 */
	public double getRecall() {
		if (itemsInGroundTruth == 0) return 0;
		return (double)itemsCorrect/itemsInGroundTruth ;			
	}
	
	/**
	 * @return the proportion of correct items over all items found.
	 */
	public double getPrecision() {
		if (itemsFound == 0) return 0;
		return (double)itemsCorrect/itemsFound ;			
	}
	
	/**
	 * @return the harmonic mean of recall and precision
	 */
	public double getFMeasure() {
		double den = getPrecision()+getRecall();
		if (den == 0) return 0;
		return 2*(getPrecision()*getRecall()) / den  ; 
	}
	
	/**
	 * @return a string representation of the result.
	 */
	public String toString() {
		return "recall: " + f.format(getRecall()) + ", precision:" + f.format(getPrecision()) + ", f-measure:" + f.format(getFMeasure()) ;
	}
	
}
