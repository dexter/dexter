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
import it.cnr.isti.hpc.dexter.spot.Spot;
import it.cnr.isti.hpc.dexter.spot.SpotReader;
import it.cnr.isti.hpc.dexter.spot.ram.RamSpotRepository;
import it.cnr.isti.hpc.dexter.spot.ram.SpotEliasFanoOffsets;
import it.cnr.isti.hpc.dexter.spot.ram.SpotMinimalPerfectHash;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encodes the offsets file with elias fano 
 * 
 */
public class IndexOffsetsUsingEliasFanoCLI extends AbstractCommandLineInterface {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(IndexOffsetsUsingEliasFanoCLI.class);

	private static String[] params = new String[] { };

	private static final String USAGE = "java -cp $jar "
			+ IndexOffsetsUsingEliasFanoCLI.class;

	public static void main(String[] args) {
		IndexOffsetsUsingEliasFanoCLI cli = new IndexOffsetsUsingEliasFanoCLI(args);
		SpotEliasFanoOffsets.dumpEliasFanoFile();
		
		

	}

	public IndexOffsetsUsingEliasFanoCLI(String[] args) {
		super(args, params, USAGE);
	}
}
