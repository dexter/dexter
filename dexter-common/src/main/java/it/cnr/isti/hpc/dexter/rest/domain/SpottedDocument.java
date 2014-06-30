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

import it.cnr.isti.hpc.dexter.common.Document;
import it.cnr.isti.hpc.dexter.common.MultifieldDocument;

import java.util.List;

/**
 * Represents a document with all the spot detected during the spotting.
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Oct 18, 2013
 */
public class SpottedDocument {
	private MultifieldDocument document;
	private List<CandidateSpot> spots;
	private int nSpots;
	private float querytime;
	private Tagmeta meta;

	public SpottedDocument(MultifieldDocument document,
			List<CandidateSpot> spots, int nSpots, float querytime) {
		super();
		this.document = document;
		this.spots = spots;
		this.nSpots = nSpots;
		this.querytime = querytime;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(MultifieldDocument document) {
		this.document = document;
	}

	public List<CandidateSpot> getSpots() {
		return spots;
	}

	public void setSpots(List<CandidateSpot> spots) {
		this.spots = spots;
	}

	public int getnSpots() {
		return nSpots;
	}

	public void setnSpots(int nSpots) {
		this.nSpots = nSpots;
	}

	public float getQuerytime() {
		return querytime;
	}

	public void setQuerytime(float querytime) {
		this.querytime = querytime;
	}

	public Tagmeta getMeta() {
		return meta;
	}

	public void setMeta(Tagmeta meta) {
		this.meta = meta;
	}

	@Override
	public String toString() {
		return "SpottedDocument [document=" + document + ", spots=" + spots
				+ ", nSpots=" + nSpots + ", querytime=" + querytime + ", meta="
				+ meta + "]";
	}

}
