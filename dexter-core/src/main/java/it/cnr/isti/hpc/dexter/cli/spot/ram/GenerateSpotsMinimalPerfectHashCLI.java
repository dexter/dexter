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
import it.cnr.isti.hpc.dexter.spot.ram.SpotMinimalPerfectHash;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Get in input a file containing the spots (one per line) generate the minimal 
 * perfect hash for each spot and store in <code>${ram.spot.perfect.hash}</code>. 
 * Puts in the output file the hash values in the same order of the spot file. 
 * 
 */
public class GenerateSpotsMinimalPerfectHashCLI extends AbstractCommandLineInterface {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(GenerateSpotsMinimalPerfectHashCLI.class);

	private static String[] params = new String[] { OUTPUT };

	private static final String USAGE = "java -cp $jar "
			+ GenerateSpotsMinimalPerfectHashCLI.class;
	
	public static void main(String[] args) {
		GenerateSpotsMinimalPerfectHashCLI cli = new GenerateSpotsMinimalPerfectHashCLI(args);
		SpotMinimalPerfectHash.dump();
		SpotMinimalPerfectHash.getInstance().dumpKeys(cli.getOutput());
		
		
		

	}

	public GenerateSpotsMinimalPerfectHashCLI(String[] args) {
		super(args, params, USAGE);
	}
}
