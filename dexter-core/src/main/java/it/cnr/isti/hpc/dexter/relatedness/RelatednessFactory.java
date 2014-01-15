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
package it.cnr.isti.hpc.dexter.relatedness;

import it.cnr.isti.hpc.dexter.util.DexterParams;

import java.util.HashMap;
import java.util.Map;

/**
 * Allows to retrieve a particular relatedness function given its name.
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Oct 10, 2012
 */
public class RelatednessFactory {

	private static DexterParams params = DexterParams.getInstance();

	private static Map<String, Relatedness> relmap = new HashMap<String, Relatedness>();
	public Relatedness relatedness;

	public RelatednessFactory() {
		String type = params.getDefaultRelatedness();
		if (!relmap.containsKey(type)) {
			// params.
			relatedness = params.getRelatedness(type);
			relmap.put(type, relatedness);
		} else {
			relatedness = relmap.get(type);
		}
		if (relatedness == null) {
			throw new UnsupportedOperationException("cannot find relatedness "
					+ type);
		}
	}

	public RelatednessFactory(String type) {
		if (!relmap.containsKey(type)) {
			// params.
			relatedness = params.getRelatedness(type);
			relmap.put(type, relatedness);
		} else {
			relatedness = relmap.get(type);
		}
		if (relatedness == null) {
			throw new UnsupportedOperationException("cannot find relatedness "
					+ type);
		}
	}

	public static void register(Relatedness rel) {
		relmap.put(rel.getName(), rel);
	}

	public double getScore(int x, int y) {
		relatedness.set(x, y);
		return relatedness.getScore();
	}

	public boolean hasNegativeScores() {
		return relatedness.hasNegativeScores();
	}

	public Relatedness getRelatedness(int x, int y) {
		relatedness.set(x, y);
		return relatedness.copy();
	}

}
