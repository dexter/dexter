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
import static org.junit.Assert.assertTrue;
import it.cnr.isti.hpc.dexter.entity.Entity;
import it.cnr.isti.hpc.dexter.spot.filter.CommonnessFilter;

import org.junit.Test;

/**
 * SpotReaderTest.java
 *
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it
 * created on 01/ago/2012
 */
public class SpotReaderTest {

	@Test
	public void test() {
		SpotReader reader = new SpotReader("./src/test/resources/spot-src-target-sample.txt","./src/test/resources/spot-frequencies.txt");
		reader.addFilter(new CommonnessFilter());
		Spot s = reader.next();
		assertEquals(20, s.getLink());
		assertEquals("argentina", s.getMention());
		assertEquals(38541,s.getFrequency());
		assertEquals(1,s.getEntities().size());
		assertTrue(s.getEntities().contains(new Entity(18951905)));
		System.out.println(s);
		System.out.println(s.toTsv());
		
		s = reader.next();
		assertEquals(5, s.getLink());
		assertEquals("goodbye argentina", s.getMention());
		assertEquals(1,s.getFrequency());
		assertEquals(2,s.getEntities().size());
		assertTrue(s.getEntities().contains(new Entity(8423965)));
		assertTrue(s.getEntities().contains(new Entity(8423966)));
		assertEquals(1,s.getLinkProbability(),0.001);
		System.out.println(s);
		System.out.println(s.toTsv());
		
		s = reader.next();
		System.out.println(s);
		System.out.println(s.toTsv());
		
		
	}
	

}
