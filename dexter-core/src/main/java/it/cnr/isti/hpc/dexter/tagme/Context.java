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
package it.cnr.isti.hpc.dexter.tagme;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.cnr.isti.hpc.dexter.entity.EntityMatch;
import it.cnr.isti.hpc.dexter.entity.EntityMatchList;
import it.cnr.isti.hpc.dexter.relatedness.RelatednessFactory;
import it.cnr.isti.hpc.dexter.spot.SpotMatch;
import it.cnr.isti.hpc.dexter.spot.SpotMatchList;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on May 13, 2013
 */
public class Context {
	
	
	private static final Logger logger = LoggerFactory.getLogger(Context.class);

	private static RelatednessFactory relatedness = new RelatednessFactory();
	private SpotMatchList sml;
	private SpotMatchList ambiguousSpots;
	private SpotMatchList notAmbiguousSpots;
	private int window;

	public Context(SpotMatchList sml, int window) {
		this.sml = sml;
		this.window = window;
		int size = this.sml.size();
		ambiguousSpots = new SpotMatchList();
		notAmbiguousSpots = new SpotMatchList();

		for (int i = 0; i < size; i++) {
			SpotMatch a = this.sml.get(i);
			for (EntityMatch em : a.getEntities()) em.setScore(0);
			int candidates = a.getEntities().size();
			if (candidates > 1) {
				ambiguousSpots.add(a);
			} else {
				notAmbiguousSpots.add(a);
				
			}
			int ldelta = window/2;
			int rdelta = window/2;
			if (i < ldelta) {
				rdelta += ldelta-i;
				ldelta = i;
			} 
			if (rdelta+i > size){
				ldelta = ldelta+ size - rdelta - i;
				rdelta = size - i;
			}
			
			
			
			int start = Math.max(i-ldelta, 0);
			int end = Math.min(i+rdelta, size);
			
			for (int j = start; j < end; j++){
				if (i == j){
					continue;
				}
				SpotMatch b = this.sml.get(j);
				vote(b, a);
			}
		}
		// at the end of this, each entitymatch contains rel_a(em) in score
		//logger.info("ambiguous = {} / not ambiguous = {}",ambiguousSpots.size(), notAmbiguousSpots.size());
	}

	private void vote(SpotMatch b, SpotMatch a) {
		double score = 0;
		for (EntityMatch aem : a.getEntities()) {
			for (EntityMatch bem : b.getEntities()) {
				double rel = relatedness.getScore(bem.getId(), aem.getId());
				double prior = bem.getPriorProbability();
				score += rel * prior;

			}
			score /= b.getEntities().size();
			aem.setScore(aem.getScore()+score);
		}

	}
	
	 

	/**
	 * @return the ambiguousSpots
	 */
	public SpotMatchList getAmbiguousSpots() {
		return ambiguousSpots;
	}

	/**
	 * @param ambiguousSpots the ambiguousSpots to set
	 */
	public void setAmbiguousSpots(SpotMatchList ambiguousSpots) {
		this.ambiguousSpots = ambiguousSpots;
	}

	/**
	 * @return the notAmbiguousSpots
	 */
	public SpotMatchList getNotAmbiguousSpots() {
		return notAmbiguousSpots;
	}
	
	
	public EntityMatchList getNotAmbiguousEntities(){
		EntityMatchList eml = new EntityMatchList();
		for (SpotMatch sm : notAmbiguousSpots){
			EntityMatchList tmp = sm.getEntities();
			if (tmp.isEmpty()) continue;
			eml.add(sm.getEntities().get(0));
		}
		return eml;
	}
	
	public EntityMatchList getAmbiguousEntities(double epsilon){
		EntityMatchList eml = new EntityMatchList();
		for (SpotMatch sm : ambiguousSpots){
			eml.add(disambiguate(sm, epsilon));
		}
		return eml;
	}
	
	
	
	public EntityMatchList getAllEntities(double epsilon){
		EntityMatchList eml = getNotAmbiguousEntities();
		eml.addAll(getAmbiguousEntities(epsilon));
		
		return eml;
	}
	
	public EntityMatchList score(EntityMatchList eml){
		eml = eml.removeOverlappings();
		int size = eml.size();
		for (int i = 0 ; i < size ; i++){
			EntityMatch e = eml.get(i);
			int ldelta = window/2;
			int rdelta = window/2;
			if (i < ldelta) {
				rdelta += ldelta-i;
				ldelta = i;
			} 
			if (rdelta+i > size){
				ldelta = ldelta+ size - rdelta - i;
				rdelta = size - i;
			}
			double avgRel = 0;
			int start = Math.max(i-ldelta, 0);
			int end = Math.min(i+rdelta, size);
			
			for (int j = start; j < end; j++){
				if (i == j) continue;
				EntityMatch c = eml.get(j); 
				avgRel += relatedness.getScore(c.getId(), e.getId());
			}
			e.setScore(0.5*avgRel/(size-1)+0.5*e.getSpotLinkProbability());
		}
		return eml;
		
	}


	/**
	 * @param notAmbiguousSpots the notAmbiguousSpots to set
	 */
	public void setNotAmbiguousSpots(SpotMatchList notAmbiguousSpots) {
		this.notAmbiguousSpots = notAmbiguousSpots;
	}
	
	public EntityMatch disambiguate(SpotMatch sm, double epsilon){
		EntityMatchList eml = sm.getEntities();
		eml.sort();
		//System.out.println("<eml>\n"+eml);
		//System.out.println("</eml>");

		EntityMatch best = eml.get(0);
		double maxScore = best.getScore();
		double minScore = maxScore - maxScore*epsilon;
		//logger.info("maxScore {} x epsilon {} = {}", maxScore,epsilon,maxScore*epsilon	);
//
		//logger.info("maxScore = {}, minScore = {} ",maxScore, minScore);
		for (EntityMatch em : eml){
			if (em.getScore()< minScore) break;
			if (best.getPriorProbability() < em.getPriorProbability()) {
				best = em;
			}
			
			
		}
		//logger.info("best = {}",best.getId());
		

		return best;
		
	}
	
	

	
}
