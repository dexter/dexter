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
package it.cnr.isti.hpc.dexter.spot.cleanpipe.cleaner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Quotes cleaner removes quotes from a spot.
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 20/lug/2012
 */
public class QuotesCleaner extends Cleaner<String> {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(QuotesCleaner.class);

	public String clean(String spot) {
		String clean = spot.replaceAll("\"", " ");
		if (!clean.equals(spot))
			logger.debug("{} -> {}", spot, clean);
		return clean;
	}

}
