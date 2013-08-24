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
package it.cnr.isti.hpc.dexter.lucene;

import static org.junit.Assert.assertEquals;
import it.cnr.isti.hpc.dexter.entity.Entity;
import it.cnr.isti.hpc.property.ProjectProperties;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.search.Query;
import org.junit.Ignore;
import org.junit.Test;

/**
 * LuceneHelperTest.java
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 06/ago/2012
 */
public class LuceneHelperTest {

	static ProjectProperties properties = new ProjectProperties(
			LuceneHelperTest.class);
	@Ignore
	@Test	
	public void test() {

		LuceneHelper helper = new LuceneHelper("/tmp/lucene");

		helper.clearIndex();
		
		helper.addDocument(1, " diego ceccarelli test test1 test2 and");
		helper.addDocument(2, " ceccarelli test test1 test2 and ");
		helper.addDocument(3, " ceccarelli test test1 test2  test3 and");
		helper.addDocument(4,
				" diego and ceccarelli test test1 test2 test3 and");
		helper.addDocument(5,
				" diego and ceccarelli test test1 test2 test3 and");
		helper.commit();
		assertEquals(1, helper.getFreq("diego ceccarelli"));
		assertEquals(5, helper.getFreq("ceccarelli test"));
		assertEquals(5, helper.getFreq("test1 test2"));
		assertEquals(5, helper.getFreq("and"));
		assertEquals(2, helper.getFreq("diego and ceccarelli"));
		
//		System.out.println(helper.getCosineSimilarity(4,5));
//		System.out.println(helper.getCosineSimilarity(4,4));
		
	}
	@Ignore
	@Test
	public void testSimilarity() {
		// IdHelper ih = IdHelperFactory.getStdIdHelper();
		LuceneHelper helper = LuceneHelper.getDexterLuceneHelper();
		int idPicasso = 24176;
		int idFrance = 5843419;
		int idGuernica = 12646;
		int idSicily = 27619; // unrelated;
		int idGuernicaPainting = 1055072;
//		System.out.println("picasso vs france\t = "
//				+ helper.getCosineSimilarity(idPicasso, idFrance));
//		System.out.println("picasso vs guernica\t = "
//				+ helper.getCosineSimilarity(idPicasso, idGuernica));
//		System.out.println("picasso vs guernica painting\t = "
//				+ helper.getCosineSimilarity(idPicasso, idGuernicaPainting));
//		System.out.println("picasso vs guernica painting\t = "
//				+ helper.getCosineSimilarity(idGuernicaPainting, idPicasso));
//
//		System.out.println("picasso vs sicily\t = "
//				+ helper.getCosineSimilarity(idPicasso, idSicily));
//		System.out.println("picasso vs picasso\t = "
//				+ helper.getCosineSimilarity(idPicasso, idPicasso));

	}
	
	@Test
	public void testQuery() {
		// IdHelper ih = IdHelperFactory.getStdIdHelper();
		LuceneHelper helper = LuceneHelper.getDexterLuceneHelper();
		int idPicasso = 24176;
		List<Entity> ids = new ArrayList<Entity>();
		ids.add(new Entity(idPicasso));
		int idGuernicaPainting = 1055072;

		ids.add(new Entity(idGuernicaPainting));
		
		Query q = null;
		// FIXME fix
//		try {
//			q = new QueryParser(Version.LUCENE_36, "content",
//					new StandardAnalyzer(Version.LUCENE_36)).parse("Pablo Ruiz y Picasso, known as Pablo Picasso was a Spanish painter, sculptor, printmaker, ceramicist, and stage designer");
//		} catch (ParseException e) {
//			
//		}
//		Spot spot = new Spot("picasso");
//		spot.setEntities(ids);
		//EntityMatchList f= helper.rankBySimilarity(spot, "Pablo Ruiz y Picasso, known as Pablo Picasso was a Spanish painter, sculptor, printmaker, ceramicist, and stage designer");
		//System.out.println(f);

	}

}
