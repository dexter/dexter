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

import java.util.List;

import it.cnr.isti.hpc.cli.AbstractCommandLineInterface;
import it.cnr.isti.hpc.dexter.lucene.LuceneHelper;
import it.cnr.isti.hpc.property.ProjectProperties;
import it.cnr.isti.hpc.wikipedia.article.Article;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * QueryLuceneCLI performs a query over the Wikipedia Lucene index and returns
 * the number of documents matching the query
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 02/lug/2012
 */
public class QueryLuceneCLI extends AbstractCommandLineInterface {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(QueryLuceneCLI.class);

	private static final String USAGE = "java -cp $jar " + QueryLuceneCLI.class
			+ " -query query";
	private static String[] params = new String[] { "query" };

	public static void main(String[] args) {
		QueryLuceneCLI cli = new QueryLuceneCLI(args);
		
		LuceneHelper helper = LuceneHelper.getDexterLuceneHelper();
		String query = cli.getParam("query");
		logger.info("query  \t {}", query);
		int res = helper.getFreq(query);
		logger.info("results\t {}", res);
		
		logger.info("top entities:\n");
		List<Integer> results = helper.query(query);
		if (results.size() > 10){
			results = results.subList(0, 10);
		}
		for (Integer id : results){
			Article a = helper.getArticle(id);
			System.out.println(a.getSnippet());
			
		}
		
	}

	public QueryLuceneCLI(String[] args) {
		super(args, params, USAGE);
	}

}
