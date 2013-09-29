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
package it.cnr.isti.hpc.dexter.cli;

import it.cnr.isti.hpc.benchmark.Stopwatch;
import it.cnr.isti.hpc.cli.AbstractCommandLineInterface;
import it.cnr.isti.hpc.dexter.label.IdHelper;
import it.cnr.isti.hpc.dexter.label.IdHelperFactory;
import it.cnr.isti.hpc.wikipedia.article.Article;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Returns an article integer identifier given the its label.
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 21/nov/2011
 */
public class GetArticleIdCLI extends AbstractCommandLineInterface {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(GetArticleIdCLI.class);


	private static String[] params = new String[] { "title" };

	private static final String USAGE = "java -cp $jar "
			+ GetArticleIdCLI.class + " -title title";

	public static void main(String[] args) {
		
		Stopwatch sw = new Stopwatch();
		GetArticleIdCLI cli = new GetArticleIdCLI(args);
		String title = cli.getParam("title");
		title = Article.getTitleInWikistyle(title);
		IdHelper hash = IdHelperFactory.getStdIdHelper();
		
		sw.start("time");
		int val = hash.getId(title);
		sw.stop("time");
		System.out.println("title \t"+title);
		System.out.println("id    \t"+val);
		System.out.println("time  \t"+sw.stat("time"));
		

	}
	
	

	public GetArticleIdCLI(String[] args) {
		super(args, params, USAGE);
	}
 }
