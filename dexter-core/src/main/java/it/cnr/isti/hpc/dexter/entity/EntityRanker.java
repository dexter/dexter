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
package it.cnr.isti.hpc.dexter.entity;

import java.util.List;

import it.cnr.isti.hpc.dexter.Document;
import it.cnr.isti.hpc.dexter.hash.IdHelper;
import it.cnr.isti.hpc.dexter.lucene.LuceneHelper;
import it.cnr.isti.hpc.dexter.spot.ContextExtractor;
import it.cnr.isti.hpc.dexter.spot.Spot;
import it.cnr.isti.hpc.property.ProjectProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EntityRanker assignes to an entity a score based on the similarity (tf-idf)
 * between a text window around the spot and the wikipedia article related to
 * the entity.
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 06/ago/2012
 */
public class EntityRanker {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(EntityRanker.class);

	Document document;
	ContextExtractor context;
	static ProjectProperties properties = new ProjectProperties(
			EntityRanker.class);
	static LuceneHelper helper = LuceneHelper.getDexterLuceneHelper();
	private static final boolean RANK_BY_SIMILARITY = properties.get(
			"rank.by.similarity").equals("true");
	private static final boolean RANK_BY_PRIOR = properties
			.get("rank.by.prior").equals("true");
	
	private static  float prior_threshold;

	private static final int WINDOW_SIZE = properties
			.getInt("context.window.size");

	public EntityRanker(Document document) {
		prior_threshold = Float.parseFloat(properties.get("prior.threshold"));
		if (RANK_BY_SIMILARITY) {
			logger.info("(e|s) using cosine similarity");
		} else {
			if (RANK_BY_PRIOR) {
				
				logger.info("(e|s) using prior probability");
			} else
				logger.info("(e|s) NO PROBABILITY");
		}
		context = new ContextExtractor(document);
		context.setWindowSize(WINDOW_SIZE);
		this.document = document;
	}
	
	
	private EntityMatchList filterEntitiesByPrior(Spot s){
		EntityMatchList eml = new EntityMatchList();
		for (Entity e : s.getEntities()) {
			if (e.id() == IdHelper.NOID)
				continue;
			if (s.getEntityCommonness(e) < prior_threshold){
				logger.debug("filter {} by commonness",e.id());
				continue;
			}
			eml.add(new EntityMatch(e, 1, s));
		}
		
		return eml;
		
		
	}

	public EntityMatchList rank(Spot spot) {
		EntityMatchList eml = filterEntitiesByPrior(spot);
		// can't happen that prior and similarity are both true
		assert(!RANK_BY_PRIOR || !RANK_BY_SIMILARITY);
		if (RANK_BY_PRIOR) {
			
			for (EntityMatch e : eml) {
				e.setScore(spot.getEntityCommonness(e.getEntity()));
			}
			
		}
		if (RANK_BY_SIMILARITY){
			String c = context.getContext(spot.getText());
			logger.debug("context spot {} = {}", spot.getText(), c);
			 helper.rankBySimilarity(spot, eml, c);
		}
		return eml;
	}

}
