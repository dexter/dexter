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
package it.cnr.isti.hpc.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Allows to encode a list of integers as string containing the integer encoded 
 * in hexadecimal (and optionally compressed) 
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 29/lug/2012
 */
public class IntArrayString {

	private static StringBuffer sb = new StringBuffer();
	private static final char SEPARATOR = ',';
	private transient boolean deltaEncoded = true;

	public IntArrayString() {
	}

	public IntArrayString(boolean deltaEncoded) {
		this.deltaEncoded = deltaEncoded;
	}

	public String toString(List<Integer> array) {

		if (deltaEncoded) {
			array = deltaEncode(array);
		}
		return arrayToString(array);
	}

	public String toString(Integer[] array) {
		return toString(Arrays.asList(array));
	}

	private List<Integer> deltaEncode(List<Integer> array) {
		List<Integer> deltaEncoded = new ArrayList<Integer>(array.size());
		Collections.sort(array);
		if (array.isEmpty())
			return array;
		int previous = array.get(0);
		Integer current = 0;
		deltaEncoded.add(previous);
		for (int i = 1; i < array.size(); i++) {
			current = array.get(i);
			deltaEncoded.add(current - previous);
			previous = current;
		}
		return deltaEncoded;
	}

	private List<Integer> deltaDecode(List<Integer> array) {
		List<Integer> deltaDecode = new ArrayList<Integer>(array.size());
		if (array.isEmpty())
			return array;

		Integer previous = array.get(0);
		deltaDecode.add(previous);
		for (int i = 1; i < array.size(); i++) {
			previous += array.get(i);
			deltaDecode.add(previous);
		}
		return deltaDecode;
	}

	private String arrayToString(List<Integer> array) {
		sb.setLength(0);
		if (array.isEmpty())
			return "";
		for (int i : array) {
			sb.append(Integer.toHexString(i));
			sb.append(SEPARATOR);
		}
		String string = sb.substring(0, sb.length() - 1);
		return string;
	}

	private List<Integer> stringToArray(String string) {
		sb.setLength(0);
		List<Integer> array = new ArrayList<Integer>();
		if (string.trim().isEmpty())
			return array;
		for (int i = 0; i < string.length(); i++) {
			char ch = string.charAt(i);
			if (ch == SEPARATOR) {
				Integer j = Integer.parseInt(sb.toString(), 16);
				array.add(j);
				sb.setLength(0);
			} else {
				sb.append(ch);
			}
		}
		if (sb.length() > 0) {
			Integer j = Integer.parseInt(sb.toString(), 16);
			array.add(j);
		}
		return array;
	}

	public List<Integer> toArray(String string) {
		List<Integer> array = stringToArray(string);
		if (deltaEncoded)
			array = deltaDecode(array);
		return array;
	}

}
