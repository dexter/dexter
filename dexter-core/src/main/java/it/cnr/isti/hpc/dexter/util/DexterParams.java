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
import it.cnr.isti.hpc.dexter.graph.NodeStar.Direction;
import it.cnr.isti.hpc.dexter.plugin.PluginLoader;
import it.cnr.isti.hpc.dexter.spotter.Spotter;

import java.io.File;
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

	private static DexterParams dexterParams;
	Map<String, DexterParamsXMLParser.Tagger> taggers;
	Map<String, String> spotters;
	Map<String, String> disambiguators;
	Map<String, Map<Direction, String>> graphs;
	Map<String, String> models;
	Map<String, Integer> cacheSize;
	Map<String, Float> thresholds;

	File defaultModel;
	File graphDir;
	File labelDir;
	File indexDir;

	File spotsData;
	File spotsEliasFano;
	File spotsOffsetData;
	File spotsPerfectHash;
	File plainSpots;

	private static final String DEFAULT = "___default";

	String defaultRelatedness;

	private DexterParamsXMLParser params;

	private PluginLoader loader;

	private File wikiToIdFile;

	private DexterParams() {
		taggers = new HashMap<String, DexterParamsXMLParser.Tagger>();
		spotters = new HashMap<String, String>();
		disambiguators = new HashMap<String, String>();
		graphs = new HashMap<String, Map<Direction, String>>();
		models = new HashMap<String, String>();
		cacheSize = new HashMap<String, Integer>();
		thresholds = new HashMap<String, Float>();
	}

	private DexterParams(String xmlConfig) {
		this();
		params = DexterParamsXMLParser.load(xmlConfig);

		loader = new PluginLoader(new File(params.getLibs().getLib()));

		for (DexterParamsXMLParser.Graph graph : params.getGraphs().getGraphs()) {
			Map<Direction, String> names = new HashMap<Direction, String>();
			names.put(Direction.IN, graph.getIncoming());
			names.put(Direction.OUT, graph.getOutcoming());
			graphs.put(graph.getName(), names);
			logger.info("registering graph {} in: {}", graph.getName(),
					graph.getIncoming());
			logger.info("registering graph {} out: {}", graph.getName(),
					graph.getOutcoming());
		}

		for (DexterParamsXMLParser.Model model : params.getModels().getModels()) {
			models.put(model.getName(), model.getPath());
		}

		for (DexterParamsXMLParser.Cache cache : params.getCaches().getCaches()) {
			cacheSize.put(cache.getName(), cache.getSize());
		}

		for (DexterParamsXMLParser.Threshold threshold : params.getThresholds()
				.getThresholds()) {
			thresholds.put(threshold.getName(), threshold.getValue());
		}

		defaultModel = new File(
				models.get(params.getModels().getDefaultModel()));

		graphDir = new File(defaultModel, params.getGraphs().getDir());

		labelDir = new File(defaultModel, params.getLabels().getDir());
		indexDir = new File(defaultModel, params.getIndex().getDir());
		wikiToIdFile = new File(indexDir, params.getIndex().getWikiIdMap());
		defaultRelatedness = params.getRelatednessFunctions()
				.getDefaultFunction();

		File spotsDir = new File(defaultModel, params.getSpotRepository()
				.getDir());
		spotsData = new File(spotsDir, params.getSpotRepository()
				.getSpotsData());
		spotsOffsetData = new File(spotsDir, params.getSpotRepository()
				.getOffsets());
		spotsEliasFano = new File(spotsDir, params.getSpotRepository()
				.getEliasFanoOffsets());
		spotsPerfectHash = new File(spotsDir, params.getSpotRepository()
				.getPerfectHash());
		plainSpots = new File(defaultModel, params.getSpotRepository()
				.getPlainSpots());

	}

	private void loadDisambiguators() {
		for (it.cnr.isti.hpc.dexter.util.DexterParamsXMLParser.Disambiguator function : params
				.getDisambiguators().getDisambiguators()) {

			logger.info("registering disambiguator {} -> {} ",
					function.getName(), function.getClazz());
			disambiguators.put(function.getName(), function.getClazz());
		}
		String defaultName = params.getDisambiguators()
				.getDefaultDisambiguator();
		String clazz = disambiguators.get(defaultName);
		disambiguators.put(DEFAULT, clazz);
	}

	private void loadSpotters() {
		for (it.cnr.isti.hpc.dexter.util.DexterParamsXMLParser.Spotter function : params
				.getSpotters().getSpotters()) {
			logger.info("registering spotter {} -> {} ", function.getName(),
					function.getClazz());
			spotters.put(function.getName(), function.getClazz());

		}
		String defaultName = params.getSpotters().getDefaultSpotter();
		String clazz = spotters.get(defaultName);
		spotters.put(DEFAULT, clazz);
	}

	private void loadTaggers() {
		for (DexterParamsXMLParser.Tagger tagger : params.getTaggers()
				.getTaggers()) {
			// TODO add tagger from class
			// TODO check if components exist

			taggers.put(tagger.getName(), tagger);
		}

		String defaultName = params.getTaggers().getDefaultTagger();
		DexterParamsXMLParser.Tagger tagger = taggers.get(defaultName);
		taggers.put(DEFAULT, tagger);

	}

	private void loadRelatednessFunctions() {
		for (it.cnr.isti.hpc.dexter.util.DexterParamsXMLParser.RelatednessFunction function : params
				.getRelatednessFunctions().getRelatednessFunctions()) {
			logger.info("registering relatedness {} -> {} ",
					function.getName(), function.getClazz());
			// FIXME remove relatedness factory??
			// RelatednessFactory.register(relatedness);
		}
	}

	public File getSpotsData() {
		return spotsData;
	}

	public String getDefaultRelatedness() {
		return defaultRelatedness;
	}

	public static DexterParams getInstance() {
		if (dexterParams == null) {
			dexterParams = new DexterParams("dexter-conf.xml");
			dexterParams.loadDisambiguators();
			dexterParams.loadRelatednessFunctions();
			dexterParams.loadSpotters();
			dexterParams.loadTaggers();
		}
		return dexterParams;
	}

	public int getCacheSize(String name) {
		if (!cacheSize.containsKey(name)) {
			logger.warn(
					"cannot find cache size for {}, use default value (1000) ",
					name);
			return 1000;
		}
		return cacheSize.get(name);
	}

	public boolean hasSpotter(String name) {
		return spotters.containsKey(name);
	}

	public Spotter getSpotter(String name) {
		if ((name == null) || (name.isEmpty())) {
			name = DEFAULT;
		}
		return loader.getSpotter(spotters.get(name));
	}

	public boolean hasDisambiguator(String name) {
		return disambiguators.containsKey(name);
	}

	public Disambiguator getDisambiguator(String name) {
		if ((name == null) || (name.isEmpty())) {
			name = DEFAULT;
		}
		return loader.getDisambiguator(disambiguators.get(name));
	}

	public Float getThreshold(String name) {
		return thresholds.get(name);
	}

	public boolean hasTagger(String name) {
		return taggers.containsKey(name);
	}

	public Tagger getTagger(String name) {
		if ((name == null) || (name.isEmpty())) {
			name = DEFAULT;
		}
		DexterParamsXMLParser.Tagger t = taggers.get(name);
		Tagger tagger = new StandardTagger(name, getSpotter(t.getSpotter()),
				getDisambiguator(t.getDisambiguator()));
		return tagger;
	}

	public File getDefaultModel() {
		return defaultModel;
	}

	public File getGraphDir() {
		return graphDir;
	}

	public File getLabelDir() {
		return labelDir;
	}

	public File getGraph(String string, Direction direction) {
		return new File(getGraphDir(), graphs.get(string).get(direction));
	}

	public File getIndexDir() {
		return indexDir;
	}

	public File getWikiToIdFile() {
		return wikiToIdFile;
	}

	public File getSpotsOffsetData() {
		return spotsOffsetData;
	}

	public File getSpotsEliasFano() {
		return spotsEliasFano;
	}

	public File getSpotsPerfectHash() {
		return spotsPerfectHash;
	}

	public File getPlainSpots() {
		return plainSpots;

	}

}
