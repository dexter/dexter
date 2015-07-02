/**
 *  Copyright 2015 Salvatore Trani
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
package it.cnr.isti.hpc.dexter.cli.label;

import it.cnr.isti.hpc.cli.AbstractCommandLineInterface;
import it.cnr.isti.hpc.dexter.label.IdHelper;
import it.cnr.isti.hpc.dexter.label.IdHelperFactory;
import it.cnr.isti.hpc.dexter.util.TitleRedirectId;
import it.cnr.isti.hpc.io.reader.JsonRecordParser;
import it.cnr.isti.hpc.io.reader.RecordParser;
import it.cnr.isti.hpc.io.reader.RecordReader;
import it.cnr.isti.hpc.log.ProgressLogger;
import it.cnr.isti.hpc.wikipedia.article.Article;
import it.cnr.isti.hpc.wikipedia.reader.filter.TypeFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ExportArticlesIdIndexCLI takes the IdHelper and creates a file
 * which contains the mapping between each article title (the label) to an int
 * (used to represent the entity in the framework).
 * 
 * @author Salvatore Trani, salvatore.trani@isti.cnr.it created on 02/lug/2015
 */
public class ExportArticlesIdIndexCLI extends AbstractCommandLineInterface {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(ExportArticlesIdIndexCLI.class);

	private static final String USAGE = "java -cp $jar "
			+ ExportArticlesIdIndexCLI.class
			+ " -output titles.tsv";
	private static String[] params = new String[] { OUTPUT };

	public static void main(String[] args) {
		ExportArticlesIdIndexCLI cli = new ExportArticlesIdIndexCLI(args);
		cli.openOutput();

		ProgressLogger pl = new ProgressLogger("Processed {} articles", 100000);

		IdHelper helper = IdHelperFactory.getStdIdHelper();

		for (String wikiTitle : helper.getLabels()) {
			pl.up();
			cli.writeLineInOutput(wikiTitle + "\t" + helper.getId(wikiTitle));
		}
		cli.closeOutput();
		return;
	}

	public ExportArticlesIdIndexCLI(String[] args) {
		super(args, params, USAGE);
	}

}
