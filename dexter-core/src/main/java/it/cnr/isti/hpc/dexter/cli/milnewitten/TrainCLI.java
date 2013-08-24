///**
// *  Copyright 2013 Diego Ceccarelli
// *
// *  Licensed under the Apache License, Version 2.0 (the "License");
// *  you may not use this file except in compliance with the License.
// *  You may obtain a copy of the License at
// * 
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// *  Unless required by applicable law or agreed to in writing, software
// *  distributed under the License is distributed on an "AS IS" BASIS,
// *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *  See the License for the specific language governing permissions and
// *  limitations under the License.
// */
//package it.cnr.isti.hpc.dexter.cli.milnewitten;
//
//import it.cnr.isti.hpc.cli.AbstractCommandLineInterface;
//import it.cnr.isti.hpc.dexter.graph.IncomingNodes;
//import it.cnr.isti.hpc.dexter.graph.NodeFactory;
//import it.cnr.isti.hpc.dexter.milnewitten.Disambiguator;
//import it.cnr.isti.hpc.dexter.util.TitleRedirectId;
//import it.cnr.isti.hpc.io.IOUtils;
//import it.cnr.isti.hpc.io.reader.Filter;
//import it.cnr.isti.hpc.io.reader.JsonRecordParser;
//import it.cnr.isti.hpc.io.reader.RecordReader;
//import it.cnr.isti.hpc.log.ProgressLogger;
//import it.cnr.isti.hpc.property.ProjectProperties;
//import it.cnr.isti.hpc.wikipedia.article.Article;
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.IOException;
//import java.util.Iterator;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//
//public class TrainCLI extends AbstractCommandLineInterface {
//	/**
//	 * Logger for this class
//	 */
//	private static final Logger logger = LoggerFactory
//			.getLogger(TrainCLI.class);
//	
//	 FIXME uncomment
//
//	static ProjectProperties properties = new ProjectProperties(TrainCLI.class);
//
//	private static final String USAGE = "java -cp $jar "
//			+ TrainCLI.class
//			+ " -training training.json  ";
//	private static String[] params = new String[] {  "training" };
//
//	public static void main(String[] args) {
//		TrainCLI cli = new TrainCLI(args);
//		JsonRecordParser<Article> parser = new JsonRecordParser<Article>(Article.class);
//		RecordReader<Article> training = new RecordReader<Article>(cli.getParam("training"), parser);
//		
//		Disambiguator disambiguator = new Disambiguator();
//		String file = properties.get("wikiminer.classifier");
//		
//		disambiguator.train(training);
//		disambiguator.buildDefaultClassifier();
//		disambiguator.saveClassifier(new File(file));
//		
//		
//		
//		
//		
//		
//		
//	}
//	
//
//
//	public TrainCLI(String[] args) {
//		super(args, params, USAGE);
//	}
//	
//
//
//}
