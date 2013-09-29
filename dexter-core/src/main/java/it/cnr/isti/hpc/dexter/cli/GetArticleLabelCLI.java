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
import it.cnr.isti.hpc.dexter.label.IdHelper;
import it.cnr.isti.hpc.dexter.label.IdHelperFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * Returns the article label given its integer identifier.
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 21/nov/2011
 */
public class GetArticleLabelCLI extends AbstractCommandLineInterface {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(GetArticleLabelCLI.class);
	static String[] params = new String[] { "id" };

	private static final String USAGE = "java -cp $jar "
			+ GetArticleLabelCLI.class + " -id id";

	public static void main(String[] args) {
		
		GetArticleLabelCLI cli = new GetArticleLabelCLI(args);
		int id = 0;
		try {
			id = Integer.parseInt(cli.getParam("id"));
		} catch (Exception e ){
			 logger.error("id must be an integer ");
			 System.exit(-1);
		}
		IdHelper hash = IdHelperFactory.getStdIdHelper();
		long start = System.currentTimeMillis();
		String title = hash.getLabel(id);
		long end = System.currentTimeMillis();
		System.out.println("title \t"+title);
		System.out.println("id    \t"+ id);
		System.out.println("time  \t"+(end-start));
		

	}
	
	

	public GetArticleLabelCLI(String[] args) {
		super(args, params, USAGE);
	}
 }
