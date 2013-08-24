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

import it.cnr.isti.hpc.dexter.entity.Entity;
import it.cnr.isti.hpc.dexter.spot.Spot;
import it.cnr.isti.hpc.property.ProjectProperties;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CommonnessFilter, filters entities with low probability to 
 * be linked with the spot
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it
 * created on 01/ago/2012
 */
public class CommonnessFilter implements SpotFilter {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(CommonnessFilter.class);
	
	double threshold = 0.005;
	
	public CommonnessFilter(){	
		ProjectProperties properties = new ProjectProperties(CommonnessFilter.class);
		if (properties.has("spot.commonness.threshold")){
			threshold = Double.parseDouble(properties.get("spot.commonness.threshold"));
		}
	}


	public boolean isRemove(Spot spot) {
		List<Entity> entities = new ArrayList<Entity>();
		int link = spot.getLink();
		for (Entity e : spot.getEntities()){
			double commonness = spot.getEntityCommonness(e);
			if ( commonness > threshold){
				entities.add(e);
			}else{
				logger.debug("delete entity {} commonness = {} ",e.id(), commonness);
			}
		}
		spot.setEntities(entities);
		return entities.isEmpty();
		
	}


	

}
