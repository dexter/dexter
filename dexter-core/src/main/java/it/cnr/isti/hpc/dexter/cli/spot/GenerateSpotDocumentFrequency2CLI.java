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
import it.cnr.isti.hpc.dexter.lucene.LuceneHelper;
import it.cnr.isti.hpc.dexter.spot.DocumentFrequencyGenerator;
import it.cnr.isti.hpc.dexter.spot.SpotReader.SpotParser;
import it.cnr.isti.hpc.io.reader.RecordReader;
import it.cnr.isti.hpc.log.ProgressLogger;
import it.cnr.isti.hpc.wikipedia.article.Article;
import it.cnr.isti.hpc.wikipedia.reader.filter.TypeFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

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
public class GenerateSpotDocumentFrequency2CLI extends
		AbstractCommandLineInterface {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(GenerateSpotDocumentFrequency2CLI.class);

	private static String[] params = new String[] { INPUT, "dump", OUTPUT };

	private static final String USAGE = "java -cp $jar "
			+ GenerateSpotDocumentFrequency2CLI.class
			+ " -input spot-src-target -dump wikipedia-jsondump -output spot-freq";

	public static void main(String[] args) {
		GenerateSpotDocumentFrequency2CLI cli = new GenerateSpotDocumentFrequency2CLI(
				args);
		ProgressLogger progress = new ProgressLogger(
				"processed {} distinct articles", 1000);
		cli.openOutput();
		RecordReader<String> reader = new RecordReader<String>(cli.getInput(),
				new SpotParser());
		DocumentFrequencyGenerator generator = new DocumentFrequencyGenerator(
				reader.iterator());
		RecordReader<Article> wikipedia = new RecordReader<Article>(
				cli.getParam("dump"), Article.class)
				.filter(TypeFilter.STD_FILTER);
		Stopwatch watch = new Stopwatch();
		for (Article article : wikipedia) {
			progress.up();
			watch.start("generate");
			Multiset<String> spots = generator.getSpotsAndFrequencies(article);
			watch.stop("generate");
			watch.start("write");
			for (Entry<String> spot : spots.entrySet()) {
				cli.writeInOutput(spot.getElement());
				cli.writeInOutput("\t");
				cli.writeLineInOutput(String.valueOf(spot.getCount()));
			}
			watch.stop("write");
			// if (progress.getStatus() % 100 == 0) {
			// System.out.println(watch.stat());
			//
			// }

		}
		cli.closeOutput();
	}

	public GenerateSpotDocumentFrequency2CLI(String[] args) {
		super(args, params, USAGE);
	}
}
