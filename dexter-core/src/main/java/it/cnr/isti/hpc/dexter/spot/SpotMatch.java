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

import it.cnr.isti.hpc.dexter.Field;
import it.cnr.isti.hpc.dexter.entity.Entity;
import it.cnr.isti.hpc.dexter.entity.EntityMatch;
import it.cnr.isti.hpc.dexter.entity.EntityMatchList;

/**
 * SpotMatch contains all the additional informations regarding a spot matched
 * in a particular position of the text. <br>
 * In particular, such object contains:
 * <ul>
 * <li>a reference to the original spot</li>
 * <li>a list of candidates entities, with relevance scores possibly affected by
 * the position and the context of the match</li>
 * <li>the document {@link Field field} where the spot was matched</li>
 * <li>the position in the field where the match starts</li>
 * <li>the position in the field where the match stops</li>
 * 
 * 
 * 
 * @see EntityMatchList
 * @see Field
 * @see Spot
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it
 */
public class SpotMatch implements Comparable<SpotMatch> {

	protected Spot spot;
	protected EntityMatchList entities;
	protected Field field;

	private int start;
	private int end;

	public SpotMatch(Spot spot) {
		this.spot = spot;
	}

	// public void incrementOccurrences() {
	// occurrences++;
	// }

	public SpotMatch(Spot spot, EntityMatchList entities) {
		this(spot);
		this.entities = entities;
	}

	public SpotMatch(Spot spot, List<Entity> entities) {
		this(spot);
		this.entities = new EntityMatchList();
		for (Entity e : entities) {
			this.entities.add(new EntityMatch(e.clone(), spot
					.getEntityCommonness(e), this));
		}

	}

	// public double getImportance() {
	// return spot.getIdf() * occurrences;
	// }

	public SpotMatch(Spot s, Field field) {
		this(s);
		this.field = field;
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

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
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

	public String getMention() {
		return spot.getMention();
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

	// public String toString() {
	// return spot.toString() + " occ: " + occurrences;
	// }

	
	/**
	 * Returns probability to be a link to a entity for the text of this spot, 
	 * it is computed dividing the number of documents in Wikipedia containing 
	 * this spot as a anchor by the number of documents in wikipedia containing
	 * this spot as simple text.
	 * 
	 * @returns the link probability
	 */
	public double getProbability() {
		return spot.getLinkProbability();
	}

	/**
	 * Returns true if this spot and the given spots overlaps in the annotated
	 * text, e.g.,
	 * <code> "neruda pablo picasso" -> 'neruda pablo' 'pablo picasso'
	 </code>.
	 * 
	 * @param s
	 *            - The spot to check
	 * @return
	 */
	public boolean overlaps(SpotMatch s) {
		boolean startOverlap = ((s.getStart() >= this.getStart()) && (s
				.getStart() <= this.getEnd()));
		if (startOverlap)
			return true;
		boolean endOverlap = ((s.getEnd() >= this.getStart()) && (s.getEnd() <= this
				.getEnd()));
		return endOverlap;
	}

	public double getEntityCommonness(Entity entity) {
		return spot.getEntityCommonness(entity);
	}

	public int getFrequency() {
		return spot.getFrequency();
	}
	
	public int getLinkFrequency(){
		return spot.getLink();
	}

	public double getLinkProbability() {
		return spot.getLinkProbability();
	}

}
