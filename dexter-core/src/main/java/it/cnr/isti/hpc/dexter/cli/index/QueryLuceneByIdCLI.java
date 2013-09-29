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
import it.cnr.isti.hpc.dexter.label.IdHelper;
import it.cnr.isti.hpc.dexter.label.IdHelperFactory;
import it.cnr.isti.hpc.dexter.lucene.LuceneHelper;
import it.cnr.isti.hpc.wikipedia.article.Article;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * QueryLuceneByIdCLI, retrieves from the index the Wikipedia Article with the
 * given Wikipedia Id.
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 02/lug/2012
 */
public class QueryLuceneByIdCLI extends AbstractCommandLineInterface {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(QueryLuceneByIdCLI.class);

	private static final String USAGE = "java -cp $jar "
			+ QueryLuceneByIdCLI.class + " -id docid";
	private static String[] params = new String[] { "id" };

	public static void main(String[] args) {
		QueryLuceneByIdCLI cli = new QueryLuceneByIdCLI(args);
		LuceneHelper lucene = LuceneHelper.getDexterLuceneHelper();
		int id = Integer.parseInt(cli.getParam("id"));
		IdHelper helper = IdHelperFactory.getStdIdHelper();
		String label = helper.getLabel(id);
		logger.info("document {} ({})  ", label, id);
		Article a = lucene.getArticle(id);
		System.out.println(a.getSnippet());
		// cli.closeOutput();

	}

	public QueryLuceneByIdCLI(String[] args) {
		super(args, params, USAGE);
	}

}
