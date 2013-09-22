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
package it.cnr.isti.hpc.dexter.spot.cleanpipe;


/**
 * A function performs a single manipulation over an object within 
 * a pipeline. 
 * 
 * @see it.cnr.isti.hpc.dexter.spot.cleanpipe.cleaner.Cleaner
 * @see it.cnr.isti.hpc.dexter.spot.cleanpipe.mapper.Mapper
 * @see it.cnr.isti.hpc.dexter.spot.cleanpipe.filter.Filter
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Sep 18, 2013
 */
public abstract class Function<T> {
	
	/**
	 * Performs the manipulation, eventual outputs produced by the function 
	 * are output using the collector object. 
	 * 
	 * @param elem - the object to manipulate
	 * @param collector - the output collector, use the method <code> pushResult </code> to output one or more manipulations
	 */
	protected abstract void eval(T elem, Pipe<T>.OutputCollector collector);

	

}
