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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;


public class ResultList<E> extends ArrayList<Result<E>>{
	

	public ResultList() {
		super();
	}
	

	

	
	/**
	 * @return the proportion of correct items over all ground truth items.
	 */
	public double getRecall() {
		if (isEmpty()) return 0;
		double recall = 0;
		for (Result<E> result : this){
			recall+= result.getRecall();
		}
		return recall/size();
	}
	
	/**
	 * @return the proportion of correct items over all items found.
	 */
	public double getPrecision() {
		if (isEmpty()) return 0;
		double precision = 0;
		for (Result<E> result : this){
			precision += result.getPrecision();
		}
		return precision/size();			
	}
	
	/**
	 * @return the harmonic mean of recall and precision
	 */
	public double getFMeasure() {
		if (isEmpty()) return 0;
		double recall = 0;
		for (Result<E> result : this){
			recall += result.getFMeasure();
		}
		return recall/size();
	}
	
	/**
	 * @return a string representation of the result.
	 */
	public String toString() {
		return "recall: " + getRecall() + ", precision:" + getPrecision() + ", f-measure:" + getFMeasure() ;
	}
	
}
