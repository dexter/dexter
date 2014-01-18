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
import it.cnr.isti.hpc.dexter.spot.SpotMatchList;
import it.cnr.isti.hpc.dexter.util.DexterLocalParams;
import it.cnr.isti.hpc.dexter.util.DexterParams;

/**
 * A disambiguator is one of the main component of an entity tagger. A spot
 * could refer to more than one entity, for example consider the sentence: <br/>
 * <br/>
 * <em>On July 20, 1969, the <strong>Apollo 11</strong> astronauts - 
 * <strong>Neil Armstrong</strong>, <strong>Michael Collins</strong>, 
 * and <strong>Edwin “Buzz” Aldrin Jr.</strong> - realized 
 * <strong>President Kennedy</strong>’s dream.</em> <br/>
 * <br/>
 * 
 * 
 * It is quite easy to map the spot to the entity John F. Kennedy, since in
 * Wikipedia there are 98 anchors exactly matching such fragment of text and
 * linking to the U.S. president page. On the other hand, it could be not so
 * easy for a software to decide if Michael Collins is an astronaut, an Irish
 * leader or the president of the Irish provisional government in 1922. <br/>
 * <br/>
 * Given a list of matched spots, where each spot has one or more candidate
 * entities, a disambiguator produces a list of entities, so that each spot can
 * have at most one entity.
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Aug 2, 2013
 */
public interface Disambiguator {

	/**
	 * Given a list of matched spots, where each spot has one or more candidate
	 * entities, returns a list of entities, so that each spot can have at most
	 * one entity.
	 * 
	 * 
	 * @param requestParams
	 *            contains particular parameters set for this query, can be
	 *            null.
	 * @param sml
	 *            - the list of spots detected in a document, each spot match
	 *            contains a list of candidate entities.
	 * 
	 * @returns a list of entities, so that each spot can have at most one
	 *          entity.
	 */
	public EntityMatchList disambiguate(DexterLocalParams requestParams,
			SpotMatchList sml);

	/**
	 * Initializes the Disambiguator with the global params.
	 * 
	 * @param dexterParams
	 *            the global params of the project.
	 * @param defaultModuleParams
	 *            the module init params
	 * 
	 */
	public void init(DexterParams dexterParams,
			DexterLocalParams defaultModuleParams);
}
