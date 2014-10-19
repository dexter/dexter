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
import it.cnr.isti.hpc.dexter.lucene.LuceneHelper;
import it.cnr.isti.hpc.log.ProgressLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Takes a file containing a list of spots (ordered lexicographically) and
 * generates a new file containing for each spots it document frequency (the
 * number of articles in Wikipedia containing the spot as raw text or anchor).
 * The output file format is: <br>
 * <br>
 * {@code
 * spot <tab> spot-document-frequency
 * }
 * 
 * 
 * @see LuceneHelper
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 21/nov/2011
 */
@Deprecated
public class GenerateSpotDocumentFrequencyCLI extends
		AbstractCommandLineInterface {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(GenerateSpotDocumentFrequencyCLI.class);

	private static String[] params = new String[] { INPUT, OUTPUT };

	private static final String USAGE = "java -cp $jar "
			+ GenerateSpotDocumentFrequencyCLI.class
			+ " -input tsv-file-with-spots-in-pos0 -output df-file-spot-<tab>-df";

	public static void main(String[] args) {
		GenerateSpotDocumentFrequencyCLI cli = new GenerateSpotDocumentFrequencyCLI(
				args);
		ProgressLogger progress = new ProgressLogger(
				"written {} distinct spots", 100000);
		cli.openInputAndOutput();
		String line;
		LuceneHelper lucene = LuceneHelper.getDexterLuceneHelper();
		String currentSpot = "";
		int df = 0;
		while ((line = cli.readLineFromInput()) != null) {

			String[] elems = line.split("\t");
			// if (currentSpot.equals(elems[0])){
			// cli.writeInOutput(line);
			// cli.writeInOutput("\t");
			// cli.writeLineInOutput(String.valueOf(df));
			// }else{
			// currentSpot = elems[0];
			// df = lucene.getFreq(elems[0]);
			// cli.writeInOutput(line);
			// cli.writeInOutput("\t");
			if (!elems[0].equals(currentSpot)) {
				progress.up();
				currentSpot = elems[0];
				df = lucene.getFreqFromSummary(currentSpot);
				cli.writeInOutput(currentSpot);
				cli.writeInOutput("\t");
				cli.writeLineInOutput(String.valueOf(df));
			}
			// }
		}
		cli.closeOutput();
	}

	public GenerateSpotDocumentFrequencyCLI(String[] args) {
		super(args, params, USAGE);
	}
}
