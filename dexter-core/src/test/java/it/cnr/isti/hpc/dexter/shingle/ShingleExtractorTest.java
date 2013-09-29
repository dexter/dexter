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
package it.cnr.isti.hpc.dexter.shingle;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 24/lug/2012
 */
public class ShingleExtractorTest {
	@Test
	public void testShingleExtractor2() {
		String text = "This is a test. let's see if it works, if it works or not.";
		ShingleExtractor shingler = new ShingleExtractor(text);
		shingler.setMaxShingleSize(3);
		Set<String> shingles = new HashSet<String>();
		for (Shingle shingle : shingler) {
			shingles.add(shingle.getText());
		}
		assertTrue(shingles.contains("if it works"));
		assertTrue(shingles.contains("works or not"));
		assertTrue(shingles.contains("this is a"));
		assertFalse(shingles.contains("test. let's"));
	}
}