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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The entity is the core concept on a Entity Linking system. The entity object
 * is described by:
 * <ul>
 * <li>The id of the entity in the external knowledge base
 * <li>The frequency of the entity, i.e. how many times the entity is linked in
 * the knowledge base
 * </ul>
 * If the id of the entity is < 0, than it means it is a disambiguation page -
 * i.e. a page that have different meanings and link to each one of them.
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it
 */
public class Entity implements Comparable<Entity>, Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(Entity.class);

	private int id;
	private int frequency;

	// private static IdHelper helper = IdHelperFactory.getStdIdHelper();

	/**
	 * builds an entity with identifier <code> id </code> and frequency
	 * <code> frequency </code>
	 * 
	 * @param id
	 *            the entity integer identifier (i.e., wikiId)
	 * @param frequency
	 *            how many times the entity is linked in the knowledge base
	 */
	public Entity(int id, int frequency) {
		this.id = id;
		this.frequency = frequency;
	}

	/**
	 * builds an entity with identifier <code> id </code>.
	 * 
	 * @param id
	 *            the entity integer identifier (i.e., wikiId)
	 */
	public Entity(int id) {
		this(id, 1);
	}

	/**
	 * @return the entity integer identifier (i.e., wikiId)
	 */
	public int getId() {
		return id;
	}

	/**
	 * sets the entity integer identifier (i.e., wikiId)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @returns the frequency of the entity, i.e., how many times the entity is
	 *          linked in the knowledge base
	 */
	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	/**
	 * @returs true if the entity represent a disambiguation, false otherwise
	 */
	public static boolean isDisambiguation(int id) {
		return id < 0;
	}

	// public String getName() {
	// return helper.getLabel(id);
	// }

	@Override
	public String toString() {
		return String.valueOf(id);// + "\t" + getName();
	}

	public int compareTo(Entity o) {
		return o.frequency - frequency;
	}

	/**
	 * @return a copy of this entity
	 */
	public Entity clone() {
		return new Entity(id, frequency);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		Entity other = (Entity) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
