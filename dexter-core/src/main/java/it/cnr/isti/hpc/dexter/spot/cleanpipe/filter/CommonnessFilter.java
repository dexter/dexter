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

import it.cnr.isti.hpc.dexter.entity.Entity;
import it.cnr.isti.hpc.dexter.spot.Spot;
import it.cnr.isti.hpc.dexter.util.DexterParams;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CommonnessFilter, filters entities with low probability to be linked with the
 * spot
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 01/ago/2012
 */
public class CommonnessFilter extends Filter<Spot> {

	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(CommonnessFilter.class);

	DexterParams params = DexterParams.getInstance();
	double threshold = 0.005;

	public CommonnessFilter() {
		// TODO check for errors
		threshold = params.getThreshold("commonness");
	}

	@Override
	public boolean isFilter(Spot spot) {
		List<Entity> entities = new ArrayList<Entity>();
		for (Entity e : spot.getEntities()) {
			double commonness = spot.getEntityCommonness(e);
			if (commonness > threshold) {
				entities.add(e);
			} else {
				logger.debug("delete entity {} commonness = {} ", e.getId(),
						commonness);
			}
		}
		spot.setEntities(entities);
		return entities.isEmpty();

	}

}
