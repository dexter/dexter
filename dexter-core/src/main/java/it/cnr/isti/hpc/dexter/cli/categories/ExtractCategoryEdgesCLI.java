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
import it.cnr.isti.hpc.log.ProgressLogger;
import it.cnr.isti.hpc.wikipedia.article.Article;
import it.cnr.isti.hpc.wikipedia.article.Article.Type;
import it.cnr.isti.hpc.wikipedia.article.Link;
import it.cnr.isti.hpc.wikipedia.reader.filter.TypeFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tudarmstadt.ukp.wikipedia.api.Category;

/**
 * Filter only the categories from the dump.
 * 
 */
public class ExtractCategoryEdgesCLI extends AbstractCommandLineInterface {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(ExtractCategoryEdgesCLI.class);

	private static String[] params = new String[] { INPUT, OUTPUT };

	private static final String USAGE = "java -cp $jar "
			+ ExtractCategoryEdgesCLI.class
			+ " -input wikipedia.json.[gz] -output category-graph.tsv[.gz]";

	public static void main(String[] args) {
		ExtractCategoryEdgesCLI cli = new ExtractCategoryEdgesCLI(args);
		ProgressLogger progress = new ProgressLogger("retrieved {} articles",
				1000);
		IdHelper helper = IdHelperFactory.getStdIdHelper();
		RecordReader<Article> reader = new RecordReader<Article>(
				cli.getInput(), new JsonRecordParser<Article>(Article.class));
		reader = reader.filter(new TypeFilter(Type.ARTICLE, Type.CATEGORY));
		cli.openOutput();
		
		
		for (Article a : reader) {
			progress.up();
			int id = a.getWid();
			for (Link c : a.getCategories()){
				//logger.info("id {}", c.getCleanId());
				int target = helper.getId(c.getCleanId());
				//logger.info("target {}", target);
				if (target <= 0) continue;
				
				cli.writeLineInOutput(a.getType()+"\t"+id+"\t"+target);	
			}
			

		}
		cli.closeOutput();

	}

	public ExtractCategoryEdgesCLI(String[] args) {
		super(args, params, USAGE);
	}
}
