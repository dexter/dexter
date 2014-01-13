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
import it.cnr.isti.hpc.dexter.util.DexterParams;

/**
 * ProbabilityFilter removes all the spots with link probability threshold lower
 * than a certain threshold. The link probability of a spot s is computed as :
 * <code>
 * <br/>
 * <br/>
 * 
 * &nbsp; &nbsp; &nbsp; &nbsp; number of times in which s appears as an anchor test <br>
 * p(s) = -------------------------------------------------------------- <br/>
 * &nbsp; &nbsp; &nbsp; &nbsp;  number of times in which s appears as simple (or anchor) text
 * </code>
 * 
 * The threshold for the link probability by default is read in the
 * project.properties file (property <code> spot.probability.threshold </code>).
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 01/ago/2012
 */
public class ProbabilityFilter extends Filter<Spot> {

	private float threshold = 0.0f;

	DexterParams params = DexterParams.getInstance();

	public ProbabilityFilter() {
		threshold = params.getThreshold("linkprobability");
	}

	@Override
	public boolean isFilter(Spot spot) {
		if (spot == null)
			return true;
		return (spot.getLinkProbability() <= threshold);
	}

}
