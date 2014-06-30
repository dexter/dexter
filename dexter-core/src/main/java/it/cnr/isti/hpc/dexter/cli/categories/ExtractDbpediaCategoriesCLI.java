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
package it.cnr.isti.hpc.dexter.cli.categories;

import it.cnr.isti.hpc.cli.AbstractCommandLineInterface;
import it.cnr.isti.hpc.dexter.StandardTagger;
import it.cnr.isti.hpc.dexter.common.Document;
import it.cnr.isti.hpc.dexter.common.FlatDocument;
import it.cnr.isti.hpc.dexter.entity.EntityMatchList;
import it.cnr.isti.hpc.dexter.label.IdHelper;
import it.cnr.isti.hpc.dexter.label.IdHelperFactory;
import it.cnr.isti.hpc.io.IOUtils;
import it.cnr.isti.hpc.io.reader.JsonRecordParser;
import it.cnr.isti.hpc.io.reader.RecordReader;
import it.cnr.isti.hpc.io.reader.TsvRecordParser;
import it.cnr.isti.hpc.io.reader.TsvTuple;
import it.cnr.isti.hpc.log.ProgressLogger;
import it.cnr.isti.hpc.wikipedia.article.Article;
import it.cnr.isti.hpc.wikipedia.article.Article.Type;
import it.cnr.isti.hpc.wikipedia.reader.filter.TypeFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts the category wikinames in category ids
 * 
 */
public class ExtractDbpediaCategoriesCLI extends AbstractCommandLineInterface {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(ExtractDbpediaCategoriesCLI.class);

	private static String[] params = new String[] { INPUT, OUTPUT };

	private static final String USAGE = "java -cp $jar "
			+ ExtractDbpediaCategoriesCLI.class
			+ " -input category->category-id -output category-category-id";

	public static void main(String[] args) {
		ExtractDbpediaCategoriesCLI cli = new ExtractDbpediaCategoriesCLI(args);
		IdHelper helper = IdHelperFactory.getStdIdHelper();

		ProgressLogger progress = new ProgressLogger("converted {} categories",
				1000);
		RecordReader<TsvTuple> reader = new RecordReader<TsvTuple>(
				cli.getInput(), new TsvRecordParser("source", "target"));

		cli.openOutput();

		for (TsvTuple t : reader) {
			progress.up();
			int source = helper.getId(t.get("source"));
			if (source == IdHelper.NOID)
				continue;
			int target = helper.getId(t.get("target"));
			if (target == IdHelper.NOID)
				continue;
			cli.writeLineInOutput(source + "\t" + target);

		}
		cli.closeOutput();

	}

	public ExtractDbpediaCategoriesCLI(String[] args) {
		super(args, params, USAGE);
	}
}
