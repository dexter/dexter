/**
 *  Copyright 2013 Diego Ceccarelli
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
package it.cnr.isti.hpc.dexter.milnewitten;

import it.cnr.isti.hpc.dexter.entity.EntityMatch;
import it.cnr.isti.hpc.dexter.entity.EntityMatchList;
import it.cnr.isti.hpc.dexter.relatedness.RelatednessFactory;
import it.cnr.isti.hpc.dexter.spot.SpotMatch;
import it.cnr.isti.hpc.dexter.spot.SpotMatchList;
import it.cnr.isti.hpc.property.ProjectProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Apr 30, 2013
 */
public class Context {

	private static final Logger logger = LoggerFactory.getLogger(Context.class);

	int contextMaxSize;

	static ProjectProperties properties = new ProjectProperties(Context.class);

	float priorProbabilityThreshold;
	EntityMatchList contextEntities;
	List<SpotMatch> ambiguousSpots;
	EntityMatchList notAmbiguousEntities;
	float totalWeight;
	RelatednessFactory relatedness;

	public Context(SpotMatchList sml) {
		contextMaxSize = 20;
		String threshold = properties.get("prior.threshold");
		if (threshold == null) {
			logger.error("no value set for prior threshold");
			System.exit(-1);
		}
		priorProbabilityThreshold = Float.parseFloat(threshold);
		contextEntities = new EntityMatchList();
		ambiguousSpots = new ArrayList<SpotMatch>();
		notAmbiguousEntities = new EntityMatchList();
		List<EntityMatch> entities = new ArrayList<EntityMatch>();

		relatedness = new RelatednessFactory();

		totalWeight = 0;

		for (SpotMatch sm : sml) {
			entities.clear();

			for (EntityMatch e : sm.getEntities()) {
				double prior = e.getPriorProbability();
				// System.out.println("e "+e.getId()+ " prior   face" +prior);
				if (prior < priorProbabilityThreshold)
					continue;
				entities.add(e);
				if (entities.size() > 1) {
					ambiguousSpots.add(sm);
					break;
				}
			}

			if (entities.size() == 1) {
				EntityMatch e = entities.get(0);

				// if we have the same entity for more than one one spot,
				// we add only the entity with max spot probability.
				int pos = contextEntities.entityIndex(e.getId());
				if (pos >= 0) {
					EntityMatch sameEntity = contextEntities.get(pos);
					if (e.getSpotLinkProbability() > sameEntity
							.getSpotLinkProbability()) {
						contextEntities.remove(pos);
						contextEntities.add(e);
					}
				} else {
					// first time we see this entity
					contextEntities.add(e);
				}

			}
		}

		TreeSet<EntityMatch> sortedEntities = new TreeSet<EntityMatch>();

		for (EntityMatch e : contextEntities) {
			double spotProbability = e.getSpotLinkProbability();
			double avgRelatedness = 0;
			// if (relatedness.hasNegativeScores()){
			// for (EntityMatch e1 : contextEntities){
			// double rel = relatedness.getScore(e1.getId(), e.getId());
			// min = Math.min(rel, min);
			// max = Math.max(rel, max);
			//
			// }
			// min = Math.abs(min);
			// }
			for (EntityMatch e1 : contextEntities) {
				double rel = relatedness.getScore(e1.getId(), e.getId());

				avgRelatedness += rel;
			}
			avgRelatedness = avgRelatedness / (contextEntities.size());

			double weight = (spotProbability + avgRelatedness + avgRelatedness) / 3;
//			System.out.println("ECON \t" + e.getId() + "\t" + avgRelatedness
//					+ "\t" + spotProbability + "\t" + weight);
			e.setScore(weight);
			sortedEntities.add(e);
			notAmbiguousEntities.add(e);
			// logger.info("add {} weight \t {}",e.getId(), e.getScore());

		}
		contextEntities.clear();
		int s = Math.min(sortedEntities.size(), contextMaxSize);
		//System.out.print("CONTEXT ");
		for (int i = 0; i < s; i++) {
			EntityMatch e = sortedEntities.pollFirst();
			contextEntities.add(e);
			System.out.print(e.getId() + " ");
			totalWeight += e.getScore();

			// logger.info("context = {} weight \t {} ",e.getId(),
			// e.getScore());
		}
		System.out.println("");

	}

	/**
	 * @return the quality (size and homogeneity) of the available context.
	 */
	public float getQuality() {
		return totalWeight;
	}

	public double getRelatednessTo(EntityMatch e) {

		if (totalWeight == 0)
			return 0;

		double rel = 0;
		// if (relatedness.hasNegativeScores()){
		// for (EntityMatch sm: contextEntities) {
		// double r = relatedness.getScore(sm.getId(), e.getId());
		// max = Math.max(r, max);
		// min = Math.min(r, min);
		//
		// }
		// }
		for (EntityMatch sm : contextEntities) {

			double r = relatedness.getScore(sm.getId(), e.getId());

			r = r * sm.getScore();
			rel += r;
		}

		return rel / totalWeight;
	}

	/**
	 * @return the contextEntities
	 */
	public EntityMatchList getContextEntities() {
		return contextEntities;
	}

	/**
	 * @return the notAmbiguousEntities
	 */
	public EntityMatchList getNotAmbiguousEntities() {
		return notAmbiguousEntities;
	}

	/**
	 * @param notAmbiguousEntities
	 *            the notAmbiguousEntities to set
	 */
	public void setNotAmbiguousEntities(EntityMatchList notAmbiguousEntities) {
		this.notAmbiguousEntities = notAmbiguousEntities;
	}

	/**
	 * @param contextEntities
	 *            the contextEntities to set
	 */
	public void setContextEntities(EntityMatchList contextEntities) {
		this.contextEntities = contextEntities;
	}

	/**
	 * @return the ambiguousSpots
	 */
	public List<SpotMatch> getAmbiguousSpots() {
		return ambiguousSpots;
	}

	/**
	 * @param ambiguousSpots
	 *            the ambiguousSpots to set
	 */
	public void setAmbiguousSpots(List<SpotMatch> ambiguousSpots) {
		this.ambiguousSpots = ambiguousSpots;
	}

	/**
	 * @return the totalWeight
	 */
	public float getTotalWeight() {
		return totalWeight;
	}

	/**
	 * @param totalWeight
	 *            the totalWeight to set
	 */
	public void setTotalWeight(float totalWeight) {
		this.totalWeight = totalWeight;
	}

	/**
	 * @return the priorProbabilityThreshold
	 */
	public float getPriorProbabilityThreshold() {
		return priorProbabilityThreshold;
	}

	/**
	 * @param priorProbabilityThreshold
	 *            the priorProbabilityThreshold to set
	 */
	public void setPriorProbabilityThreshold(float priorProbabilityThreshold) {
		this.priorProbabilityThreshold = priorProbabilityThreshold;
	}

}
