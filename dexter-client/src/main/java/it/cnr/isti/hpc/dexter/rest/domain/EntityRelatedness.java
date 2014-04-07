/**
 *  Copyright 2014 Diego Ceccarelli
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
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Apr 7, 2014
 */
public class EntityRelatedness {

	int entity1;
	int entity2;

	String entity1Wikiname;
	String entity2Wikiname;

	double relatedness;
	String type;

	public EntityRelatedness(int entity1, int entity2, String type) {
		super();
		this.entity1 = entity1;
		this.entity2 = entity2;
		this.type = type;
	}

	public int getEntity1() {
		return entity1;
	}

	public void setEntity1(int entity1) {
		this.entity1 = entity1;
	}

	public int getEntity2() {
		return entity2;
	}

	public void setEntity2(int entity2) {
		this.entity2 = entity2;
	}

	public String getEntity1Wikiname() {
		return entity1Wikiname;
	}

	public void setEntity1Wikiname(String entity1Wikiname) {
		this.entity1Wikiname = entity1Wikiname;
	}

	public String getEntity2Wikiname() {
		return entity2Wikiname;
	}

	public void setEntity2Wikiname(String entity2Wikiname) {
		this.entity2Wikiname = entity2Wikiname;
	}

	public double getRelatedness() {
		return relatedness;
	}

	public void setRelatedness(double relatedness) {
		this.relatedness = relatedness;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "EntityRelatedness [entity1=" + entity1 + ", entity2=" + entity2
				+ ", entity1Wikiname=" + entity1Wikiname + ", entity2Wikiname="
				+ entity2Wikiname + ", relatedness=" + relatedness + ", type="
				+ type + "]";
	}

}
