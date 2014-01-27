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
package it.cnr.isti.hpc.dexter.disambiguation;

import it.cnr.isti.hpc.dexter.entity.EntityMatchList;
import it.cnr.isti.hpc.dexter.spot.SpotMatch;
import it.cnr.isti.hpc.dexter.spot.SpotMatchList;
import it.cnr.isti.hpc.dexter.util.DexterLocalParams;
import it.cnr.isti.hpc.dexter.util.DexterParams;

/**
 * Implements the Okkam's Razor principle, resolving the ambiguity for a spot
 * using the entity with the largest probability to be represented by the spot
 * (this probability is called <i>commonness</i>, and it is computed as the
 * ratio between the links that point to the entity (using the spot as anchor)
 * and the total number of links that have the spot as anchor.
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Sep 30, 2013
 */
public class TopScoreEntityDisambiguator implements Disambiguator {

	@Override
	public EntityMatchList disambiguate(DexterLocalParams localParams,
			SpotMatchList sml) {
		EntityMatchList eml = new EntityMatchList();
		for (SpotMatch match : sml) {
			EntityMatchList list = match.getEntities();
			if (!list.isEmpty()) {
				list.sort();
				eml.add(list.get(0));
			}
		}
		return eml;
	}

	@Override
	public void init(DexterParams dexterParams,
			DexterLocalParams dexterModuleParams) {

	}

}
