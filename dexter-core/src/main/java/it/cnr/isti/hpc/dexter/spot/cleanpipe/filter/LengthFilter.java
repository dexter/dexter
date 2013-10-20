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
package it.cnr.isti.hpc.dexter.spot.cleanpipe.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LengthFilter filters out spots shorter than a given length (default is 3).
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 20/lug/2012
 */
public class LengthFilter extends Filter<String> {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(LengthFilter.class);

	private final static int DEFAULT_MIN_LENGTH = 3;
	private int minLength;

	public LengthFilter() {
		this(DEFAULT_MIN_LENGTH);
	}

	public LengthFilter(int minLength) {
		this.minLength = minLength;
	}

	public boolean isFilter(String spot) {
		return spot.length() < minLength;
	}

}
