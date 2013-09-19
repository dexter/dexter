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

import java.util.ArrayList;
import java.util.Collections;

/**
 * EntityMatchList.java
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 06/ago/2012
 */

public class EntityMatchList extends ArrayList<EntityMatch> {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean add(EntityMatch e) {
		return super.add(e);
	}

	public void sort() {
		Collections.sort(this);
	}

	public int entityIndex(int e) {
		int pos = 0;
		for (EntityMatch em : this) {
			if (em.getId() == e) {
				return pos;
			}
			pos++;
		}
		return -1;
	}

	public void normalizeScores() {
		double totalScore = 0;
		for (EntityMatch e : this)
			totalScore += e.getScore();
		for (EntityMatch e : this) {
			e.setScore(e.getScore() / (double) totalScore);
		}
		totalScore = 1;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		int i = 1;
		for (EntityMatch match : this) {
			sb.append(i++).append(" ").append(match.toString());
			// .append("\n");
			// sb.append(match.getExplanation());
		}
		return sb.toString();
	}

	public String toEntityString() {
		StringBuilder sb = new StringBuilder();
		int i = 1;
		for (EntityMatch match : this) {
			sb.append(i++).append(" ").append(match.toEntityString());
			// .append("\n");
			// sb.append(match.getExplanation());
		}
		return sb.toString();
	}

	public EntityMatchList removeOverlappings() {
		EntityMatchList eml = new EntityMatchList();
		for (EntityMatch e : this) {
			boolean overlaps = false;
			for (EntityMatch e1 : eml) {
				// check if e overlaps with some entity yet in the result list
				overlaps = e1.getSpot().overlaps(e.getSpot());
				if (overlaps)
					break;
			}
			if (!overlaps)
				eml.add(e);
		}
		return eml;
	}

}
