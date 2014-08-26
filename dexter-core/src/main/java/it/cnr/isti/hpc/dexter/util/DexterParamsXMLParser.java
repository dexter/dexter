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

import it.cnr.isti.hpc.io.IOUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Jan 2, 2014
 */
public class DexterParamsXMLParser {
	private static final Logger logger = LoggerFactory
			.getLogger(DexterParamsXMLParser.class);

	private Models models;
	private Labels labels;
	private Index index;
	private Thresholds thresholds;
	private Graphs graphs;
	private Rankers rankers;
	private Libs libs;
	private RelatednessFunctions relatednessFunctions;
	private Spotters spotters;
	private Taggers taggers;
	private Caches caches;
	private SpotFilters spotFilters;

	private SpotRepository spotRepository;

	public SpotRepository getSpotRepository() {
		return spotRepository;
	}

	public void setSpotRepository(SpotRepository spotRepository) {
		this.spotRepository = spotRepository;
	}

	public Caches getCaches() {
		return caches;
	}

	public void setCaches(Caches caches) {
		this.caches = caches;
	}

	private Disambiguators disambiguators = new Disambiguators();

	public Spotters getSpotters() {
		return spotters;
	}

	public void setSpotters(Spotters spotters) {
		this.spotters = spotters;
	}

	public Models getModels() {
		return models;
	}

	public void setModels(Models models) {
		this.models = models;
	}

	public SpotFilters getSpotFilters() {
		return spotFilters;
	}

	public void setSpotFilters(SpotFilters spotFilters) {
		this.spotFilters = spotFilters;
	}

	public Labels getLabels() {
		return labels;
	}

	public void setLabels(Labels labels) {
		this.labels = labels;
	}

	public Index getIndex() {
		return index;
	}

	public void setIndex(Index index) {
		this.index = index;
	}

	public Thresholds getThresholds() {
		return thresholds;
	}

	public void setThresholds(Thresholds thresholds) {
		this.thresholds = thresholds;
	}

	public Graphs getGraphs() {
		return graphs;
	}

	public void setGraphs(Graphs graphs) {
		this.graphs = graphs;
	}

	public Rankers getRankers() {
		return rankers;
	}

	public void setRankers(Rankers rankers) {
		this.rankers = rankers;
	}

	public Libs getLibs() {
		return libs;
	}

	public void setLibs(Libs libs) {
		this.libs = libs;
	}

	public RelatednessFunctions getRelatednessFunctions() {
		return relatednessFunctions;
	}

	public void setRelatednessFunctions(
			RelatednessFunctions relatednessFunctions) {
		this.relatednessFunctions = relatednessFunctions;
	}

	public Disambiguators getDisambiguators() {
		if (disambiguators == null) {
			Disambiguators dis = new Disambiguators();
			dis.setDisambiguators(Collections.EMPTY_LIST);
			return dis;
		}
		return disambiguators;
	}

	public void setDisambiguators(Disambiguators disambiguators) {
		this.disambiguators = disambiguators;
	}

	private DexterParamsXMLParser() {

	}

	public static class SpotRepository {

		public String dir;
		public String plainSpots;
		public String perfectHash;
		public String offsets;
		public String eliasFanoOffsets;
		public String spotsData;
		public String entityToSpots;

		public String getDir() {
			return dir;
		}

		public String getPlainSpots() {
			return plainSpots;
		}

		public void setPlainSpots(String plainSpots) {
			this.plainSpots = plainSpots;
		}

		public void setDir(String dir) {
			this.dir = dir;
		}

		public String getPerfectHash() {
			return perfectHash;
		}

		public void setPerfectHash(String perfectHash) {
			this.perfectHash = perfectHash;
		}

		public String getOffsets() {
			return offsets;
		}

		public void setOffsets(String offsets) {
			this.offsets = offsets;
		}

		public String getEliasFanoOffsets() {
			return eliasFanoOffsets;
		}

