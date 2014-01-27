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
package it.cnr.isti.hpc.dexter.spot.repo;

import it.cnr.isti.hpc.dexter.spot.ram.RamSpotRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SpotRepositoryFactory returns a spot repository instance given a string
 * defining its type.
 * 
 * @see SpotRepository
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Feb 26, 2013
 */
public class SpotRepositoryFactory {

	private static final Logger logger = LoggerFactory
			.getLogger(SpotRepositoryFactory.class);

	public SpotRepositoryFactory() {
	}

	/**
	 * Returns an instance of a SpotRepository
	 * 
	 * @param type
	 *            - a string identifying a spot repository
	 * @returns a SpotRepository
	 * @throws UnsupportedOperationException
	 *             if the given name does not match with any spot repository
	 *             nameF
	 */
	public SpotRepository getInstance(String type) {
		// if (type.equals("mapfile")){
		// logger.info("Using Mapfile Spot Repository");
		// return new SpotMapFile();

		// }
		// if (type.equals("mapdb")){
		// logger.info("Using Mapdb Spot Repository");
		// return SpotMapDB.getInstance();
		// }
		if (type.equals("ram")) {
			logger.info("Using Ram Spot Repository");
			return new RamSpotRepository();
		}

		throw new UnsupportedOperationException("No SpotRepository for type "
				+ type);

	}

	/**
	 * Returns the standard instance of the SpotRepository, defined in the
	 * Dexter property file (<code> spot.repository </code>)
	 */
	public SpotRepository getStdInstance() {
		// return getInstance(properties.get("spot.repository"));
		// FIXME remove spotRepositoryFactory?
		return getInstance("ram");
	}

}
