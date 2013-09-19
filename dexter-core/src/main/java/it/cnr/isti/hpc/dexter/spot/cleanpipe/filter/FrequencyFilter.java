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
package it.cnr.isti.hpc.dexter.spot.cleanpipe.filter;

import it.cnr.isti.hpc.dexter.spot.Spot;

/**
 * FrequencyFilter removes spots that are not associated to any entity
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 01/ago/2012
 */
public class FrequencyFilter extends Filter<Spot> {

	public boolean isFilter(Spot spot) {

		return (spot.getEntities().size() == 0);
	}

}
