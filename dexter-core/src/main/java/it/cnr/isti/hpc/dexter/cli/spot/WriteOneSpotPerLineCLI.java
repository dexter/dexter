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

import it.cnr.isti.hpc.cli.AbstractCommandLineInterface;
import it.cnr.isti.hpc.dexter.spot.Spot;
import it.cnr.isti.hpc.dexter.spot.SpotReader;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.filter.CommonnessFilter;
import it.cnr.isti.hpc.log.ProgressLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * get in input the join between a file containing :
 * <spot> <tab> <src entity> <tab> <target entity>  
 * and a file 
 * <spot> <tab> <df(spot)> 
 * where df(spot) is the number of wikipedia documents containing the text 'spot' 
 * (as anchor or simple text) 
 * 
 * and produce in output a file containing: 
 * <spot> <tab> <outcoming entities> <tab> <link(spot)> <tab> <df(spot)> 
 * 
 * where link(spot) is the number of documents that contains the spot as an anchor.
 * 
 */
public class WriteOneSpotPerLineCLI extends AbstractCommandLineInterface {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(WriteOneSpotPerLineCLI.class);

	private static String[] params = new String[] { INPUT, "freq", OUTPUT };

	private static final String USAGE = "java -cp $jar " + WriteOneSpotPerLineCLI.class
			+ " -input spot-src-target -freq spot-docfreq ";

	public static void main(String[] args) {
		WriteOneSpotPerLineCLI cli = new WriteOneSpotPerLineCLI(args);
		String input = cli.getInput();
		cli.openOutput();
		logger.info("writing spots in {}",cli.getOutput());
		ProgressLogger progress = new ProgressLogger( "processed {} distinct spots", 100000);
		SpotReader reader = new SpotReader(input, cli.getParam("freq"));
		reader.addFilter(new CommonnessFilter());
		StringBuilder sb = new StringBuilder();
		// writer.add("1 us dollar", "1\t2");
		
		while (reader.hasNext()) {
			sb.setLength(0);
			progress.up();
			Spot s = reader.next();
			sb.append(s.getText()).append("\t").append(s.toTsv()).append("\n");
			cli.writeInOutput(sb.toString());
		}
		cli.closeOutput();
		logger.info("done");

	}

	public WriteOneSpotPerLineCLI(String[] args) {
		super(args, params, USAGE);
	}
}
