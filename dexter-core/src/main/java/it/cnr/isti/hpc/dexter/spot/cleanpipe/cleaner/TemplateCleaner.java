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
package it.cnr.isti.hpc.dexter.spot.cleanpipe.cleaner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * TemplateCleaner removes all the text matching TEMPLATE[....]
 * 
 * @Deprecated language dependent
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 20/lug/2012
 */
public class TemplateCleaner extends Cleaner<String> {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(TemplateCleaner.class);

	static StringBuilder sb = new StringBuilder();

	public static void main(String[] args) {
		TemplateCleaner cleaner = new TemplateCleaner();
		System.out
				.println(cleaner
						.clean(" Spain, 1868-1898 [p. 135]. TEMPLATE[Main, Mutualism (economic theory)] Mutualism "));
		System.out
				.println(cleaner
						.clean(" stateless society.TEMPLATE[Cite_book, last=Bakun      1 in, first=Mikhail, title=Statism and Anarchy, publisher=Cambridge University Press, location=Cambridge, year=1990, isbn=0-521-36182-6, quote=They [the Marxists] maintain that only a dictatorship - their dictatorship, of course - c      1 an create the will of the people, while our answer to this is: No dictatorship can have any other aim but that of self-perpetuation, and it can beget only slavery in the people tolerating it; freedom can be created only by freedom      1 , that is, by a universal rebellion on the part of the people and free organization of the toiling masses from the bottom up.] Anarchist,"));

	}

	public String clean(String spot) {
		sb.setLength(0);
		int lastIndex = 0;
		int len = spot.length();
		logger.debug("{}", "----------------------------");
		logger.debug("{}", spot);
		logger.debug("{}", "----------------------------");
		for (int i = -1; (i = spot.indexOf("TEMPLATE[", i + 1)) != -1;) {

			sb.append(spot.substring(lastIndex, i));
			logger.debug("* {}", spot.substring(lastIndex, i));

			sb.append(" ");
			int count = 1;
			i += "TEMPLATE[".length();

			while (i < len && count > 0) {

				if (spot.charAt(i) == '[')
					count++;
				if (spot.charAt(i) == ']')
					count--;
				i++;
			}

			lastIndex = i;
			i--;

		}
		sb.append(spot.substring(lastIndex));
		logger.debug("* {}," + spot.substring(lastIndex));
		return sb.toString();
	}

	public boolean post() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean pre() {
		// TODO Auto-generated method stub
		return true;
	}

}
