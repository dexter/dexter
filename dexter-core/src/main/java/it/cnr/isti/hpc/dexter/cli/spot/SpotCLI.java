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
package it.cnr.isti.hpc.dexter.cli.spot;

import it.cnr.isti.hpc.benchmark.Stopwatch;
import it.cnr.isti.hpc.cli.AbstractCommandLineInterface;
import it.cnr.isti.hpc.dexter.Document;
import it.cnr.isti.hpc.dexter.FlatDocument;
import it.cnr.isti.hpc.dexter.spot.SpotMatchList;
import it.cnr.isti.hpc.dexter.spot.Spotter;
import it.cnr.isti.hpc.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Query the map file for a the spot given in input
 * 
 */
public class SpotCLI extends AbstractCommandLineInterface {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(SpotCLI.class);

	private static String[] params = new String[] { "input" };

	private static final String USAGE = "java -cp $jar " + SpotCLI.class
			+ " -input filetospot";

	public static void main(String[] args) {
		SpotCLI cli = new SpotCLI(args);
		String input = cli.getInput();
		Document doc = new FlatDocument(IOUtils.getFileAsString(input));
		Spotter spotter = new Spotter();
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("spot");
		SpotMatchList sml =  spotter.match(doc);
		stopwatch.stop("spot");
		
		logger.info("matched {} spot  ", sml.size());
		logger.info("{}", stopwatch.stat("spot"));
		System.out.println(sml);
		

	}

	public SpotCLI(String[] args) {
		super(args, params, USAGE);
	}
}
