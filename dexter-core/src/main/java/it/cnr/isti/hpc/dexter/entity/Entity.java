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

import java.io.Serializable;

import it.cnr.isti.hpc.dexter.hash.IdHelper;
import it.cnr.isti.hpc.dexter.hash.IdHelperFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entity.java
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 07/ago/2012
 */
public class Entity implements Comparable<Entity>, Serializable {

	
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(Entity.class);
	
	private int id;
	
	// the frequency of the entity in a given spot (used to compute prior probability
	private int frequency = 1;	
	
	//private static IdHelper helper = IdHelperFactory.getStdIdHelper();
	
	

	public Entity(int id) {
		this.id = id;
	}
	
	public Entity(int id, int frequency) {
		this(id);
		this.frequency = frequency;
	}

	public static boolean isDisambiguation(int id) {
		return id < 0;
	}
	
	public Entity clone(){
		return new Entity(id, frequency);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Entity other = (Entity) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	public int id() {
		return id;
	}
	
	
	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}


//	public String getName() {
//		return helper.getLabel(id);
//	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return String.valueOf(id);// + "\t" + getName();
	}

	
	public int compareTo(Entity o) {
		return o.frequency - frequency;
	}
	 
	
	

}
