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
package it.cnr.isti.hpc.dexter.spot.filter;

import it.cnr.isti.hpc.dexter.spot.Spot;
import it.cnr.isti.hpc.property.ProjectProperties;

/**
 * CommonnessFilter.java
 *
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it
 * created on 01/ago/2012
 */
public class ProbabilityFilter implements SpotFilter {
	
	static ProjectProperties properties = new ProjectProperties(ProbabilityFilter.class);
	double threshold = 0;
	public ProbabilityFilter(){
		if (properties.has("spot.probability.threshold")){
			threshold = Double.parseDouble(properties.get("spot.probability.threshold"));
		}
	}

	
	public boolean isRemove(Spot spot) {
		if (spot == null) return true;
		return (spot.getProbability() <= threshold );
	}

}
