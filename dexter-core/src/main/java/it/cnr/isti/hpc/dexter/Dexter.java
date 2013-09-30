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
package it.cnr.isti.hpc.dexter;

import it.cnr.isti.hpc.benchmark.Stopwatch;
import it.cnr.isti.hpc.dexter.disambiguation.Disambiguator;
import it.cnr.isti.hpc.dexter.disambiguation.TopScoreEntityDisambiguator;
import it.cnr.isti.hpc.dexter.entity.EntityMatchList;
import it.cnr.isti.hpc.dexter.plugin.PluginLoader;
import it.cnr.isti.hpc.dexter.relatedness.Relatedness;
import it.cnr.isti.hpc.dexter.relatedness.RelatednessFactory;
import it.cnr.isti.hpc.dexter.spot.DictionarySpotter;
import it.cnr.isti.hpc.dexter.spot.SpotMatchList;
import it.cnr.isti.hpc.property.ProjectProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Sep 5, 2012
 */
public class Dexter implements Tagger {

	private static final Logger logger = LoggerFactory.getLogger(Dexter.class);
	private ProjectProperties properties = new ProjectProperties(Dexter.class);

	private Spotter spotter;
	private Disambiguator disambiguator;
	private Stopwatch stopwatch;

	public Dexter() {
		stopwatch = new Stopwatch();
		PluginLoader pl = new PluginLoader();
		
		if (properties.has("spotter.class")) {
			spotter = pl.getSpotter(properties.get("spotter.class"));
		} else {
			spotter = new DictionarySpotter();
		}
		if (properties.has("disambiguator.class")) {
			disambiguator = pl
					.getDisambiguator(properties.get("disambiguator.class"));
		} else {
			disambiguator = new TopScoreEntityDisambiguator();
		}
		
		if (properties.has("relatedness.class")){
			Relatedness r = pl
					.getRelatedness(properties.get("relatedness.class"));
			RelatednessFactory.register(r);
		}

	}

	

	public EntityMatchList tag(Document doc) {

		stopwatch.start("spotting");
		SpotMatchList sml = spotter.match(doc);

		logger.info("spotting performed in {} millis",
				stopwatch.stop("spotting"));
		
		stopwatch.start("disambiguation");
		EntityMatchList eml = disambiguator.disambiguate(sml);
		if (! eml.isEmpty()){
			eml = eml.removeOverlappings();
		} else {
			logger.warn("no spot identified in text");
		}
		logger.info("disambiguation performed in {} millis",
				stopwatch.stop("disambiguation"));
		return eml;

	}
	
	public String stats() {
		return stopwatch.stat();
	}

}
