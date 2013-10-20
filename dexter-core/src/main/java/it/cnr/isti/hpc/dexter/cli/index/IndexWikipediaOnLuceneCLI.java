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
package it.cnr.isti.hpc.dexter.cli.index;

import it.cnr.isti.hpc.cli.AbstractCommandLineInterface;
import it.cnr.isti.hpc.dexter.lucene.LuceneHelper;
import it.cnr.isti.hpc.io.reader.JsonRecordParser;
import it.cnr.isti.hpc.io.reader.RecordReader;
import it.cnr.isti.hpc.log.ProgressLogger;
import it.cnr.isti.hpc.wikipedia.article.Article;
import it.cnr.isti.hpc.wikipedia.reader.filter.TypeFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IndexWikipediaOnLuceneCLI takes the Wikipedia Dump (in json) and indexes it
 * with Lucene.
 * 
 * @see <a href="https://github.com/diegoceccarelli/json-wikipedia"> Json-Wikipedia </a>
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 02/lug/2012
 */
public class IndexWikipediaOnLuceneCLI extends AbstractCommandLineInterface {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(IndexWikipediaOnLuceneCLI.class);

	private static final String USAGE = "java -cp $jar "
			+ IndexWikipediaOnLuceneCLI.class
			+ " -input wikipediadump.json[.gz] ";
	private static String[] params = new String[] { INPUT };

	final static int COMMIT_FREQUENCY = 10000;

	public static void main(String[] args) {
		IndexWikipediaOnLuceneCLI cli = new IndexWikipediaOnLuceneCLI(args);
		
		LuceneHelper indexer = LuceneHelper.getDexterLuceneHelper();
		indexer.clearIndex();
		RecordReader<Article> reader = new RecordReader<Article>(
				cli.getInput(), new JsonRecordParser<Article>(Article.class));
		reader = reader.filter(TypeFilter.MAIN);

		ProgressLogger progress = new ProgressLogger("indexed {} articles",100000);

		for (Article a : reader) {
			if (progress.up() % COMMIT_FREQUENCY == 0) {
				logger.info("commit");
				indexer.commit();
			}
			indexer.addDocument(a);
		}

		logger.info("commit");
		indexer.commit();
	}

	public IndexWikipediaOnLuceneCLI(String[] args) {
		super(args, params, USAGE);
	}

}
