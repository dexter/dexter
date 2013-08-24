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
//public class TestCLI extends AbstractCommandLineInterface {
//	/**
//	 * Logger for this class
//	 */
//	private static final Logger logger = LoggerFactory
//			.getLogger(TestCLI.class);
//	
//	FIXME SOLVE
//
//
//	private static final String USAGE = "java -cp $jar "
//			+ TestCLI.class
//			+ " -training training.json  ";
//	private static String[] params = new String[] {  "test" };
//
//	public static void main(String[] args) {
//		TestCLI cli = new TestCLI(args);
////		JsonRecordParser<Article> parser = new JsonRecordParser<Article>(Article.class);
//		RecordReader<Article> test = new RecordReader<Article>(cli.getParam("test"), parser);
//		
//		Disambiguator disambiguator = new Disambiguator();
//		disambiguator.loadDefaultClassifier();
//		disambiguator.test(test);
//		
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
//	public TestCLI(String[] args) {
//		super(args, params, USAGE);
//	}
//	
//
//
//}
