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
package it.cnr.isti.hpc.dexter.spotter;

import it.cnr.isti.hpc.dexter.spot.SpotMatchList;
import it.cnr.isti.hpc.dexter.spotter.filter.SpotMatchFilter;
import it.cnr.isti.hpc.dexter.util.DexterLocalParams;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Feb 12, 2014
 */
public abstract class AbstractSpotter implements Spotter {

	private static final Logger logger = LoggerFactory
			.getLogger(AbstractSpotter.class);
	private List<SpotMatchFilter> filters;

	/**
	 * Set the filter to apply to the spots after the spotting
	 * 
	 */
	@Override
	public void setFilters(List<SpotMatchFilter> filters) {
		this.filters = filters;
	}

	/**
	 * Removes the SpotMatch based on the filters.
	 * 
	 * @param sml
	 *            the SpotMatchList to filter, at the end of the function the
	 *            list is modified
	 * 
	 **/
	@Override
	public SpotMatchList filter(DexterLocalParams params, SpotMatchList sml) {
		if (filters == null) {
			logger.debug("no filters");
			return sml;
		}
		for (SpotMatchFilter filter : filters) {
			if (filter == null)
				continue;
			sml = filter.filter(params, sml);
		}
		return sml;

	}
}
