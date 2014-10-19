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
 * Represent a candidate spot during the entity linking process.
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Oct 18, 2013
 */
public class CandidateSpot {
	String mention;
	double linkProbability;
	String field;
	int start;
	int end;
	int linkFrequency;
	int documentFrequency;
	List<CandidateEntity> candidates;

	public CandidateSpot() {

	}

	public int getLinkFrequency() {
		return linkFrequency;
	}

	public void setLinkFrequency(int linkFrequency) {
		this.linkFrequency = linkFrequency;
	}

	public int getDocumentFrequency() {
		return documentFrequency;
	}

	public void setDocumentFrequency(int documentFrequency) {
		this.documentFrequency = documentFrequency;
	}

	public String getMention() {
		return mention;
	}

	public void setMention(String mention) {
		this.mention = mention;
	}

	public double getLinkProbability() {
		return linkProbability;
	}

	public void setLinkProbability(double linkProbability) {
		this.linkProbability = linkProbability;
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

	public List<CandidateEntity> getCandidates() {
		return candidates;
	}

	public void setCandidates(List<CandidateEntity> candidates) {
		this.candidates = candidates;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	@Override
	public String toString() {
		return "CandidateSpot [mention=" + mention + ", linkProbability="
				+ linkProbability + ", start=" + start + ", end=" + end
				+ ", linkFrequency=" + linkFrequency + ", documentFrequency="
				+ documentFrequency + ", candidates=" + candidates + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + end;
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result + start;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CandidateSpot other = (CandidateSpot) obj;
		if (end != other.end)
			return false;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.equals(other.field))
			return false;
		if (start != other.start)
			return false;
		return true;
	}

}
