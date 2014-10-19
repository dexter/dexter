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
package it.cnr.isti.hpc.dexter.graph.ram;

import it.cnr.isti.hpc.dexter.graph.Node;
import it.cnr.isti.hpc.dexter.graph.NodeStar;
import it.cnr.isti.hpc.dexter.graph.NodesWriter;
import it.cnr.isti.hpc.io.Serializer;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RamNodes implements {@link NodeStar} object in main memory.
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 05/lug/2012
 */
public abstract class RamNodes implements NodesWriter, NodeStar {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(RamNodes.class);

	Int2ObjectOpenHashMap<int[]> map;
	File serializedFile = null;

	// int numEntry = 0;

	protected RamNodes(File serializedFile) {
		this.serializedFile = serializedFile;
		if (serializedFile.exists()) {
			logger.info("loading {} ", serializedFile);
			load();
		} else {
			logger.warn("cannot find {}, using empty ram nodes",
					serializedFile.getAbsolutePath());

			map = new Int2ObjectOpenHashMap<int[]>(5000000);
		}
	}

	private void load() {
		Serializer sr = new Serializer();
		map = (Int2ObjectOpenHashMap<int[]>) sr.load(serializedFile.getPath());
	}

	@Override
	public void add(Node n) {
		map.put(n.getNode(), n.getNeighbours());
	}

	public void commit() {
		return;
	}

	@Override
	public void close() {
		Serializer sr = new Serializer();
		logger.info("storing edges in {} ", serializedFile);
		sr.dump(map, serializedFile.getPath());
	}

	@Override
	public int[] getNeighbours(int id) {
		int[] n = map.get(id);
		if (n == null) {
			logger.debug("no neighbours for node {} ", id);
			n = new int[0];
		}
		return n;
	}

	@Override
	public Node getNode(int id) {
		int[] neigh = getNeighbours(id);
		return new Node(id, neigh);
	}

	public IntIterator iterator() {
		return map.keySet().iterator();
	}

	@Override
	public int size() {
		return map.size();
	}

}
