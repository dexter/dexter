/**
 *  Copyright 2013 Diego Ceccarelli
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
package it.cnr.isti.hpc.dexter.milnewitten;

import it.cnr.isti.hpc.dexter.FlatDocument;
import it.cnr.isti.hpc.dexter.entity.EntityMatchList;

import org.junit.Test;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 * Created on May 4, 2013
 */
public class DisambiguatorTest {

	@Test
	public void test() {
		String conll4 = "China says time right for Taiwan talks. BEIJING 1996-08-22 China has said it was time for political talks with Taiwan and that the rival island should take practical steps towards that goal. Consultations should be held to set the time and format of the talks, the official Xinhua news agency quoted Tang Shubei, executive vice chairman of the Association for Relations Across the Taiwan Straits, as saying late on Wednesday.";
		Wikiminer wiki = new Wikiminer();
		EntityMatchList eml = wiki.tag(new FlatDocument(conll4));
		System.out.println(eml);
	}

}
