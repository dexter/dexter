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
package it.cnr.isti.hpc.dexter.spotter;

import it.cnr.isti.hpc.dexter.document.Document;
import it.cnr.isti.hpc.dexter.document.Field;
import it.cnr.isti.hpc.dexter.entity.EntityMatchList;
import it.cnr.isti.hpc.dexter.entity.EntityRanker;
import it.cnr.isti.hpc.dexter.shingle.Shingle;
import it.cnr.isti.hpc.dexter.shingle.ShingleExtractor;
import it.cnr.isti.hpc.dexter.spot.Spot;
import it.cnr.isti.hpc.dexter.spot.SpotMatch;
import it.cnr.isti.hpc.dexter.spot.SpotMatchList;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.filter.ProbabilityFilter;
import it.cnr.isti.hpc.dexter.spot.repo.SpotRepository;
import it.cnr.isti.hpc.dexter.spot.repo.SpotRepositoryFactory;
import it.cnr.isti.hpc.dexter.util.DexterLocalParams;
import it.cnr.isti.hpc.dexter.util.DexterParams;
import it.cnr.isti.hpc.structure.LRUCache;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Spotter
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 01/ago/2012
 */
public class DictionarySpotter extends AbstractSpotter implements Spotter {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(DictionarySpotter.class);

	private static LRUCache<String, Spot> cache;

	DexterParams params = DexterParams.getInstance();

	SpotRepository spotRepo;
	private final boolean usePriorProbability = false;

	public DictionarySpotter() {
		int cachesize = params.getCacheSize("spotter");
		cache = new LRUCache<String, Spot>(cachesize);
		SpotRepositoryFactory factory = new SpotRepositoryFactory();
		spotRepo = factory.getStdInstance();
	}

	@Override
	public SpotMatchList match(DexterLocalParams localParams, Document document) {
		ProbabilityFilter filter = new ProbabilityFilter();
		SpotMatchList matches = new SpotMatchList();

		Iterator<Field> fields = document.getFields();
		while (fields.hasNext()) {

			Field field = fields.next();
			EntityRanker er = new EntityRanker(field);
			ShingleExtractor shingler = new ShingleExtractor(field.getValue());
			Spot s;
			String text;
			for (Shingle shingle : shingler) {
				logger.debug("SHINGLE: [{}] ", shingle);
				text = shingle.getText();
				if (cache.containsKey(text)) {
					// hit in cache
					s = cache.get(text);
					if (s != null) {
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

				// s.setStart(shingle.getStart());
				// s.setEnd(shingle.getEnd());

				if (filter.isFilter(s)) {
					logger.info("ignoring spot {}, probability too low {}",
							s.getMention(), s.getLinkProbability());
					continue;
				}

				// int pos = matches.index(s);
				// if (pos >= 0) {
				// // the spot is yet in the list, increment its occurrences
				// matches.get(pos).incrementOccurrences();
				// continue;
				// }
				SpotMatch match = new SpotMatch(s, field);
				logger.debug("adding {} to matchset ", s);

				EntityMatchList entities = er.rank(match);
				match.setEntities(entities);
				match.setStart(shingle.getStart());
				match.setEnd(shingle.getEnd());
				matches.add(match);

			}
		}

		return matches;
	}

	@Override
	public void init(DexterParams dexterParams,
			DexterLocalParams defaultModuleParams) {

	}

}
