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
package it.cnr.isti.hpc.dexter.spot.ram;

import it.cnr.isti.hpc.io.Serializer;
import it.cnr.isti.hpc.io.reader.RecordReader;
import it.cnr.isti.hpc.io.reader.TsvRecordParser;
import it.cnr.isti.hpc.io.reader.TsvTuple;
import it.cnr.isti.hpc.log.ProgressLogger;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Feb 27, 2014
 */
public class EntityToSpotListMap implements Serializable {

	private static final long serialVersionUID = 1L;

	Int2ObjectFunction<String> map;

	private static final Logger logger = LoggerFactory
			.getLogger(EntityToSpotListMap.class);

	public EntityToSpotListMap() {
		map = new Int2ObjectArrayMap<String>(1000000);
	}

	public void loadFromFile(String file) {
		RecordReader<TsvTuple> reader = new RecordReader<TsvTuple>(file,
				new TsvRecordParser("spot", "id"));
		StringBuilder sb = new StringBuilder();
		int currentId = -1;
		ProgressLogger pl = new ProgressLogger(
				"readed {} entities with their spots", 100000);

		for (TsvTuple tuple : reader) {

			int e = tuple.getInt("id");
			String spot = tuple.get("spot");
			if ((currentId != e) && (sb.length() > 0)) {
				// new item, dump the old
				sb.setLength(sb.length() - 1);
				map.put(currentId, sb.toString());
				currentId = e;
				sb.setLength(0);
				sb.append(spot);
				sb.append('\t');
				pl.up();
			} else {
				sb.append(spot);
				sb.append('\t');
			}

		}
		map.put(currentId, sb.toString());
	}

	public void dump(String outputFile) {
		Serializer serializer = new Serializer();
		serializer.dump(this, outputFile);
	}

	public static EntityToSpotListMap load(String serializedFile) {
		Serializer serializer = new Serializer();
		EntityToSpotListMap obj = (EntityToSpotListMap) serializer
				.load(serializedFile);
		return obj;
	}

	public List<String> getSpots(int wikiid) {
		String spots = map.get(wikiid);
		if (spots == null) {
			logger.warn("no spots for wiki-id {}", wikiid);
			return Collections.emptyList();
		}
		String[] list = spots.split("\t");
		return Arrays.asList(list);
	}

}
