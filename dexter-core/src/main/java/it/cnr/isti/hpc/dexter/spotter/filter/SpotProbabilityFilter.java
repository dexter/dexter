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
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Feb 12, 2014
 */
public class SpotProbabilityFilter implements SpotMatchFilter {

	private static final Logger logger = LoggerFactory
			.getLogger(SpotProbabilityFilter.class);

	float probability;

	@Override
	public SpotMatchList filter(DexterLocalParams params, SpotMatchList sml) {
		if (params.containsKey("lp")) {
			probability = Float.parseFloat(params.getParam("lp"));
		}
		logger.info("link probability filter = {}", probability);
		SpotMatchList filtered = new SpotMatchList();
		for (SpotMatch match : sml) {
			if (match.getLinkProbability() >= probability) {
				filtered.add(match);
			} else {
				logger.info("filtering spot '{}': link probability low {}",
						match.getMention(), match.getLinkProbability());
			}
		}
		return filtered;
	}

	@Override
	public void init(DexterParams dexterParams, DexterLocalParams initParams) {
		probability = dexterParams.getThreshold("linkprobability");
		if (initParams.containsKey("lp")) {
			probability = Float.parseFloat(initParams.getParam("lp"));
		}
		logger.info("link probability filter = {}", probability);
	}
}
