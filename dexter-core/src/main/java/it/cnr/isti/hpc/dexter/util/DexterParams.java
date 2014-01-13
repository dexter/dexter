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

import it.cnr.isti.hpc.dexter.StandardTagger;
import it.cnr.isti.hpc.dexter.Tagger;
import it.cnr.isti.hpc.dexter.disambiguation.Disambiguator;
import it.cnr.isti.hpc.dexter.plugin.PluginLoader;
import it.cnr.isti.hpc.dexter.relatedness.Relatedness;
import it.cnr.isti.hpc.dexter.relatedness.RelatednessFactory;
import it.cnr.isti.hpc.dexter.spotter.Spotter;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Jan 2, 2014
 */
public class DexterParams {
	private static final Logger logger = LoggerFactory
			.getLogger(DexterParams.class);

	private final static DexterParams dexterParams = new DexterParams(
			"dexter-conf.xml");

	Map<String, Tagger> taggers;
	Map<String, Spotter> spotters;
	Map<String, Disambiguator> disambiguators;

	private final PluginLoader loader = new PluginLoader();

	private DexterParams() {
		taggers = new HashMap<String, Tagger>();
		spotters = new HashMap<String, Spotter>();
		disambiguators = new HashMap<String, Disambiguator>();
	}

	private DexterParams(String xmlConfig) {
		super();
		DexterParamsXMLParser params = DexterParamsXMLParser.load(xmlConfig);
		for (it.cnr.isti.hpc.dexter.util.DexterParamsXMLParser.Disambiguator function : params
				.getDisambiguators().getDisambiguators()) {
			Disambiguator disambiguator = loader.getDisambiguator(function
					.getClazz());
			logger.info("registering disambiguator {} -> {} ",
					function.getName(), function.getClazz());
			disambiguators.put(function.getName(), disambiguator);
		}

		for (it.cnr.isti.hpc.dexter.util.DexterParamsXMLParser.Spotter function : params
				.getSpotters().getSpotters()) {
			Spotter spotter = loader.getSpotter(function.getClazz());
			logger.info("registering spotter {} -> {} ", function.getName(),
					function.getClazz());
			spotters.put(function.getName(), spotter);
		}

		for (it.cnr.isti.hpc.dexter.util.DexterParamsXMLParser.RelatednessFunction function : params
				.getRelatednessFunctions().getRelatednessFunctions()) {
			Relatedness relatedness = loader
					.getRelatedness(function.getClazz());
			logger.info("registering relatedness {} -> {} ",
					function.getName(), function.getClazz());
			// FIXME remove relatedness factory??
			RelatednessFactory.register(relatedness);
		}

		for (it.cnr.isti.hpc.dexter.util.DexterParamsXMLParser.Tagger tagger : params
				.getTaggers().getTaggers()) {
			// TODO add tagger from class
			// TODO check if components exist

			Spotter s = spotters.get(tagger.getSpotter());
			Disambiguator d = disambiguators.get(tagger.getDisambiguator());
			Tagger t = new StandardTagger(tagger.getName(), s, d);
		}

	}

	public static DexterParams getInstance() {
		return dexterParams;
	}

	public boolean hasSpotter() {
		// TODO Auto-generated method stub
		return false;
	}

	public Spotter getSpotter() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasDisambiguator() {
		// TODO Auto-generated method stub
		return false;
	}

	public Disambiguator getDisambiguator() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasTagger(String name) {
		return taggers.containsKey(name);
	}

	public Tagger getTagger(String name) {
		return taggers.get(name);
	}
}
