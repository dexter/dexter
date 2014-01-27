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
package it.cnr.isti.hpc.dexter.rest.domain;

import it.cnr.isti.hpc.dexter.util.DexterLocalParams;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Jan 15, 2014
 */
public class Tagmeta {

	String spotter;
	String disambiguator;

	DexterLocalParams requestParams;

	public String getSpotter() {
		return spotter;
	}

	public void setSpotter(String spotter) {
		this.spotter = spotter;
	}

	public String getDisambiguator() {
		return disambiguator;
	}

	public void setDisambiguator(String disambiguator) {
		this.disambiguator = disambiguator;
	}

	public DexterLocalParams getRequestParams() {
		return requestParams;
	}

	public void setRequestParams(DexterLocalParams requestParams) {
		this.requestParams = requestParams;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((disambiguator == null) ? 0 : disambiguator.hashCode());
		result = prime * result + ((spotter == null) ? 0 : spotter.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tagmeta other = (Tagmeta) obj;
		if (disambiguator == null) {
			if (other.disambiguator != null)
				return false;
		} else if (!disambiguator.equals(other.disambiguator))
			return false;
		if (spotter == null) {
			if (other.spotter != null)
				return false;
		} else if (!spotter.equals(other.spotter))
			return false;
		return true;
	}

}
