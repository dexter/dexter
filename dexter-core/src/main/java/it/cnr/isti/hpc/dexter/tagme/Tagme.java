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
package it.cnr.isti.hpc.dexter.tagme;

import it.cnr.isti.hpc.benchmark.Stopwatch;
import it.cnr.isti.hpc.dexter.Document;
import it.cnr.isti.hpc.dexter.entity.EntityMatchList;
import it.cnr.isti.hpc.dexter.spot.SpotMatchList;
import it.cnr.isti.hpc.dexter.spot.DictionarySpotter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Sep 5, 2012
 */
public class Tagme {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(Tagme.class);

	DictionarySpotter spotter;
	Stopwatch stopwatch;
	Disambiguator disambiguator;
	public Tagme() {
		stopwatch = new Stopwatch();
		spotter = new DictionarySpotter();
		 disambiguator = new Disambiguator();
	}

	public String stats() {
		return stopwatch.stat();
	}

	public EntityMatchList tag(Document doc) {


		stopwatch.start("spotting");
		SpotMatchList results = spotter.match(doc);

		logger.info("spotting performed in {} millis",
				stopwatch.stop("spotting"));
		// List<Spot> spots = new ArrayList<Spot>();
		// for (SpotMatch m : results) {
		// if (!spots.contains(m.getSpot())) {
		// spots.add(m.getSpot());
		// }
		// try {
		//
		// writer.write(m.toString());
		//
		// writer.write("\n");
		// } catch (IOException e) {
		// logger.error("writing the entities in the output file ({})",
		// e.toString());
		// System.exit(-1);
		// }
		// }
		// try {
		// writer.write("------ SPOTS -----\n");
		// writer.write(results.getEntities().toString());
		//
		// } catch (IOException e) {
		// logger.error("writing the entities in the output file ({})",
		// e.toString());
		// System.exit(-1);
		// }
		//
		// logger.info("spotted in {} millis", end - start);
		EntityMatchList eml = new EntityMatchList();
		if (!results.isEmpty()) {
			eml = disambiguator.disambiguate(results);
			
			
		} else {
			logger.warn("no spot identified in text");
		}
		return eml;

	}

}
