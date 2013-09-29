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
package it.cnr.isti.hpc.dexter.label;

import static org.junit.Assert.assertEquals;
import it.cnr.isti.hpc.dexter.label.IdHelper;
import it.cnr.isti.hpc.dexter.label.IdHelperFactory;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/**
 * HashHelperTest.java
 * #TODO generate a different database and test
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 05/lug/2012
 */
public class IdHelperTest {
	/**
	 * Logger for this class
	 */

	static {
		Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		root.setLevel(Level.DEBUG);

	}

	/**
	@Test
	public void test() {
		ItemReader<Article> reader = new BaseItemReader<Article>(
				"./src/test/resources/enwiki-top500-pages-articles.json.gz",
				new Article()).filter(TypeFilter.MAIN_CATEGORY_TEMPLATE);
		HashHelper hash = new HashHelper();

		try {
			hash.hashWikipediaTitles(reader);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(hash.getHash("anti-semitism"),
				hash.getHash("antisemitism"));
		assertEquals("antisemitism",
				hash.getLabel(hash.getHash("anti-semitism")));
		assertEquals("antisemitism",
				hash.getLabel(hash.getHash("anti-semitic")));
		assertEquals("alan_turing", hash.getLabel(320));
		assertEquals("analysis_of_variance",
				hash.getLabel(hash.getHash("anova")));
	}
	*/	
	
	@Ignore
	@Test
	public void test2() {
		IdHelper hash = IdHelperFactory.getStdIdHelper();
		assertEquals(hash.getId("amoeboid"), hash.getId("ameboid_stage"));
	}
}
