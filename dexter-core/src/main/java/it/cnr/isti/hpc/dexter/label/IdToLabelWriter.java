/**
 *  Copyright 2012 Diego Ceccarelli
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License
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
package it.cnr.isti.hpc.dexter.label;

/**
 * 
 * IdToLabelWriter takes care to write in a persistent format the 
 * mapping <code> id -> label </code>.
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 * Created on Oct 7, 2012
 */
public interface IdToLabelWriter {
	
	
	/**
	 * Adds the mapping <code> id -> label </code>.
	 */
	public void add(int id, String label);
	
	/**
	 * Closes the writer.
	 */
	public void close();
	

}
