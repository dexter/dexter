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

import it.cnr.isti.hpc.dexter.common.Field;
import it.cnr.isti.hpc.dexter.spot.clean.SpotManager;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A ContextExtractor extract the context of a spot (i.e., text around the the
 * spot)
 * 
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 24/lug/2012
 */
public class ContextExtractor {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ContextExtractor.class);

	private int windowSize = 50;
	protected String text;
	protected List<Integer> positions = new ArrayList<Integer>();
	
	private final int MAX_TERM_NUMBER=300;

	protected ContextExtractor() {

	}

	public ContextExtractor(Field field) {
		this.text = SpotManager.cleanText(field.getValue());
		init(text);
	}
	
	protected ContextExtractor(String text){
		this.text = text;
		init(text);
	}
	
	private String cleanContext(String context){
		context = context.replaceAll("[\\[\\]]"," ");
		return context;
	}

	protected int closest(int key, List<Integer> list) {
		int size = list.size();
		int delta = size / 2;
		int i = size / 2;
		while (delta > 1) {
			logger.debug("i = {} \t delta = {} ", i, delta);

			int elem = list.get(i);
			delta = (delta % 2 == 0) ? (delta) / 2 : (delta + 1) / 2;
			if (elem == key)
				return i;
			if (elem > key) {
				i = Math.max(i - delta, 0);
			} else {
				i = Math.min(delta + i, size - 1);
			}

		}
		int elem = list.get(i);
		if (elem > key)
			return i - 1;
		return i;
	}

	private String getContext(int l, int r) {
		int start = Math.max(0, l - windowSize / 2);
		int end = Math.min(positions.size() - 1, r + windowSize / 2);
		return text.substring(positions.get(start), positions.get(end)).trim();
	}

	public String getContext(String label) {
		StringBuilder sb = new StringBuilder();
		int terms = 0;
		for (int i = -1; (i = text.indexOf(label, i + 1)) != -1;) {
			sb.append(getContext(termPos(i), termPos(i + label.length() + 1)));
			sb.append(" ");
			terms += windowSize;
			if (terms >= MAX_TERM_NUMBER){
				break;
			}
			
		}
		String context = cleanContext(sb.toString());
		return context.trim();
	}

	public int getWindowSize() {
		return windowSize;
	}

	protected void init(String text) {
		int len = text.length();
		positions.add(0);
		for (int i = 0; i < len; i++) {
			if (text.charAt(i) == ' ') {
				positions.add(i++);
				while ((i < len)
						&& ((text.charAt(i) == ' ') || (text.charAt(i) == '.')
								|| (text.charAt(i) == ',') || (text.charAt(i) == ';')))
					i++;
			}
		}
		positions.add(text.length());
	}

	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
	}

	private int termPos(int pos) {
		// return Collections.binarySearch(positions,pos);
		return closest(pos, positions);

	}

}
