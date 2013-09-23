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
package it.cnr.isti.hpc.dexter.spot.repo;

import it.cnr.isti.hpc.dexter.spot.Spot;

/**
 * SpotRepository allows to retrieve  metadata referring to a {@link Spot spot}, i.e., 
 * a piece of text referring to one or more <i>entities</i>.
 * 
 * @see Spot
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Sep 10, 2012
 */
public interface SpotRepository {

	/**
	 * Given the a piece of text, the method will return a {@link Spot} if the
	 * given text is associated to one or more entities. Otherwise, it will return
	 * null.
	 * 
	 * @param spot
	 *            - a piece of text possibly referring to one or more entities
	 * @return a Spot object containing several informations about the spot and
	 *         the possible entities linked by the spot, null if the string is
	 *         not referring to any entity
	 * 
	 */
	public Spot getSpot(String spot);

}
