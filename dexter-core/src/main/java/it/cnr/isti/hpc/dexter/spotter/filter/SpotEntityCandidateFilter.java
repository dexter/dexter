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
package it.cnr.isti.hpc.dexter.spotter.filter;

import it.cnr.isti.hpc.dexter.spot.SpotMatch;
import it.cnr.isti.hpc.dexter.spot.SpotMatchList;
import it.cnr.isti.hpc.dexter.util.DexterLocalParams;
import it.cnr.isti.hpc.dexter.util.DexterParams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Removes a spot if the number of candidates is not in the given range (min,
 * max)
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Feb 12, 2014
 */
public class SpotEntityCandidateFilter implements SpotMatchFilter {

	private static final Logger logger = LoggerFactory
			.getLogger(SpotEntityCandidateFilter.class);

	private int min = 1;
	private int max = Integer.MAX_VALUE;

	@Override
	public SpotMatchList filter(DexterLocalParams params, SpotMatchList sml) {

		SpotMatchList filtered = new SpotMatchList();

		for (SpotMatch spot : sml) {
			int candidates = spot.getEntities().size();
			if (candidates >= min && candidates <= max) {
				filtered.add(spot);
			} else {
				logger.info("spot {} removed, candidates {}, out of range"
						+ String.format("[min= %d, max= %d]", min, max),
						spot.getMention(), candidates);
			}
		}
		return filtered;
	}

	@Override
	public void init(DexterParams dexterParams, DexterLocalParams initParams) {
		if (initParams.containsKey("min")) {
			min = initParams.getIntParam("min");
			logger.info("min param set to {}", min);
		}
		if (initParams.containsKey("max")) {
			max = initParams.getIntParam("max");
			logger.info("max param set to {}", max);
		}

	}
}
