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
package it.cnr.isti.hpc.dexter.label.mapdb;

import it.cnr.isti.hpc.dexter.hash.IdHelper;
import it.cnr.isti.hpc.dexter.hash.LabelToId;
import it.cnr.isti.hpc.dexter.hash.LabelToIdWriter;
import it.cnr.isti.hpc.mapdb.MapDB;
import it.cnr.isti.hpc.property.ProjectProperties;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ArticleToHash.java
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 05/lug/2012
 */
public class MapDBLabelToId implements LabelToId, LabelToIdWriter {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(MapDBLabelToId.class);

	ProjectProperties properties;
	private static final String COLLECTION_NAME = "a2id";
	private static MapDBLabelToId instance;
	MapDB db = MapDBInstance.DB;
	Map<String, Integer> map;
	int numEntry = 0;
	int commitFrequency = -1;

	private MapDBLabelToId() {
		properties = new ProjectProperties(this.getClass());

		map = db.getCollection(COLLECTION_NAME);
		commitFrequency = properties.getInt("jdbm.commit");
	}

	public static MapDBLabelToId getInstance() {
		if (instance == null)
			instance = new MapDBLabelToId();
		return instance;
	}

	public void add(String label, int key) {
		numEntry++;
		if (label.isEmpty() || key == 0) {
			logger.error("label \"{}\" empty or key \"{}\" is 0 ", label, key);
			return;
		}
		map.put(label, key);
		if (numEntry % commitFrequency == 0) {
			logger.info("autocommit");
			db.commit();
		}
	}

	public Integer getId(String label) {
		Integer i = map.get(label);
		if (i == null)
			i = IdHelper.NOID;
		return i;

	}

	public void commit() {
		db.commit();

	}

	
	public void close() {
		db.close();
	}

}
