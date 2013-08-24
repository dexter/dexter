///**
// *  Copyright 2012 Diego Ceccarelli
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
//package it.cnr.isti.hpc.dexter.spot;
//
//import it.cnr.isti.hpc.io.IOUtils;
//import it.cnr.isti.hpc.io.reader.BaseItemReader;
//import it.cnr.isti.hpc.io.reader.ItemReader;
//import it.isti.cnr.hpc.wikipedia.domain.Article;
//import it.isti.cnr.hpc.wikipedia.domain.Link;
//import it.isti.cnr.hpc.wikipedia.reader.filter.TypeFilter;
//
//import java.io.BufferedWriter;
//import java.io.IOException;
//
//import org.junit.Ignore;
//import org.junit.Test;
//
///**
// * ContextExtractorTest.java
// * 
// * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 24/lug/2012
// */
//public class ArticleContextExtractorTest {
//
//	@Test
//	public void test() throws IOException {
//		ItemReader<Article> reader = new BaseItemReader<Article>(
//				"./src/test/resources/enwiki-top500-pages-articles.json.gz",
//				new Article()).filter(TypeFilter.MAIN_CATEGORY_TEMPLATE);
//		BufferedWriter writer = IOUtils.getPlainOrCompressedWriter("/tmp/test");
//
//		for (Article a : reader) {
//			ArticleContextExtractor extractor = new ArticleContextExtractor(a);
//			extractor.setWindowSize(2);
//			for (Link l : a.getLinks()) {
//				for (String spot : SpotManager.getStandardSpotManager()
//						.process(l.getDescription().toLowerCase())) {
//					String context = extractor.getContext(spot);
//					// writer.write("------------------\n");
//					// writer.write(extractor.getCleanAsciiText(a));
//					// writer.write("\n");
//					// writer.write("------------------\n");
//					writer.write(spot);
//					writer.write("\t");
//					writer.write(context);
//					writer.write("\n");
//				}
//
//			}
//		}
//		writer.close();
//	}
//
//	@Ignore
//	@Test
//	public void testClean() throws IOException {
//		ItemReader<Article> reader = new BaseItemReader<Article>(
//				"./src/test/resources/enwiki-top500-pages-articles.json.gz",
//				new Article()).filter(TypeFilter.MAIN_CATEGORY_TEMPLATE);
//		BufferedWriter writer = IOUtils.getPlainOrCompressedWriter("/tmp/test");
//
//		for (Article a : reader) {
//			ArticleContextExtractor extractor = new ArticleContextExtractor(a);
//			writer.write(extractor.getCleanAsciiText(a));
//		}
//		writer.close();
//	}
//
//}
