/**
 *  Copyright 2011 Diego Ceccarelli
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

package it.cnr.isti.hpc.dexter.cli.spot.ram;

import it.cnr.isti.hpc.cli.AbstractCommandLineInterface;
import it.cnr.isti.hpc.dexter.spot.ram.EntityToSpotListMap;
import it.cnr.isti.hpc.dexter.util.DexterParams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encodes the offsets file with elias fano
 * 
 */
public class IndexEntityToSpotsCLI extends AbstractCommandLineInterface {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(IndexEntityToSpotsCLI.class);

	private static String[] params = new String[] { INPUT };

	private static final String USAGE = "java -cp $jar "
			+ IndexEntityToSpotsCLI.class
			+ " -input spot<tab>entity -output binarymap";

	private static DexterParams dexterParams = DexterParams.getInstance();

	public static void main(String[] args) {
		IndexEntityToSpotsCLI cli = new IndexEntityToSpotsCLI(args);
		EntityToSpotListMap map = new EntityToSpotListMap();
		map.loadFromFile(cli.getInput());
		map.dump(dexterParams.getEntityToSpots().getAbsolutePath());
	}

	public IndexEntityToSpotsCLI(String[] args) {
		super(args, params, USAGE);
	}
}
