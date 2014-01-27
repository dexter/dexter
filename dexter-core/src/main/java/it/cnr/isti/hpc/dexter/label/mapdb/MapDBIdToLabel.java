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

import it.cnr.isti.hpc.dexter.label.IdToLabel;
import it.cnr.isti.hpc.dexter.label.IdToLabelWriter;
import it.cnr.isti.hpc.mapdb.MapDB;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MapDBIdToLabel allows to retrieve the label of an entity given its ID.
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 05/lug/2012
 */
public class MapDBIdToLabel implements IdToLabel, IdToLabelWriter {

	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(MapDBLabelToId.class);

	private static final String COLLECTION_NAME = "id2a";
	private static MapDBIdToLabel instance;
	MapDB db;
	Map<Integer, String> map;

	// int numEntry = 0;
	// int commitFrequency = -1;

	private MapDBIdToLabel(boolean readonly) {
		db = MapDBInstance.getInstance(readonly);
		map = db.getCollection(COLLECTION_NAME);
		// commitFrequency = properties.getInt("mapdb.commit");
	}

	public static MapDBIdToLabel getInstance(boolean readonly) {
		if (instance == null)
			instance = new MapDBIdToLabel(readonly);
		return instance;
	}

	@Override
	public void add(int key, String label) {
		// numEntry++;
		if (label.isEmpty() || key == 0) {
			logger.error("label \"{}\" empty or key \"{}\" is 0 ", label, key);
			return;
		}
		map.put(key, label);
		// if (numEntry % commitFrequency == 0) {
		// logger.info("autocommit");
		// db.commit();
		// }
	}

	@Override
	public String getLabel(Integer key) {
		String label = map.get(key);
		if (label == null)
			label = "";
		return label;
	}

	public void commit() {
		db.commit();

	}

	@Override
	public void close() {
		db.commit();
		db.close();
	}
}
