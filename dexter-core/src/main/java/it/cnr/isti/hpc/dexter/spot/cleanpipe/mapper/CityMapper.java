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
package it.cnr.isti.hpc.dexter.spot.cleanpipe.mapper;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CityMapper manages labels of the type "city, country" (e.g., ada, wisconsin),
 * returning only the name of the city
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 20/lug/2012
 */

public class CityMapper extends Mapper<String> {
	// TODO change the name in NameMapper?
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(CityMapper.class);

	@Override
	public Set<String> map(String spot) {
		Set<String> mappings = new HashSet<String>();
		mappings.add(spot);
		if (spot.matches("^[^,]+,[^,]+$"))
			removeRegex(mappings, spot, " *,.+$");
		return mappings;

	}

	private void removeRegex(Set<String> mappings, String spot, String regex) {
		String str = spot.replaceAll(regex, "");
		if (!str.equals(spot)) {
			mappings.add(str);
			logger.debug("{} -> {} ", spot, str);
		}
	}
}
