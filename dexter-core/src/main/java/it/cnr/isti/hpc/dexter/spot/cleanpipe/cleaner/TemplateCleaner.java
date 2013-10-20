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
 * 
 * TemplateCleaner removes all the text matching TEMPLATE[....]
 * 
 * @Deprecated language dependent
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 20/lug/2012
 */
public class TemplateCleaner extends Cleaner<String> {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(TemplateCleaner.class);

	static StringBuilder sb = new StringBuilder();

	public String clean(String spot) {
		sb.setLength(0);
		int lastIndex = 0;
		int len = spot.length();
		logger.debug("{}", "----------------------------");
		logger.debug("{}", spot);
		logger.debug("{}", "----------------------------");
		for (int i = -1; (i = spot.indexOf("TEMPLATE[", i + 1)) != -1;) {

			sb.append(spot.substring(lastIndex, i));
			logger.debug("* {}", spot.substring(lastIndex, i));

			sb.append(" ");
			int count = 1;
			i += "TEMPLATE[".length();

			while (i < len && count > 0) {

				if (spot.charAt(i) == '[')
					count++;
				if (spot.charAt(i) == ']')
					count--;
				i++;
			}

			lastIndex = i;
			i--;

		}
		sb.append(spot.substring(lastIndex));
		logger.debug("* {}," + spot.substring(lastIndex));
		return sb.toString();
	}
}
