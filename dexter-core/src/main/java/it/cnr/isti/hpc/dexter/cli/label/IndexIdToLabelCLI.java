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
import it.cnr.isti.hpc.dexter.hash.IdHelperFactory;
import it.cnr.isti.hpc.dexter.hash.IdToLabelWriter;
import it.cnr.isti.hpc.dexter.util.TitleRedirectId;
import it.cnr.isti.hpc.io.reader.RecordReader;
import it.cnr.isti.hpc.log.ProgressLogger;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Takes a file containing a list of TitleRedirectId and indexes the 
 * mapping <code> id -> title </code>.
 * 
 * @see LabelHelper, TitleRedirectId
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 02/lug/2012
 */
public class IndexIdToLabelCLI extends AbstractCommandLineInterface {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(IndexIdToLabelCLI.class);

	private static final String USAGE = "java -cp $jar "
			+ IndexIdToLabelCLI.class
			+ " -input titles.tsv ";
	private static String[] params = new String[] { INPUT };	
	
	static IdToLabelWriter writer = IdHelperFactory.getStdIdToLabelWriter();
	
	public static void main(String[] args) {
		
		ProgressLogger pl = new ProgressLogger("{} records inserted",100000);
		IndexIdToLabelCLI cli = new IndexIdToLabelCLI(args);
		RecordReader<TitleRedirectId> reader = new RecordReader<TitleRedirectId>(cli.getInput(),new TitleRedirectId.Parser());		
		String currentTitle = "";
		Integer currentId = -1;
		Iterator<TitleRedirectId> iterator = reader.iterator();
		TitleRedirectId article = null;
		while (iterator.hasNext()){
			pl.up();
			try{
			 article = iterator.next();
			}
			catch(Exception e){
				logger.error(e.getMessage());
				continue;
			}		
			if (!article.isRedirect()) {
				// real articles 
				currentTitle = article.getTitle();
				currentId = Integer.parseInt(article.getId());
				store(currentId, currentTitle);
			} 
		}
		
		writer.close();

	}

	public static void store(Integer v, String k) {
		logger.debug("adding {} {} ", v, k );
		writer.add(v, k);
	}

	public IndexIdToLabelCLI(String[] args) {
		super(args, params, USAGE);
	}
}
