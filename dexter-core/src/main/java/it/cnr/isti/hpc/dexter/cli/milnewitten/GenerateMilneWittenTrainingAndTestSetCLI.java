/**
 *  Copyright 2013 Diego Ceccarelli
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
package it.cnr.isti.hpc.dexter.cli.milnewitten;

import it.cnr.isti.hpc.cli.AbstractCommandLineInterface;
import it.cnr.isti.hpc.dexter.graph.IncomingNodes;
import it.cnr.isti.hpc.dexter.graph.NodeFactory;
import it.cnr.isti.hpc.dexter.util.TitleRedirectId;
import it.cnr.isti.hpc.io.IOUtils;
import it.cnr.isti.hpc.io.reader.Filter;
import it.cnr.isti.hpc.io.reader.JsonRecordParser;
import it.cnr.isti.hpc.io.reader.RecordReader;
import it.cnr.isti.hpc.log.ProgressLogger;
import it.cnr.isti.hpc.wikipedia.article.Article;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GenerateMilneWittenTrainingAndTestSetCLI extends AbstractCommandLineInterface {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(GenerateMilneWittenTrainingAndTestSetCLI.class);

	private static final String USAGE = "java -cp $jar "
			+ GenerateMilneWittenTrainingAndTestSetCLI.class
			+ " -input wikipedia-dump.json -training training.json -test test.json ";
	private static String[] params = new String[] { INPUT, "training", "test" ,"size" };

	public static void main(String[] args) {
		GenerateMilneWittenTrainingAndTestSetCLI cli = new GenerateMilneWittenTrainingAndTestSetCLI(args);
		ProgressLogger pl = new ProgressLogger("{} articles added", 50);
		int size =  Integer.parseInt(cli.getParam("size"));
		JsonRecordParser<Article> parser = new JsonRecordParser<Article>(Article.class);
		RecordReader<Article> reader = new RecordReader<Article>(cli.getInput(), parser).filter(new FrequencyFilter());
		BufferedWriter training = IOUtils.getPlainOrCompressedUTF8Writer(cli.getParam("training"));
		BufferedWriter test = IOUtils.getPlainOrCompressedUTF8Writer(cli.getParam("test"));
		
		Iterator<Article> iterator = reader.iterator();
		for (int i = 0; i < size ; i++){
			pl.up();
			try {
				Article a = iterator.next();
				training.write(parser.encode(a));
				training.newLine();
				//logger.info("article {} written in training ",a.getTitle());
			} catch (IOException e) {
				logger.error("writing the training");
			}	
		}
		try {
			training.close();
		} catch (IOException e) {
			logger.error("writing the training");
		}
		pl = new ProgressLogger("{} articles added", 50);
		for (int i = 0; i < size ; i++){
			pl.up();
			try {
				Article a = iterator.next();
				test.write(parser.encode(a));
				test.newLine();
				//logger.info("article {} written in test ",a.getTitle());
			} catch (IOException e) {
				logger.error("writing the test");
			}	
		}
		try {
			test.close();
		} catch (IOException e) {
			logger.error("writing the training");
		}
		
		
		
		
		
	}
	


	public GenerateMilneWittenTrainingAndTestSetCLI(String[] args) {
		super(args, params, USAGE);
	}
	
	public static class FrequencyFilter implements Filter<Article>{
		IncomingNodes nodes = null;
		
		
		public FrequencyFilter(){
			nodes = NodeFactory.getIncomingNodes(NodeFactory.STD_TYPE);
			
		}

		@Override
		public boolean isFilter(Article item) {
			return nodes.getNeighbours(item.getWikiId()).length < 500;
		}
		
	}

}
