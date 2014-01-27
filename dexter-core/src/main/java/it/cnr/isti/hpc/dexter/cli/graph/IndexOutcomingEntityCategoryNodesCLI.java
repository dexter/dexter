/**
 *  Copyright 2011 Diego Ceccarelli
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
package it.cnr.isti.hpc.dexter.cli.graph;

import it.cnr.isti.hpc.cli.AbstractCommandLineInterface;
import it.cnr.isti.hpc.dexter.graph.EntityCategoryNodeFactory;
import it.cnr.isti.hpc.dexter.graph.Node;
import it.cnr.isti.hpc.dexter.graph.NodeStar.Direction;
import it.cnr.isti.hpc.dexter.graph.NodesWriter;
import it.cnr.isti.hpc.dexter.util.DexterParams;
import it.cnr.isti.hpc.io.reader.RecordReader;
import it.cnr.isti.hpc.log.ProgressLogger;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IndexOutcomingEntityCategoryNodesCLI generate the binary file containing the
 * outcoming entities for a category. Takes a file containing the outcoming
 * entities in the format: <br>
 * <br>
 * <code>
 * 	entity_id <tab> c1 c2 ... cN  
 * </code> <br>
 * <br>
 * where <code> c1 c2 .. cN </code> are the entities linking to the given entity
 * <code> category</code>. The file is sorted by <code>entity_id</code> , and in
 * each line the outcoming categories are sorted by their numerical id.
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 16/nov/2013
 */
public class IndexOutcomingEntityCategoryNodesCLI extends
		AbstractCommandLineInterface {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(IndexOutcomingEntityCategoryNodesCLI.class);

	private static String[] params = new String[] { "input" };

	private static final String USAGE = "java -cp $jar "
			+ IndexOutcomingEntityCategoryNodesCLI.class
			+ " -input outcoming-entity-categories-file";

	private static final DexterParams dexterParams = DexterParams.getInstance();

	public static void main(String[] args) {
		IndexOutcomingEntityCategoryNodesCLI cli = new IndexOutcomingEntityCategoryNodesCLI(
				args);

		File outcomingFile = dexterParams.getGraph("entity-category",
				Direction.OUT);

		if (outcomingFile.exists()) {
			logger.info("serialized file {} yet exists, removing",
					outcomingFile);
			outcomingFile.delete();
		}
		ProgressLogger pl = new ProgressLogger("indexed {} nodes", 100000);
		RecordReader<Node> reader = new RecordReader<Node>(cli.getInput(),
				new Node.Parser());
		NodesWriter writer = EntityCategoryNodeFactory
				.getOutcomingNodeWriter(EntityCategoryNodeFactory.STD_TYPE);
		for (Node n : reader) {
			pl.up();
			writer.add(n);
		}
		writer.close();

	}

	public IndexOutcomingEntityCategoryNodesCLI(String[] args) {
		super(args, params, USAGE);
	}
}
