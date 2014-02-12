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

import java.util.Collections;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Feb 12, 2014
 */
public class SpotOverlapFilter implements SpotMatchFilter {

	private static final Logger logger = LoggerFactory
			.getLogger(SpotOverlapFilter.class);

	float probability;

	@Override
	public SpotMatchList filter(SpotMatchList sml) {
		Collections.sort(sml, new SpotMatchLinkProbabilityComparator());
		SpotMatchList filtered = new SpotMatchList();

		for (SpotMatch spot : sml) {
			boolean overlaps = false;
			for (SpotMatch spot1 : sml) {
				overlaps = spot.overlaps(spot1);
				if (overlaps) {
					break;
				}
			}
			if (!overlaps) {
				filtered.add(spot);
			}

		}
		return filtered;
	}

	private static class SpotMatchLengthComparator implements
			Comparator<SpotMatch> {

		@Override
		public int compare(SpotMatch o1, SpotMatch o2) {
			int l1 = o1.getSpot().getMention().length();
			int l2 = o2.getSpot().getMention().length();
			return l2 - l1;
		}

	}

	private static class SpotMatchLinkProbabilityComparator implements
			Comparator<SpotMatch> {

		@Override
		public int compare(SpotMatch o1, SpotMatch o2) {
			double l1 = o1.getSpot().getLinkProbability();
			double l2 = o2.getSpot().getLinkProbability();
			if (l1 > l2)
				return -1;
			if (l1 < l2)
				return 1;
			return 0;
		}
	}

	@Override
	public void init(DexterParams dexterParams, DexterLocalParams initParams) {

	}
}
