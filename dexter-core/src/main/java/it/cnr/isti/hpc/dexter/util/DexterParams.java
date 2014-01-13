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
import it.cnr.isti.hpc.dexter.relatedness.Relatedness;
import it.cnr.isti.hpc.dexter.relatedness.RelatednessFactory;
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

	private final static DexterParams dexterParams = new DexterParams(
			"dexter-conf.xml");

	Map<String, Tagger> taggers;
	Map<String, Spotter> spotters;
	Map<String, Disambiguator> disambiguators;
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

	String defaultRelatedness;

	private PluginLoader loader;

	private File wikiToIdFile;

	private DexterParams() {
		taggers = new HashMap<String, Tagger>();
		spotters = new HashMap<String, Spotter>();
		disambiguators = new HashMap<String, Disambiguator>();
		graphs = new HashMap<String, Map<Direction, String>>();
		models = new HashMap<String, String>();
		cacheSize = new HashMap<String, Integer>();
		thresholds = new HashMap<String, Float>();
	}

	private DexterParams(String xmlConfig) {
		super();
		DexterParamsXMLParser params = DexterParamsXMLParser.load(xmlConfig);

		loader = new PluginLoader(new File(params.getLibs().getLib()));
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
			taggers.put(tagger.getName(), t);
		}

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

	public File getSpotsData() {
		return spotsData;
	}

	public String getDefaultRelatedness() {
		return defaultRelatedness;
	}

	public static DexterParams getInstance() {
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
		return spotters.get(name);
	}

	public boolean hasDisambiguator(String name) {
		return disambiguators.containsKey(name);
	}

	public Disambiguator getDisambiguator(String name) {
		return disambiguators.get(name);
	}

	public Float getThreshold(String name) {
		return thresholds.get(name);
	}

	public boolean hasTagger(String name) {
		return taggers.containsKey(name);
	}

	public Tagger getTagger(String name) {
		return taggers.get(name);
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
