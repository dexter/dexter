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
//package it.cnr.isti.hpc.dexter.shingle;
//
//import org.junit.Test;
//
///**
// * ContextExtractorTest.java
// * 
// * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 24/lug/2012
// */
//public class ShingleExtractorTest {
//
//	// @Ignore
//	// @Test
//	// public void testShingleExtractor() throws IOException {
//	// ItemReader<Article> reader = new BaseItemReader<Article>(
//	// "./src/test/resources/enwiki-top500-pages-articles.json.gz",
//	// new Article()).filter(TypeFilter.MAIN_CATEGORY_TEMPLATE);
//	//
//	// for (Article a : reader) {
//	// ShingleExtractor ase = new ShingleExtractor(a);
//	// ase.setMaxShingleSize(3);
//	// for(String shingle: ase) {
//	// System.out.println(shingle);
//	// }
//	// }
//	// }
//
//	@Test
//	public void testShingleExtractor2() {
//		String text = "Questa è una prova. Vediamo come funziona, se va a modo o meno.";
//		ShingleExtractor shingler = new ShingleExtractor(text);
//		shingler.setMaxShingleSize(3);
//		for (Shingle shingle : shingler) {
//			System.out.println(shingle);
//			System.out.println("original: " + shingle.originalShingle(text));
//		}
//	}
//
//	@Test
//	public void testShingleExtractor3() {
//		String text = "María Belén Rodríguez Cozzani (Spanish pronunciation: [maˈɾi.a βeˈlen roðˈɾiɣeθ koˈθani]) (born 20 September 1984) is an Argentine showgirl, model, television personality, actress, and media icon who lives and works in Italy.        She is known by the name of Belén Rodríguez and has presented many variety shows.";
//		ShingleExtractor shingler = new ShingleExtractor(text);
//		shingler.setMaxShingleSize(3);
//		for (Shingle shingle : shingler) {
//			System.out.println(shingle);
//			System.out.println("original: " + shingle.originalShingle(text));
//		}
//	}
//
// }