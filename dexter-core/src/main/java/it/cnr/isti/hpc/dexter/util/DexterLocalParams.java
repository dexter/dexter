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
package it.cnr.isti.hpc.dexter.util;

import it.cnr.isti.hpc.dexter.Tagger;
import it.cnr.isti.hpc.dexter.disambiguation.Disambiguator;
import it.cnr.isti.hpc.dexter.spotter.Spotter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Jan 13, 2014
 */
public class DexterLocalParams {

	private final Map<String, String> params;

	public DexterLocalParams() {
		params = new HashMap<String, String>();
	}

	public void addParam(String key, String value) {
		params.put(key, value);
	}

	public String getParam(String key) {
		return params.get(key);
	}

	public Map<String, String> getParams() {
		return params;
	}

	public boolean containsKey(String key) {
		return params.containsKey(key);
	}

	public int getIntParam(String key) {
		return Integer.parseInt(params.get(key));
	}

	public double getDoubleParam(String key) {
		return Double.parseDouble(params.get(key));
	}

	public double getFloatParam(String key) {
		return Double.parseDouble(params.get(key));
	}

	public Spotter spotter;
	public Disambiguator disambiguator;
	public Tagger tagger;

	public Spotter getSpotter() {
		return spotter;
	}

	public void setSpotter(Spotter spotter) {
		this.spotter = spotter;
	}

	public Disambiguator getDisambiguator() {
		return disambiguator;
	}

	public void setDisambiguator(Disambiguator disambiguator) {
		this.disambiguator = disambiguator;
	}

	public Tagger getTagger() {
		return tagger;
	}

	public void setTagger(Tagger tagger) {
		this.tagger = tagger;
	}

	public boolean hasSpotter() {
		return spotter != null;
	}

	public boolean hasDisambiguator() {
		return disambiguator != null;
	}

	public boolean hasTagger() {
		return tagger != null;
	}

}
