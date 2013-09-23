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

import it.cnr.isti.hpc.dexter.entity.EntityMatch;
import it.cnr.isti.hpc.dexter.entity.EntityMatchList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Match.java
 *
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it
 * created on 03/ago/2012
 */
public class SpotMatchList extends ArrayList<SpotMatch> {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(SpotMatchList.class);
	
	private double totalIdf = 0;
	
	@Override
	public boolean add(SpotMatch m){
		logger.debug("adding spot {} ",m.spot.getMention());
		totalIdf += m.getSpot().getIdf();
		return super.add(m);
		
	}
	
	public void normalizeSpotProbabilities(){
		double totalProbability = 0;
		for (SpotMatch s : this){
			totalProbability+=s.getProbability();
		}
		for (SpotMatch s : this){
			s.setProbability(s.getProbability()/totalProbability);
		}
	}
	
	public int index(Spot s){
		int i = 0;
		for (SpotMatch em : this){
			if (em.getSpot().equals(s)) return i;
			i++;
		}
		return -1;
		
	}
	
	
	
	
	public double getTotalIdf(){
		return totalIdf;
	}
	
	public EntityMatchList getEntities(){
		EntityMatchList eml = new EntityMatchList();
		for (SpotMatch match : this ){
			eml.addAll(match.getEntities());
		}
		return eml;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for (SpotMatch m  : this){
			sb.append(m.spot.getMention()).append("\n");
			sb.append("\t").append("entities: [ ");
			for (EntityMatch e : m.getEntities()){
				sb.append(e.toString()).append(" ");
			}
			sb.append(" ]\n");
			
		}
		return sb.toString();
	}
	
	public void sortByProbability(){
		Collections.sort(this, new ProbabilityComparator());
	}
	
	public EntityMatchList getSortedEntities(){
		this.normalizeSpotProbabilities();
		EntityMatchList eml = this.getEntities();
		eml.normalizeScores();
		Collections.sort(eml, new EntityProbabilityComparar());
		return eml;
	}
	
	
	private class ProbabilityComparator implements Comparator<SpotMatch>{

		
		public int compare(SpotMatch s1, SpotMatch s2) {
			if ( s1.getProbability() > s2.getProbability()) return -1;
			else return 1;
		}
		
	}
	
	private class EntityProbabilityComparar implements Comparator<EntityMatch> {

		
		public int compare(EntityMatch e1, EntityMatch e2) {
			
			double e1score = e1.getFrequency() * e1.getScore() * e1.getSpotProbability();
			
			double e2score = e2.getFrequency() * e2.getScore() * e2.getSpotProbability();
			if (e1score > e2score) return -1;
			else return 1;
			
		}
		
	}
	
	

}