		public void setEliasFanoOffsets(String eliasFanoOffsets) {
			this.eliasFanoOffsets = eliasFanoOffsets;
		}

		public String getSpotsData() {
			return spotsData;
		}

		public void setSpotsData(String spotsData) {
			this.spotsData = spotsData;
		}

		public String getEntityToSpots() {
			return entityToSpots;
		}

		public void setEntityToSpots(String entityToSpots) {
			this.entityToSpots = entityToSpots;
		}

	}

	public static class Caches {
		List<Cache> caches = new ArrayList<Cache>();

		public List<Cache> getCaches() {
			return caches;
		}

		public void setCaches(List<Cache> caches) {
			this.caches = caches;
		}

	}

	public static class Cache {
		String name;
		int size;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getSize() {
			return size;
		}

		public void setSize(int size) {
			this.size = size;
		}

	}

	public static class Models {
		List<Model> models = new ArrayList<Model>();
		String defaultModel;

		public String getDefaultModel() {
			return defaultModel;
		}

		public void setDefaultModel(String defaultModel) {
			this.defaultModel = defaultModel;
		}

		public List<Model> getModels() {
			return models;
		}

		public void setModels(List<Model> models) {
			this.models = models;
		}

	}

	public static class Params {
		public final static Params NO_PARAM = new Params();
		public List<Param> params = new ArrayList<Param>();

		public List<Param> getParams() {
			return params;
		}

		public void setParams(List<Param> params) {
			this.params = params;
		}

	}

	public static class Param {
		public String name;
		public String value;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}

	public static class Model {

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		String name;
		String path;

	}

	public static class Labels {
		String dir;

		public String getDir() {
			return dir;
		}

		public void setDir(String dir) {
			this.dir = dir;
		}

	}

	public static class Index {
		String dir;
		String wikiIdMap;

		public String getDir() {
			return dir;
		}

		public void setDir(String dir) {
			this.dir = dir;
		}

		public String getWikiIdMap() {
			return wikiIdMap;
		}

		public void setWikiIdMap(String wikiIdMap) {
			this.wikiIdMap = wikiIdMap;
		}

	}

	public static class Thresholds {

		List<Threshold> thresholds = new ArrayList<Threshold>();

		public List<Threshold> getThresholds() {
			return thresholds;
		}

		public void setThresholds(List<Threshold> thresholds) {
			this.thresholds = thresholds;
		}

	}

	public static class Threshold {
		String name;
		float value;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public float getValue() {
			return value;
		}

		public void setValue(float value) {
			this.value = value;
		}

	}

	public static class Graphs {
		String dir;
		List<Graph> graphs = new ArrayList<Graph>();

		public String getDir() {
			return dir;
		}

		public void setDir(String dir) {
			this.dir = dir;
		}

		public List<Graph> getGraphs() {
			return graphs;
		}

		public void setGraphs(List<Graph> graphs) {
			this.graphs = graphs;
		}

	}

	public static class Graph {
		String name;
		String incoming;
		String outcoming;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getIncoming() {
			return incoming;
		}

		public void setIncoming(String incoming) {
			this.incoming = incoming;
		}

		public String getOutcoming() {
			return outcoming;
		}

		public void setOutcoming(String outcoming) {
			this.outcoming = outcoming;
		}

	}

	public static class Rankers {
		List<Ranker> rankers = new ArrayList<Ranker>();
	}

	public static class Ranker {
		String name;
		String clazz;
		Params params;
	}

	public static class Libs {
		String lib;

		public String getLib() {
			return lib;
		}

		public void setLib(String lib) {
			this.lib = lib;
		}

	}

	public static class RelatednessFunctions {
		String defaultFunction;
		List<RelatednessFunction> relatednessFunctions = new ArrayList<RelatednessFunction>();

		public String getDefaultFunction() {
			return defaultFunction;
		}

		public void setDefaultFunction(String defaultFunction) {
			this.defaultFunction = defaultFunction;
		}

