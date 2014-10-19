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
package it.cnr.isti.hpc.dexter.cli;

import it.cnr.isti.hpc.cli.AbstractCommandLineInterface;
import it.cnr.isti.hpc.dexter.StandardTagger;
import it.cnr.isti.hpc.dexter.Tagger;
import it.cnr.isti.hpc.dexter.common.Document;
import it.cnr.isti.hpc.dexter.common.FlatDocument;
import it.cnr.isti.hpc.dexter.entity.EntityMatchList;
import it.cnr.isti.hpc.dexter.util.DexterParams;
import it.cnr.isti.hpc.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Given a file containing plain text prints on the stdout the entities detected
 * by the {@link StandardTagger} tagger.
 * 
 */
public class DexterCLI extends AbstractCommandLineInterface {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(DexterCLI.class);

	private static String[] params = new String[] { "tagger", "input" };

	private static final String USAGE = "java -cp $jar " + DexterCLI.class
			+ " -input filetospot";

	public static void main(String[] args) {
		DexterCLI cli = new DexterCLI(args);
		String input = cli.getInput();
		Document doc = new FlatDocument(IOUtils.getFileAsString(input));
		DexterParams params = DexterParams.getInstance();
		Tagger tagger = params.getTagger(cli.getParam("tagger"));
		EntityMatchList eml = tagger.tag(doc);
		System.out.println(eml);
	}

	public DexterCLI(String[] args) {
		super(args, params, USAGE);
	}
}
