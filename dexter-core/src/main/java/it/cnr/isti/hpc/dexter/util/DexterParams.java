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
public class DexterParams {
	private static final Logger logger = LoggerFactory
			.getLogger(DexterParams.class);

	private Models models;
	private Labels labels;
	private Index index;
	private Threshold threshold;
	private Graphs graphs;
	private Rankers rankers;
	private Spots spots;
	private Libs libs;
	private RelatednessFunctions relatednessFunctions;
	private Disambiguators disambiguators;

	private DexterParams() {

	}

	private static class Models {
		List<Model> models = new ArrayList<Model>();

	}

	private static class Model {

		String name;
		String path;

	}

	private static class Labels {
		String dir;
	}

	private static class Index {
		String dir;
		String wikiIdMap;
	}

	private static class Threshold {
		float commonness;
		float linkprobability;
	}

	private static class Graphs {
		String dir;
		List<Graph> graphs = new ArrayList<Graph>();

	}

	private static class Graph {
		String name;
		String incoming;
		String outcoming;

	}

	private static class EntityEntity {
		String incoming;
		String outcoming;
	}

	private static class EntityCategory {
		String incoming;
		String outcoming;
	}

	private static class CategoryCategory {
		String incoming;
		String outcoming;
	}

	private static class Rankers {
		List<Ranker> rankers = new ArrayList<Ranker>();
	}

	private static class Ranker {
		String name;
		String clazz;
	}

	private static class Spots {
		String dir;
	}

	private static class Libs {
		String lib;
	}

	private static class RelatednessFunctions {
		List<RelatednessFunction> relatednessFunctions = new ArrayList<RelatednessFunction>();
	}

	private static class RelatednessFunction {
		String name;
		String clazz;
	}

	private static class Disambiguators {
		List<Disambiguator> disambiguators = new ArrayList<Disambiguator>();
	}

	private static class Spotters {
		List<Spotter> spotters = new ArrayList<Spotter>();
	}

	private static class Spotter {
		String name;
		String clazz;
	}

	private static class Disambiguator {
		String name;
		String clazz;
	}

	private static class Taggers {
		List<Tagger> taggers = new ArrayList<Tagger>();
	}

	private static class Tagger {
		String name;
		String spotter;
		String disambiguator;
		String relatedness;
	}

	public static DexterParams load(String xmlConfig) {
		logger.info("loading configuration from {} ", xmlConfig);
		XStream xstream = new XStream(new StaxDriver());
		xstream.alias("config", DexterParams.class);
		xstream.alias("model", Model.class);
		xstream.alias("labels", Labels.class);
		xstream.alias("index", Index.class);
		xstream.alias("threshold", Threshold.class);
		xstream.alias("graph", Graph.class);
		xstream.alias("graphs", Graphs.class);
		xstream.addImplicitCollection(Graphs.class, "graphs");

		xstream.alias("rankers", Rankers.class);
		xstream.addImplicitCollection(Models.class, "models");
		xstream.addImplicitCollection(Rankers.class, "rankers");
		xstream.alias("ranker", Ranker.class);
		xstream.aliasField("class", Ranker.class, "clazz");

		xstream.alias("entityEntity", EntityEntity.class);
		xstream.alias("entityCategory", EntityCategory.class);

		xstream.alias("categoryCategory", CategoryCategory.class);
		xstream.alias("spots", Spots.class);
		xstream.alias("libs", Libs.class);

		xstream.addImplicitCollection(RelatednessFunctions.class,
				"relatednessFunctions");
		xstream.alias("relatednessFunctions", RelatednessFunctions.class);
		xstream.alias("relatednessFunction", RelatednessFunction.class);

		xstream.alias("disambiguators", Disambiguators.class);
		xstream.addImplicitCollection(Disambiguators.class, "disambiguators");
		xstream.alias("disambiguator", Disambiguator.class);

		xstream.alias("spotters", Spotters.class);
		xstream.addImplicitCollection(Spotters.class, "spotters");
		xstream.alias("spotter", Spotter.class);

		xstream.aliasField("class", RelatednessFunction.class, "clazz");
		xstream.aliasField("class", Disambiguator.class, "clazz");
		xstream.aliasField("class", Spotter.class, "clazz");

		xstream.alias("taggers", Taggers.class);
		xstream.addImplicitCollection(Taggers.class, "taggers");
		xstream.alias("tagger", Tagger.class);

		String xml = IOUtils.getFileAsString("dexter-conf.xml");
		DexterParams config = (DexterParams) xstream.fromXML(xml);
		return config;
	}

	public static void main(String[] args) {
		DexterParams config = load("dexter-conf.xml");
		Gson gson = new Gson();
		System.out.println(gson.toJson(config));
	}
}
