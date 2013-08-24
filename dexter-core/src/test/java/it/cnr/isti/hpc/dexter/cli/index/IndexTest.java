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
package it.cnr.isti.hpc.dexter.cli.index;

import it.cnr.isti.hpc.dexter.lucene.LuceneHelper;
import it.cnr.isti.hpc.property.ProjectProperties;

import org.junit.Test;

/**
 * IndexTest.java
 *
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it
 * created on 19/lug/2012
 */
public class IndexTest {

	@Test
	public void test() {
		ProjectProperties properties =new  ProjectProperties(this.getClass());
		LuceneHelper lucene = new LuceneHelper(properties.get("lucene.index"));
		System.out.println("alain connes    \t "+lucene.getFreq("alain connes"));
		System.out.println("and             \t "+lucene.getFreq("and"));
		System.out.println("social          \t "+lucene.getFreq("social"));
		System.out.println("social movement \t "+lucene.getFreq("social movement"));
	}

}
