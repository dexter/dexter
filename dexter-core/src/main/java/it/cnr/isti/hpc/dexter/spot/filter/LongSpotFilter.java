/**
 *  Copyright 2012 Diego Ceccarelli
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
package it.cnr.isti.hpc.dexter.spot.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SymbolFilter.java filters out all spot that do not contain alphabetic characters
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 20/lug/2012
 */
public class LongSpotFilter implements Filter {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(LongSpotFilter.class);
	private static int maxNumberOfTerms = 6;
	
	
	
	public boolean isFilter(String spot) {
		int count = 0;
		int len = spot.length();
		for (int i = 0 ; i < len; i++){
			if (spot.charAt(i) == ' ') count++;
			if (count >= maxNumberOfTerms) return true;
		}
		return false;
	}
	


	
	public boolean post() {
		return true;
	}

	
	public boolean pre() {
		return false;
	}

}
