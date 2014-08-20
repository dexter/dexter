/**
 *  Copyright 2014 Diego Ceccarelli
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

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Aug 19, 2014
 */
public class CityTokenFilter extends TokenFilter {

	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
	private final Set<String> spots = new HashSet<String>();
	private Iterator<String> iterator = null;

	public void map(Set<String> spots, String spot) {
		spots.add(spot);
		if (spot.matches("^[^,]+,[a-z ]+$"))
			removeRegex(spots, spot, " *,.+$");

	}

	private void removeRegex(Set<String> mappings, String spot, String regex) {
		String str = spot.replaceAll(regex, "");
		if (!str.equals(spot)) {
			mappings.add(str);
		}
	}

	protected CityTokenFilter(TokenStream input) {
		super(input);

	}

	@Override
	public boolean incrementToken() throws IOException {

		if (iterator == null || !iterator.hasNext()) {
			if (!input.incrementToken())
				return false;
			spots.clear();

			map(spots, termAtt.toString());
			iterator = spots.iterator();
		}
		if (iterator.hasNext()) {
			String next = iterator.next();
			System.out.println("add " + next);
			termAtt.copyBuffer(next.toCharArray(), 0, next.length());
			termAtt.setLength(next.length());
		}

		return true;
	}
}
