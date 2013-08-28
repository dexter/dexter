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
import static org.junit.Assert.assertTrue;
import it.cnr.isti.hpc.property.ProjectProperties;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.Files;

/**
 * LuceneHelperTest.java
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 06/ago/2012
 */
public class LuceneHelperTest {

	static ProjectProperties properties = new ProjectProperties(
			LuceneHelperTest.class);

	static LuceneHelper helper = null;

	static File luceneDir = Files.createTempDir();

	@BeforeClass
	public static void init() throws IOException {
		File wikiIdtToLuceneId = File.createTempFile("dexter-", "tmp");
		helper = new LuceneHelper(wikiIdtToLuceneId, luceneDir);
		helper.clearIndex();

		helper.addDocument(1, " diego ceccarelli test test1 test2 and");
		helper.addDocument(2, " ceccarelli test test1 test2 and ");
		helper.addDocument(3, " ceccarelli test test1 test2  test3 and");
		helper.addDocument(4,
				" diego and ceccarelli test test1 test2 test3 and");
		helper.addDocument(5,
				" diego and ceccarelli test test1 test2 test3 and");
		helper.addDocument(6, " pippo pippo pippo pippos");
		helper.commit();
		helper.closeWriter();
		helper = new LuceneHelper(wikiIdtToLuceneId, luceneDir);

	}

	@Test
	public void testFreq() {

		assertEquals(1, helper.getFreq("diego ceccarelli"));
		assertEquals(5, helper.getFreq("ceccarelli test"));
		assertEquals(5, helper.getFreq("test1 test2"));
		assertEquals(5, helper.getFreq("and"));
		assertEquals(2, helper.getFreq("diego and ceccarelli"));

	}

	@Test
	public void testGetArticle() {
		assertEquals("diego ceccarelli test test1 test2 and", helper
				.getArticle(1).getText().trim());
		assertEquals("ceccarelli test test1 test2 and", helper.getArticle(2)
				.getText().trim());
		assertEquals("pippo pippo pippo pippos", helper.getArticle(6).getText()
				.trim());
	}

	@Test
	public void testSimilarity() {

		assertEquals(0, helper.getCosineSimilarity(1, 6), 0.01);
		assertEquals(1, helper.getCosineSimilarity(1, 1), 0.01);
		assertTrue(helper.getCosineSimilarity(1, 4) > helper
				.getCosineSimilarity(1, 3));
	}

	@Test
	public void testQuery() {
		List<Integer> results = helper.query("diego");
		assertEquals(3, results.size());
		Set<Integer> expected = new HashSet<Integer>();
		expected.add(1);
		expected.add(4);
		expected.add(5);

		assertEquals(expected, new HashSet<Integer>(results));

	}

}
