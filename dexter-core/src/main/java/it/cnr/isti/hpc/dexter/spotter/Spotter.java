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
package it.cnr.isti.hpc.dexter.spotter;

import it.cnr.isti.hpc.dexter.common.Document;
import it.cnr.isti.hpc.dexter.spot.SpotMatchList;
import it.cnr.isti.hpc.dexter.spotter.filter.SpotMatchFilter;
import it.cnr.isti.hpc.dexter.util.DexterLocalParams;
import it.cnr.isti.hpc.dexter.util.DexterParams;

import java.util.List;

/**
 * Spotting aims at identifying spots, i.e., contiguous sequences of n terms
 * (n-grams) occurring in a document <code>D</code> that might mention some
 * entity <code>e</code>. <br/>
 * <br/>
 * Each spot is associated with a set of candidate entities C, that could be
 * represented by the spot.
 * 
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 */
public interface Spotter {
	/**
	 * Given a document a Spotter identifies all the fragments that could be
	 * referring to an entity. It returns a list of Spots, i.e. mentions (
	 * {@link SpotMatchList}) each one associated with a set of candidate
	 * entities C, that could be represented by the spot.
	 * 
	 * @param dexterParams
	 *            the global params of the project.
	 * @param localParams
	 *            contains particular parameters set for this query, can be
	 *            null.
	 * @param sml
	 *            the list of spots detected in a document, each spot match
	 *            contains a list of candidate entities.
	 * 
	 * @return a list of spots, each one associated with a set of candidate
	 *         entities C, that could be represented by the spot.
	 * 
	 */
	public SpotMatchList match(DexterLocalParams localParams, Document document);

	/**
	 * Initializes the Spotter with the global params
	 * 
	 * @param dexterParams
	 *            the global params of the project.
	 * 
	 * @param defaultModuleParams
	 *            the module init params
	 * 
	 */
	public void init(DexterParams dexterParams,
			DexterLocalParams defaultModuleParams);

	/**
	 * Set the filter to apply to the spots after the spotting.
	 * 
	 */
	public void setFilters(List<SpotMatchFilter> filters);

	/**
	 * Removes the SpotMatch based on the filters.
	 * 
	 * @param sml
	 *            the SpotMatchList to filter, at the end of the function the
	 *            list is modified.
	 * @return the SpotMatchList filtered.
	 * 
	 **/
	public SpotMatchList filter(DexterLocalParams params, SpotMatchList sml);

}
