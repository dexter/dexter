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
package it.cnr.isti.hpc.dexter.spot;

import it.cnr.isti.hpc.dexter.Document;
import it.cnr.isti.hpc.dexter.entity.EntityRanker;
import it.cnr.isti.hpc.dexter.shingle.Shingle;
import it.cnr.isti.hpc.dexter.shingle.ShingleExtractor;
import it.cnr.isti.hpc.dexter.spot.filter.ProbabilityFilter;
import it.cnr.isti.hpc.dexter.spot.repo.SpotRepository;
import it.cnr.isti.hpc.dexter.spot.repo.SpotRepositoryFactory;
import it.cnr.isti.hpc.property.ProjectProperties;
import it.cnr.isti.hpc.structure.LRUCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Spotter.java
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 01/ago/2012
 */
public class Spotter {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(Spotter.class);
	ProbabilityFilter filter = new ProbabilityFilter();
	private static LRUCache<String, Spot> cache;
	ProjectProperties properties = new ProjectProperties(Spotter.class);
	SpotRepository spotRepo;
	private boolean usePriorProbability = false;

	public Spotter() {
		int cachesize = properties.getInt("spotter.cache.size");
		String prior = properties.get("prior.probabability");
		usePriorProbability = (prior != null && prior.equals("true"));
		cache = new LRUCache<String, Spot>(cachesize);
		SpotRepositoryFactory factory = new SpotRepositoryFactory();
		spotRepo = factory.getStdInstance();
	}

	public SpotMatchList match(Document document) {
		SpotMatchList matches = new SpotMatchList();

		EntityRanker er = new EntityRanker(document);

		ShingleExtractor shingler = new ShingleExtractor(document);
		Spot s;
		String text;
		for (Shingle shingle : shingler) {
			logger.debug("SHINGLE: [{}] ", shingle);
			text = shingle.getText();
			if (cache.containsKey(text)) {
				// hit in cache
				s = cache.get(text);
				if (s != null){
					s = s.clone();
				}
			} else {
				s = spotRepo.getSpot(text);
				cache.put(text, s);
			}

			if (s == null) {
				logger.debug("no shingle for [{}] ", shingle);
				continue;
			}

			s.setStart(shingle.getStart());
			s.setEnd(shingle.getEnd());

			if (filter.isRemove(s)) {
				logger.debug("ignoring spot {}, probability too low {}",
						s.getText(), s.getProbability());
				continue;
			}

			int pos = matches.index(s);
			if (pos >= 0) {
				// the spot is yet in the list, increment its occurrences
				matches.get(pos).incrementOccurrences();
				continue;
			}

			logger.debug("adding {} to matchset ", s);
			
			SpotMatch match = new SpotMatch(s, er.rank(s));
			matches.add(match);

		}

		
		return matches;
	}

}