		public List<RelatednessFunction> getRelatednessFunctions() {
			return relatednessFunctions;
		}

		public void setRelatednessFunctions(
				List<RelatednessFunction> relatednessFunctions) {
			this.relatednessFunctions = relatednessFunctions;
		}

	}

	public static class RelatednessFunction {
		String name;
		String clazz;
		Params params;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getClazz() {
			return clazz;
		}

		public void setClazz(String clazz) {
			this.clazz = clazz;
		}

		public Params getParams() {
			if (params == null)
				params = new Params();
			return params;
		}

		public void setParams(Params params) {
			this.params = params;
		}

	}

	public static class Disambiguators {
		String defaultDisambiguator;
		List<Disambiguator> disambiguators = new ArrayList<Disambiguator>();

		public List<Disambiguator> getDisambiguators() {
			if (disambiguators == null) {
				disambiguators = Collections.emptyList();
			}
			return disambiguators;
		}

		public void setDisambiguators(List<Disambiguator> disambiguators) {
			this.disambiguators = disambiguators;
		}

		public String getDefaultDisambiguator() {
			return defaultDisambiguator;
		}

		public void setDefaultDisambiguator(String defaultDisambiguator) {
			this.defaultDisambiguator = defaultDisambiguator;
		}

	}

	public static class Spotters {
		String defaultSpotter;
		List<Spotter> spotters = new ArrayList<Spotter>();

		public List<Spotter> getSpotters() {
			return spotters;
		}

		public void setSpotters(List<Spotter> spotters) {
			this.spotters = spotters;
		}

		public String getDefaultSpotter() {
			return defaultSpotter;
		}

	}

	public static class Spotter {
		String name;
		String clazz;
		List<Filter> filters = new ArrayList<Filter>();

		Params params = Params.NO_PARAM;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getClazz() {
			return clazz;
		}

		public void setClazz(String clazz) {
			this.clazz = clazz;
		}

		public Params getParams() {
			return params;
		}

		public void setParams(Params params) {
			this.params = params;
		}

		public List<Filter> getFilters() {
			return filters;
		}

		public void setFilters(List<Filter> filters) {
			this.filters = filters;
		}

	}

	public static class Filter {
		String name;

