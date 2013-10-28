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

/**
 * Represents a candidate entity for a spot.
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 * Created on Oct 18, 2013
 */
public class CandidateEntity implements Comparable<CandidateEntity> {
	int entity;
	int freq;
	double commonness;
	
	
	
	public CandidateEntity(int entity, int freq, double commonness) {
		super();
		this.entity = entity;
		this.freq = freq;
		this.commonness = commonness;
	}
	public int getEntity() {
		return entity;
	}
	public void setEntity(int entity) {
		this.entity = entity;
	}
	public int getFreq() {
		return freq;
	}
	public void setFreq(int freq) {
		this.freq = freq;
	}
	public double getCommonness() {
		return commonness;
	}
	public void setCommonness(double commonness) {
		this.commonness = commonness;
	}
	@Override
	public int compareTo(CandidateEntity o) {
		if (o.getCommonness() > getCommonness()) return 1;
		if (o.getCommonness() < getCommonness()) return -1;
		return 0;
	}
	
	

}
