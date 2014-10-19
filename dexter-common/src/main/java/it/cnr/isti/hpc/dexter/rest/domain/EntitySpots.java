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

package it.cnr.isti.hpc.dexter.rest.domain;

import java.util.List;

/**
 * Represents an annotated spot.
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Oct 18, 2013
 */
public class EntitySpots {
	public int entity;
	public String wikiname;
	public List<CandidateSpot> spots;

	public EntitySpots() {

	}

	public int getEntity() {
		return entity;
	}

	public void setEntity(int entity) {
		this.entity = entity;
	}

	public String getWikiname() {
		return wikiname;
	}

	public void setWikiname(String wikiname) {
		this.wikiname = wikiname;
	}

	public List<CandidateSpot> getSpots() {
		return spots;
	}

	public void setSpots(List<CandidateSpot> spots) {
		this.spots = spots;
	}

}
