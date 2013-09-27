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

import it.cnr.isti.hpc.dexter.spot.SpotMatch;

import java.util.Comparator;

/**
 * EntityMatch contains the confidence score of an entity associated to a spot
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 06/ago/2012
 */
public class EntityMatch implements Comparable<EntityMatch> {
	private Entity entity;
	
	private SpotMatch spot;
	

	private double score;
	
	private EntityMatch(Entity e,  double score) {
		super();
		this.entity = new Entity(e.getId(), e.getFrequency());
		this.score = score;
	}

	private EntityMatch(int id, double score) {
		super();
		this.entity = new Entity(id);
		this.score = score;
	}
	
	public EntityMatch(Entity e, double score, SpotMatch spot) {
		this(e, score);
		this.spot = spot;
	}
	
	
	public EntityMatch(int id, double score, SpotMatch spot) {
		this(id, score);
		this.spot = spot;
	}
	
	public double getPriorProbability(){
		return spot.getEntityCommonness(entity);
	}

	public int compareTo(EntityMatch em) {
		if (score > em.getScore())
			return -1;
		if (score < em.getScore())
			return 1;
		return 0;
	}

	public Entity getEntity() {
		return entity;
	}

	public int getId() {
		return entity.getId();
	}

	public double getScore() {
		return score;
	}

	public SpotMatch getSpot() {
		return spot;
	}
	
	public int getStart(){
		return spot.getStart();
	}
	
	public int getEnd(){
		return spot.getEnd();
	}
	
	public String getMention(){
		return spot.getMention();
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}
	
	 public static class SortByPosition implements Comparator<EntityMatch> {

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		
		public int compare(EntityMatch em, EntityMatch em1) {
			return em.getStart() - em1.getStart();
		}
	 }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entity == null) ? 0 : entity.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EntityMatch other = (EntityMatch) obj;
		if (entity == null) {
			if (other.entity != null)
				return false;
		} else if (!entity.equals(other.entity))
			return false;
		return true;
	}

	public void setId(int id) {
		this.entity.setId(id);
	}

	public void setScore(double score) {
		this.score = score;
	}

	public void setSpot(SpotMatch spot) {
		this.spot = spot;
	}

	public String toString() {
		String str = spot.getMention() + "[" + getStart() + ","
				+ getEnd() + "]" +"\t score: "+score+"\t"+"prior:"+getPriorProbability()+"\n" + entity.toString() + "\n";
		return str;
	}

	public String toEntityString() {
//		String str = entity.getName() + "\t" + spot.getSpot() + "["
//				+ spot.getStart() + "," + spot.getEnd() + "]" + "\t"
//				+ entity.toString() + "\n";
		return "";
	}

	public int getFrequency() {
		return spot.getFrequency();
	}

	public double getSpotLinkProbability() {
		return spot.getLinkProbability();
	}

	public static class SpotLengthComparator implements Comparator<EntityMatch> {
		public int compare(EntityMatch em1, EntityMatch em2) {
			return em1.getSpot().getMention().length() - em2.getSpot().getMention().length();
		}
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
	public boolean overlaps(EntityMatch s) {
		boolean startOverlap = ((s.getStart() >= this.getStart()) && (s
				.getStart() <= this.getEnd()));
		if (startOverlap)
			return true;
		boolean endOverlap = ((s.getEnd() >= this.getStart()) && (s.getEnd() <= this
				.getEnd()));
		return endOverlap;
	}

	
}
