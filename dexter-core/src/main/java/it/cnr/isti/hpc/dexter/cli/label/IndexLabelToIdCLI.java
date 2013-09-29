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
package it.cnr.isti.hpc.dexter.cli.label;

import it.cnr.isti.hpc.cli.AbstractCommandLineInterface;
import it.cnr.isti.hpc.dexter.label.IdHelperFactory;
import it.cnr.isti.hpc.dexter.label.LabelToIdWriter;
import it.cnr.isti.hpc.dexter.util.TitleRedirectId;
import it.cnr.isti.hpc.io.reader.RecordReader;
import it.cnr.isti.hpc.log.ProgressLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Takes a file containing a list of TitleRedirectId and indexes the mapping
 * <code> title -> id </code>.
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 02/lug/2012
 */
public class IndexLabelToIdCLI extends AbstractCommandLineInterface {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(IndexLabelToIdCLI.class);

	private static final String USAGE = "java -cp $jar "
			+ IndexLabelToIdCLI.class + " -input titles.tsv ";
	private static String[] params = new String[] { INPUT };

	static LabelToIdWriter writer = IdHelperFactory.getStdLabelToIdWriter();

	public static void main(String[] args) {
		ProgressLogger pl = new ProgressLogger("{} records inserted", 100000);
		IndexLabelToIdCLI cli = new IndexLabelToIdCLI(args);
		RecordReader<TitleRedirectId> reader = new RecordReader<TitleRedirectId>(
				cli.getInput(), new TitleRedirectId.Parser());
		String currentTitle = "";
		Integer currentId = -1;
		for (TitleRedirectId article: reader){
			pl.up();
			

			if (!article.isRedirect()) {
				// real article
				currentTitle = article.getTitle();
				currentId = Integer.parseInt(article.getId());
				store(currentTitle, currentId);
			}
		}
		writer.close();
	}

	public static void store(String k, Integer v) {
		logger.debug("adding {} {} ", k, v);
		writer.add(k, v);
	}

	public IndexLabelToIdCLI(String[] args) {
		super(args, params, USAGE);
	}
}
