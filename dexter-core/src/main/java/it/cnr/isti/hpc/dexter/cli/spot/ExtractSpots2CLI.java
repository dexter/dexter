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
import it.cnr.isti.hpc.dexter.analysis.SpotCleaner;
import it.cnr.isti.hpc.dexter.label.IdHelper;
import it.cnr.isti.hpc.dexter.label.IdHelperFactory;
import it.cnr.isti.hpc.dexter.spot.clean.SpotManager;
import it.cnr.isti.hpc.io.reader.JsonRecordParser;
import it.cnr.isti.hpc.io.reader.RecordReader;
import it.cnr.isti.hpc.log.ProgressLogger;
import it.cnr.isti.hpc.wikipedia.article.Article;
import it.cnr.isti.hpc.wikipedia.article.Link;
import it.cnr.isti.hpc.wikipedia.reader.filter.TypeFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retrieves all the titles and anchors from the Wikipedia articles, considers
 * only articles, redirects, templates and categories. The output file contains
 * the fields: <br>
 * <br>
 * {@code spot <tab> source id (id article containing the spot) <tab> target id (id of the target) article }
 * <br>
 * <br>
 * In case of a redirect or a title the source id is equal to the target id.
 * Each spot is processed using the {@link SpotManager#getStandardSpotManager()
 * standard spot manager}, which cleans, enriches and filters the text.
 * 
 */
public class ExtractSpots2CLI extends AbstractCommandLineInterface {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(ExtractSpots2CLI.class);

	private static String[] params = new String[] { INPUT, OUTPUT };

	private static final String USAGE = "java -cp $jar "
			+ ExtractSpots2CLI.class
			+ " -input wikipedia-json-dump -output spot-file";

	public static void main(String[] args) throws IOException,
			InterruptedException {
		ExtractSpots2CLI cli = new ExtractSpots2CLI(args);
		cli.openOutput();
		// Thread.sleep(20000);
		SpotCleaner spotManager = new SpotCleaner();
		IdHelper hp = IdHelperFactory.getStdIdHelper();
		RecordReader<Article> reader = new RecordReader<Article>(
				cli.getInput(), new JsonRecordParser<Article>(Article.class))
				.filter(TypeFilter.STD_FILTER);

		ProgressLogger progress = new ProgressLogger(
				"extracted spots for {} articles", 1000);

		Set<String> spots = new HashSet<String>();

		for (Article a : reader) {
			spots.clear();
			progress.up();
			int target = 0;
			int source = a.getWikiId();
			if (a.isRedirect()) {
				target = hp.getId(a.getRedirectNoAnchor());
				spotManager.getAllSpots(a, spots);
				for (String spot : spots) {
					if (target == 0) {
						logger.debug("cannot find id for redirect label {}",
								a.getRedirectNoAnchor());
						continue;
					}
					if (target > 0) {
						// if target > 0, then target is not a disambiguation
						// (disambiguations has id < 0)
						cli.writeLineInOutput(spot + "\t" + target + "\t"
								+ target);
					}
				}
				spots.clear();
			} else {

				if (!a.isDisambiguation()) {
					spotManager.enrich(a.getTitle(), spots);
					for (String spot : spots) {
						cli.writeLineInOutput(spot + "\t" + source + "\t"
								+ source);
					}
					spots.clear();
				}

				for (Link l : a.getLinks()) {
					spots.clear();
					spotManager.enrich(l.getDescription(), spots);
					for (String spot : spots) {
						target = hp.getId(l.getCleanId());
						if (target == 0) {
							logger.debug("cannot find id for label {}",
									l.getCleanId());

							continue;
						}

						if (hp.isDisambiguation(target)) {
							logger.debug(
									"{} {} is a disambiguation (ignoring)",
									target, hp.getLabel(target));
							logger.debug("(source = {})", hp.getLabel(source));

							continue;
						}
						if (a.isDisambiguation()) {
							// if a is a disambiguation, the label of a is good
							// for all
							// the pointed articles
							Set<String> spots2 = new HashSet<String>();
							spotManager.enrich(l.getDescription(), spots2);
							for (String label : spots2) {
								cli.writeLineInOutput(label + "\t" + source
										+ "\t" + target);

							}
						}

						cli.writeLineInOutput(spot + "\t" + source + "\t"
								+ target);

					}
				}
			}
		}
		cli.closeOutput();
	}

	public ExtractSpots2CLI(String[] args) {
		super(args, params, USAGE);
	}
}
