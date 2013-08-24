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
package it.cnr.isti.hpc.dexter.cli;

import it.cnr.isti.hpc.cli.AbstractCommandLineInterface;
import it.cnr.isti.hpc.dexter.spot.SpotManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GenerateArticleHashCLI takes the json dump of wikipedia and create a function
 * that maps each article to an int, and the reverse.
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 02/lug/2012
 */
public class CleanLabelsCLI extends AbstractCommandLineInterface {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(CleanLabelsCLI.class);

	private static final String USAGE = "java -cp $jar "
			+ CleanLabelsCLI.class
			+ " -input titles.tsv ";
	private static String[] params = new String[] { INPUT, OUTPUT};	
		
	public static void main(String[] args) {
		
		CleanLabelsCLI cli = new CleanLabelsCLI(args);
		SpotManager cleaner = SpotManager.getStandardSpotCleaner();
		
		cli.openInputAndOutput();
		String query = "";
		
		while (( query = cli.readLineFromInput())!= null){
			String cleanQuery = cleaner.clean(query);
			cli.writeLineInOutput(query.replace('\t', ' ')+"\t"+cleanQuery);
		}
		cli.closeOutput();
	}



	public CleanLabelsCLI(String[] args) {
		super(args, params, USAGE);
	}
}
