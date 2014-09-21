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
package it.cnr.isti.hpc.dexter.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Set;

import org.junit.Test;

/**
 * SpotManagerTest.java
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 20/lug/2012
 */
public class SpotAnalyzerTest {

	@Test
	public void testCityMapper() throws IOException {
		SpotCleaner sm = new SpotCleaner();

		assertTrue(sm.enrich("ada, wisconsin").contains("ada"));
	}

	@Test
	public void testJuniorCleaner() throws IOException {
		SpotCleaner sm = new SpotCleaner();

		assertTrue(sm.clean("ted ginn, jr. ").contains("ted ginn"));
		assertTrue(sm.clean("w. thomas smith, jr").contains("thomas smith"));
		assertTrue(sm.clean("a. e. w. mason").contains("mason"));
	}

	@Test
	public void testNumericFilter() throws IOException {
		SpotCleaner sm = new SpotCleaner();
		System.out.println(sm.clean("12345678900"));
		System.out.println(sm.clean("135"));
		System.out.println(sm.clean("000"));
		System.out.println(sm.clean("1.345.123"));
	}

	@Test
	public void testQuotesMapper() throws IOException {
		SpotCleaner sm = new SpotCleaner();

		Set<String> res = sm.enrich("dave \"baby\" cortez");
		assertTrue(res.contains("dave cortez"));
		assertTrue(res.contains("baby"));
		res = sm.enrich("tv series \"supernatural\"");
		assertFalse(res.contains("supernatural"));

		// assertTrue(sm.process("").contains("ada"));
	}

	@Test
	public void testSymbolFilter() throws IOException {
		SpotCleaner sm = new SpotCleaner();
		assertTrue(sm.clean("$$$$$$$").isEmpty());
		assertTrue(sm.clean("$!\"£$%&/()").isEmpty());
		assertTrue(sm.clean("°°°°°").isEmpty());
		assertFalse(sm.clean("diego").isEmpty());

		assertFalse(sm.clean("a£$%&/(").isEmpty());
		assertTrue(sm.clean("1.345.123").isEmpty());

	}

	@Test
	public void testTypeMapper() throws IOException {
		SpotCleaner sm = new SpotCleaner();
		assertTrue(sm.clean("shot (filmmaking)").equals("shot"));
		assertFalse(sm.clean("12 (filmmaking)").equals("12"));
		assertTrue(sm.clean("Shot_(filmmaking)").equals("shot"));

		assertTrue(sm.clean("shot#filmmaking").equals("shot"));
		assertTrue(sm.clean("\"atomic\" (song)").equals("atomic"));
		assertTrue(sm.clean("\"atomic\" (song)").equals("atomic"));
		assertFalse(sm.clean("\"1968\"").equals("1968"));
		// assertEquals("kose",);
	}

	@Test
	public void testUnidecode() throws IOException {
		SpotCleaner sm = new SpotCleaner();
		assertEquals("diego", sm.clean("diègo"));
		assertEquals("asociacion", sm.clean("asociación"));

		// assertEquals("kose",);
		assertEquals("misar", sm.clean("misar"));
		assertEquals("odon", sm.clean("ödön"));

	}

	@Test
	public void testJavascriptCleaner() throws IOException {
		SpotCleaner sm = new SpotCleaner();
		assertEquals("l'isola dei famosi",
				sm.clean("%27%27l'isola dei famosi%27%27"));
		assertEquals("", sm.clean("&lt;7&gt;"));
		assertEquals("diego", sm.clean("diego"));

		// FIXME think about dashes
		// assertEquals("o 3 fatty acid",sm.clean("&omega;-3 fatty acid"));
	}

	// @Test
	// public void testLongSpotCleaner() {
	// SpotAnalyzer sm = new SpotAnalyzer();
	// assertTrue(sp.process("this is a really long spot, more than 6 terms")
	// .isEmpty());
	// System.out.println("-> " + sp.process("this is a short spot (6terms)"));
	// assertFalse(sp.process("this is a short spot (6terms)").isEmpty());
	// }

	@Test
	public void testPrefixCleaner() throws IOException {
		SpotCleaner pc = new SpotCleaner();
		assertEquals("battle of troia", pc.clean("the battle of troia"));
		assertEquals("battle of troia",
				pc.clean("   the         battle of troia"));
		assertEquals("battle of troia", pc.clean("   the battle of troia"));
		assertEquals("battle of troia", pc.clean("the        battle of troia"));
		assertEquals("game of thrones", pc.clean("a game of thrones"));
		assertEquals("battle of troia", pc.clean("the battle of troia"));
		assertEquals("battle of troia",
				pc.clean("   the         battle of troia"));
		assertEquals("battle of troia", pc.clean("   the battle of troia"));
		assertEquals("battle of troia", pc.clean("the        battle of troia"));
		assertEquals("game of thrones", pc.clean("a game of thrones"));
	}

	// @Test
	// public void testGetAllSpots(){
	// SpotManager sm = SpotManager.getStandardSpotManager();
	//
	// String json =
	// IOUtils.getFileAsUTF8String("./src/test/resources/article.json.gz");
	// Article a = Article.fromJson(json);
	// Set<String> spots = sm.getAllSpots(a);
	// for (String s : spots){
	// System.out.println("---> "+s);
	// }
	// }

	@Test
	public void testProcess() throws IOException {
		SpotCleaner sm = new SpotCleaner();
		Set<String> set = sm
				.enrich("William Arthur Waldegrave, Baron_Waldegrave of North Hill");
		assertTrue(set.contains("william arthur waldegrave"));
		set = sm.enrich("S. Zorig");
		assertTrue(set.contains("zorig"));

		set = sm.enrich("- -1- benzofuran-2-yl -2-propylaminopentane...");
		System.out.println(set);
		assertTrue(set.contains("1 benzofuran 2 yl 2 propylaminopentane"));
		set = sm.enrich("-endo-fenchol dehydrogenase");
		assertTrue(set.contains("endo fenchol dehydrogenase"));
		set = sm.enrich("(-)-zingiberene");
		assertTrue(set.contains("zingiberene"));

	}

}
