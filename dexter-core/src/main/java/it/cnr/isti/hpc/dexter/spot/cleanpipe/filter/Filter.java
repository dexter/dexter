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

import it.cnr.isti.hpc.dexter.spot.cleanpipe.Function;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.Pipe;

/**
 * Filter allows to remove a given spot if it does not respect a filter
 * constraint.
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 20/lug/2012
 */
public abstract class Filter<T> extends Function<T> {
	
	


	protected void eval(T elem, Pipe<T>.OutputCollector collector) {
		if (!isFilter(elem)) {
			collector.pushResult(elem);
		}
	}

	/**
	 * returns true if the given spot does not respect the filter constraint.
	 * 
	 * @param label
	 * @return boolean if the current spot does not respect the filter
	 *         constraint.
	 */
	public abstract boolean isFilter(T elem);

}
