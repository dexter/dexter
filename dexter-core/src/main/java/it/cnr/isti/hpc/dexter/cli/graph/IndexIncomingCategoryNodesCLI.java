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
import it.cnr.isti.hpc.dexter.graph.CategoryNodeFactory;
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
 * IndexIncomingNodesCLI generate the binary file containing the incoming
 * categories. Takes a file containing the incoming categories in the format: <br>
 * <br>
 * <code>
 * 	entity/category_id <tab> c1 c2 ... cN  
 * </code> <br>
 * <br>
 * where <code> c1 c2 .. cN </code> are the categories linking to the given
 * entity (or category) The file is sorted by <code>entity/category_id</code>,
 * and in each line the incoming entities are sorted by their numerical id.
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 21/nov/2011
 */
public class IndexIncomingCategoryNodesCLI extends AbstractCommandLineInterface {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(IndexIncomingCategoryNodesCLI.class);

	private static final DexterParams dexterParams = DexterParams.getInstance();

	private static String[] params = new String[] { "input" };

	private static final String USAGE = "java -cp $jar "
			+ IndexIncomingCategoryNodesCLI.class
			+ " -input incoming-categories-file";

	public static void main(String[] args) {
		IndexIncomingCategoryNodesCLI cli = new IndexIncomingCategoryNodesCLI(
				args);

		File incomingFile = dexterParams.getGraph("category-category",
				Direction.IN);
		if (incomingFile.exists()) {
			logger.info("serialized file {} yet exists, removing", incomingFile);
			incomingFile.delete();
		}
		ProgressLogger pl = new ProgressLogger("indexed {} nodes", 100000);
		RecordReader<Node> reader = new RecordReader<Node>(cli.getInput(),
				new Node.Parser());
		NodesWriter writer = CategoryNodeFactory
				.getIncomingNodeWriter(CategoryNodeFactory.STD_TYPE);
		for (Node n : reader) {
			pl.up();
			writer.add(n);
		}
		writer.close();

	}

	public IndexIncomingCategoryNodesCLI(String[] args) {
		super(args, params, USAGE);
	}
}
