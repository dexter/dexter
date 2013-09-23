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

import java.util.List;

import it.cnr.isti.hpc.dexter.entity.Entity;
import it.cnr.isti.hpc.dexter.entity.EntityMatch;
import it.cnr.isti.hpc.dexter.entity.EntityMatchList;

/**
 * SpotMatch contains the entities matched for a spot ( @see EntityMatchList )
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it
 */
public class SpotMatch implements Comparable<SpotMatch> {

	protected Spot spot;
	protected EntityMatchList entities;
	protected int occurrences = 1;

	public SpotMatch(Spot spot) {
		this.spot = spot;
	}

	public void incrementOccurrences() {
		occurrences++;
	}

	public SpotMatch(Spot spot, EntityMatchList entities) {
		this(spot);
		this.entities = entities;
	}

	/**
	 * @param s
	 * @param entities2
	 */
	public SpotMatch(Spot spot, List<Entity> entities) {
		this(spot);
		this.entities = new EntityMatchList();
		for (Entity e : entities) {
			this.entities.add(new EntityMatch(e.clone(), spot
					.getEntityCommonness(e), spot));
		}

	}

	public double getImportance() {
		return spot.getIdf() * occurrences;
	}

	public void setProbability(double probability) {
		spot.setLinkProbability(probability);
	}

	public int compareTo(SpotMatch m) {
		if (spot.getIdf() > m.spot.getIdf())
			return 1;
		if (spot.getIdf() < m.spot.getIdf())
			return -1;
		return 1;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SpotMatch other = (SpotMatch) obj;
		if (spot == null) {
			if (other.spot != null)
				return false;
		} else if (!spot.equals(other.spot))
			return false;
		return true;
	}

	public EntityMatchList getEntities() {
		return entities;
	}

	public Spot getSpot() {
		return spot;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((spot == null) ? 0 : spot.hashCode());
		return result;
	}

	public void setEntities(EntityMatchList entities) {
		this.entities = entities;
	}

	// @Override
	// public int compareTo(Match m) {
	// if (spot.getSpotProbability() > m.spot.getSpotProbability()) return 1;
	// if (spot.getSpotProbability() < m.spot.getSpotProbability()) return -1;
	// return 1;
	// }

	public void setSpot(Spot spot) {
		this.spot = spot;
	}

	public String toString() {
		return spot.toString() + " occ: " + occurrences;
	}

	public double getProbability() {
		return spot.getLinkProbability();
	}

}
