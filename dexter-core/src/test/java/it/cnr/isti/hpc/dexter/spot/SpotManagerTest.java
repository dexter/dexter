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
package it.cnr.isti.hpc.dexter.spot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.cleaner.HtmlCleaner;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.cleaner.JuniorAndInitialsCleaner;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.cleaner.PrefixCleaner;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.cleaner.QuotesCleaner;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.cleaner.StripCleaner;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.cleaner.TypeCleaner;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.cleaner.UnicodeCleaner;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.filter.LongSpotFilter;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.filter.NumberFilter;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.filter.SymbolFilter;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.mapper.CityMapper;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.mapper.QuotesMapper;

import java.util.Set;

import org.junit.Test;

/**
 * SpotManagerTest.java
 *
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it
 * created on 20/lug/2012
 */
public class SpotManagerTest {

	@Test
	public void testCityMapper(){
		SpotManager sm = new SpotManager(); 
		sm.add(new CityMapper());
		sm.add(new StripCleaner());
		assertTrue(sm.process("ada, wisconsin").contains("ada"));
	}
	
	
	
	
	@Test
	public void testJuniorCleaner(){
		SpotManager sm = new SpotManager();
		sm.add(new JuniorAndInitialsCleaner());
		sm.add(new StripCleaner());
		System.out.println(sm.process("ted ginn, jr. "));
		assertTrue(sm.process("ted ginn, jr. ").contains("ted ginn"));
		assertTrue(sm.process("w. thomas smith, jr").contains("thomas smith"));
		assertTrue(sm.process("a. e. w. mason").contains("mason"));
	}
	
	@Test
	public void testNumericFilter() {
		SpotManager sm = new SpotManager();
		sm.add(new NumberFilter());
		assertTrue(sm.isFilter("12345678900"));
		assertTrue(sm.isFilter("135"));
		assertTrue(sm.isFilter("000"));
		assertTrue(sm.isFilter("1.345.123"));
	}
	
	@Test
	public void testQuotesMapper(){
		SpotManager sm = new SpotManager();
		sm.add(new QuotesMapper());
		sm.add(new StripCleaner());
		Set<String> res = sm.process("dave \"baby\" cortez");
		assertTrue(res.contains("dave cortez"));
		assertTrue(res.contains("baby"));
		res = sm.process("tv series \"supernatural\"");
		assertFalse(res.contains("supernatural"));

		
		//assertTrue(sm.process("").contains("ada"));
	}
	
	@Test
	public void testSymbolFilter(){
		SpotManager sm = new SpotManager();
		sm.add(new SymbolFilter());
		assertTrue(sm.isFilter("$$$$$$$"));
		assertTrue(sm.isFilter("$!\"£$%&/()"));
		assertTrue(sm.isFilter("°°°°°"));
		assertFalse(sm.isFilter("diego"));
		assertFalse(sm.isFilter("a£$%&/("));
		assertTrue(sm.isFilter("1.345.123"));
		
		
	}
	
	@Test
	public void testTypeMapper(){
		SpotManager sm = new SpotManager();
		sm.add(new TypeCleaner());
		sm.add(new QuotesCleaner());
		sm.add(new StripCleaner());
		sm.add(new SymbolFilter());
		assertTrue(sm.process("shot (filmmaking)").contains("shot"));
		assertFalse(sm.process("12 (filmmaking)").contains("12"));
		assertTrue(sm.process("shot#filmmaking").contains("shot"));
		assertTrue(sm.process("\"atomic\" (song)").contains("atomic"));
		assertTrue(sm.process("\"atomic\" (song)").contains("atomic"));
		assertFalse(sm.process("\"1968\"").contains("1968"));
		//assertEquals("kose",);
	}
	
	@Test
	public void testUnicodeCleaner(){
		SpotManager sm = new SpotManager();
		sm.add(new UnicodeCleaner());
		sm.clean("yenikent asa– stadium");
		//assertEquals("kose",);
		
		
	}
		

	@Test
	public void testUnidecode(){
		SpotManager sm = new SpotManager();
		sm.add(new UnicodeCleaner());
		assertEquals("diego",sm.clean("diègo"));
		assertEquals("asociacion",sm.clean("asociación"));
		
		//assertEquals("kose",);
		assertEquals("misar",sm.clean("misar"));
		assertEquals("odon",sm.clean("ödön"));
		
		
	}
	
	@Test
	public void testJavascriptCleaner(){
		SpotManager sm = SpotManager.getStandardSpotManager();
		HtmlCleaner cleaner = new HtmlCleaner();
		assertEquals("''l'isola dei famosi''",cleaner.clean("%27%27l'isola dei famosi%27%27"));
		assertEquals("7",sm.clean("&lt;7&gt;"));
		assertEquals("diego",sm.clean("diego"));
		assertEquals("o-3 fatty acid",sm.clean("&omega;-3 fatty acid"));
	}
	
	@Test
	public void testLongSpotCleaner(){
		SpotManager sp = new SpotManager();
		sp.add(new LongSpotFilter());
		assertTrue(sp.process("this is a really long spot, more than 6 terms").isEmpty());
		System.out.println("-> "+sp.process("this is a short spot (6terms)"));
		assertFalse(sp.process("this is a short spot (6terms)").isEmpty());
	}
	
	@Test
	public void testPrefixCleaner(){
		PrefixCleaner pc = new PrefixCleaner("the ");
		assertEquals("battle of troia",pc.clean("the battle of troia"));	
		assertEquals("battle of troia",pc.clean("   the         battle of troia"));
		assertEquals("battle of troia",pc.clean("   the battle of troia"));
		assertEquals("battle of troia",pc.clean("the        battle of troia"));
		pc = new PrefixCleaner("a ");
		assertEquals("game of thrones",pc.clean("a game of thrones"));
		pc = PrefixCleaner.A_OR_THE;
		assertEquals("battle of troia",pc.clean("the battle of troia"));	
		assertEquals("battle of troia",pc.clean("   the         battle of troia"));
		assertEquals("battle of troia",pc.clean("   the battle of troia"));
		assertEquals("battle of troia",pc.clean("the        battle of troia"));
		assertEquals("game of thrones",pc.clean("a game of thrones"));	
	}
	
		
	
	
	
	

 }
