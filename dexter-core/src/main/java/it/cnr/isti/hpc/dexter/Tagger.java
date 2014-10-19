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
package it.cnr.isti.hpc.dexter;

import it.cnr.isti.hpc.dexter.common.Document;
import it.cnr.isti.hpc.dexter.entity.EntityMatchList;
import it.cnr.isti.hpc.dexter.util.DexterLocalParams;
import it.cnr.isti.hpc.dexter.util.DexterParams;

/**
 * 
 * A tagger performs the whole entity linking process. A typical entity linking
 * system performs this task in two steps: spotting and disambiguation. <br>
 * <br>
 * The spotting process identifies a set of candidate spots in the input
 * document, and produces a list of candidate entities for each spot. </br>
 * Then, the disambiguation process selects the most relevant spots and the most
 * likely entities among the candidates. The spotting step exploits a given
 * catalog of named entities, or some knowledge base, to devise the possible
 * mentions of entities occurring in the input.
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Aug 2, 2013
 */
public interface Tagger {

	/**
	 * Takes a Document performs the entity linking. It returns an {link
	 * EntityMatchList}, that maps some spots detected in the document with one
	 * (and only one) entity.
	 * 
	 * @param document
	 *            - a document to annotate.
	 * @returns A list of entities detected in the document, an empty list if
	 *          the tagger does not annotate anything.
	 */
	public EntityMatchList tag(Document document);

	/**
	 * Takes a Document performs the entity linking. It returns an {link
	 * EntityMatchList}, that maps some spots detected in the document with one
	 * (and only one) entity.
	 * 
	 * @param localParams
	 *            contains particular parameters set for this query, can be null
	 * @param document
	 *            - a document to annotate.
	 * @returns A list of entities detected in the document, an empty list if
	 *          the tagger does not annotate anything.
	 */
	public EntityMatchList tag(DexterLocalParams localParams, Document document);

	/**
	 * Initializes the Tagger with the global params.
	 * 
	 * @param dexterParams
	 *            the global params of the project.
	 * 
	 * @param defaultModuleParams
	 *            the module init params
	 */
	public void init(DexterParams dexterParams,
			DexterLocalParams defaultModuleParams);
}