		public String getName() {
			return name.trim();
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	public static class SpotFilters {

		List<SpotFilter> spotFilters = new ArrayList<SpotFilter>();

		public List<SpotFilter> getSpotFilters() {
			return spotFilters;
		}

		public void setSpotFilters(List<SpotFilter> filters) {
			this.spotFilters = filters;
		}

	}

	public static class SpotFilter {
		String name;
		String clazz;
		Params params = Params.NO_PARAM;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getClazz() {
			return clazz;
		}

		public void setClazz(String clazz) {
			this.clazz = clazz;
		}

		public Params getParams() {
			return params;
		}

		public void setParams(Params params) {
			this.params = params;
		}

	}

	public static class Disambiguator {
		String name;
		String clazz;
		Params params;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getClazz() {
			return clazz;
		}

		public void setClazz(String clazz) {
			this.clazz = clazz;
		}

		public Params getParams() {
			if (params == null)
				params = new Params();
			return params;
		}

		public void setParams(Params params) {
			this.params = params;
		}

	}

	public static class Taggers {
		String defaultTagger;
		List<Tagger> taggers = new ArrayList<Tagger>();

		public List<Tagger> getTaggers() {
			return taggers;
		}

		public void setTaggers(List<Tagger> taggers) {
			this.taggers = taggers;
		}

		public String getDefaultTagger() {
			return defaultTagger;
		}

	}

	public static class Tagger {
		String name;
		String spotter;
		String disambiguator;
		String relatedness;
		Params params;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getSpotter() {
			return spotter;
		}

		public void setSpotter(String spotter) {
			this.spotter = spotter;
		}

		public String getDisambiguator() {
			return disambiguator;
		}

		public void setDisambiguator(String disambiguator) {
			this.disambiguator = disambiguator;
		}

		public String getRelatedness() {
			return relatedness;
		}

		public void setRelatedness(String relatedness) {
			this.relatedness = relatedness;
		}

		public Params getParams() {
			if (params == null)
				params = new Params();
			return params;
		}

		public void setParams(Params params) {
			this.params = params;
		}

	}

	public static DexterParamsXMLParser load(String xmlConfig) {
		logger.info("loading configuration from {} ", xmlConfig);
		XStream xstream = new XStream(new StaxDriver());
		xstream.alias("config", DexterParamsXMLParser.class);
		xstream.alias("model", Model.class);
		xstream.alias("labels", Labels.class);
		xstream.alias("index", Index.class);

		xstream.alias("thresholds", Thresholds.class);

		xstream.addImplicitCollection(Thresholds.class, "thresholds");
		xstream.alias("threshold", Threshold.class);
		xstream.alias("spotFilter", SpotFilter.class);
		xstream.alias("spotFilters", SpotFilters.class);

		xstream.addImplicitCollection(SpotFilters.class, "spotFilters");
		xstream.aliasField("class", SpotFilter.class, "clazz");

		xstream.alias("graph", Graph.class);
		xstream.alias("graphs", Graphs.class);
		xstream.addImplicitCollection(Graphs.class, "graphs");

		xstream.alias("rankers", Rankers.class);
		xstream.addImplicitCollection(Models.class, "models");
		xstream.aliasField("default", Models.class, "defaultModel");

		xstream.aliasField("default", Disambiguators.class,
				"defaultDisambiguator");

		xstream.aliasField("default", Spotters.class, "defaultSpotter");

		xstream.aliasField("default", Taggers.class, "defaultTagger");

		xstream.addImplicitCollection(Rankers.class, "rankers");
		xstream.alias("ranker", Ranker.class);
		xstream.aliasField("class", Ranker.class, "clazz");

		xstream.alias("params", Params.class);
		xstream.alias("param", Param.class);

		xstream.addImplicitCollection(Params.class, "params");

		xstream.alias("libs", Libs.class);

		xstream.addImplicitCollection(RelatednessFunctions.class,
				"relatednessFunctions");
		xstream.aliasField("default", RelatednessFunctions.class,
				"defaultFunction");

		xstream.alias("relatednessFunctions", RelatednessFunctions.class);
		xstream.alias("relatednessFunction", RelatednessFunction.class);

		xstream.addImplicitCollection(Caches.class, "caches");
		xstream.alias("caches", Caches.class);
		xstream.alias("cache", Cache.class);

		xstream.alias("disambiguators", Disambiguators.class);
		xstream.addImplicitCollection(Disambiguators.class, "disambiguators");
		xstream.alias("disambiguator", Disambiguator.class);

		xstream.alias("spotters", Spotters.class);
		xstream.addImplicitCollection(Spotters.class, "spotters");
		xstream.alias("spotter", Spotter.class);

		xstream.alias("filter", Filter.class);
		xstream.addImplicitCollection(Filter.class, "filters");

		xstream.aliasField("class", RelatednessFunction.class, "clazz");
		xstream.aliasField("class", Disambiguator.class, "clazz");
		xstream.aliasField("class", Spotter.class, "clazz");

		xstream.alias("taggers", Taggers.class);
		xstream.addImplicitCollection(Taggers.class, "taggers");
		xstream.alias("tagger", Tagger.class);

		xstream.alias("spotRepository", SpotRepository.class);

		String xml = IOUtils.getFileAsString(xmlConfig);
		DexterParamsXMLParser config = (DexterParamsXMLParser) xstream
				.fromXML(xml);
		return config;
	}

	public Taggers getTaggers() {
		return taggers;
	}

	public void setTaggers(Taggers taggers) {
		this.taggers = taggers;
	}

	public static void main(String[] args) {
		DexterParamsXMLParser config = load("dexter-conf.xml");
		Gson gson = new Gson();
		System.out.println(gson.toJson(config));
	}

}
