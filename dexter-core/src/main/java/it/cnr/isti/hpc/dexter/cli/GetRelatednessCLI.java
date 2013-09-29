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
import it.cnr.isti.hpc.dexter.graph.IncomingNodes;
import it.cnr.isti.hpc.dexter.graph.NodeFactory;
import it.cnr.isti.hpc.dexter.label.IdHelper;
import it.cnr.isti.hpc.dexter.label.IdHelperFactory;
import it.cnr.isti.hpc.dexter.relatedness.RelatednessFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Get relatedness between two entities
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it
 */
public class GetRelatednessCLI extends AbstractCommandLineInterface {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(GetRelatednessCLI.class);

	private static final String USAGE = "java -cp $jar "
			+ GetRelatednessCLI.class
			+ " -e entity -c candidate ";
	private static String[] params = new String[] { "e", "c"};	
		
	public static void main(String[] args) {
		IdHelper helper = IdHelperFactory.getStdIdHelper();
		IncomingNodes nodes = NodeFactory.getIncomingNodes(NodeFactory.STD_TYPE);
		GetRelatednessCLI cli = new GetRelatednessCLI(args);
		RelatednessFactory relatedness = new RelatednessFactory();
		String e = cli.getParam("e");
		String c = cli.getParam("c");
		int ee,cc;
		String eee,ccc;
		try {
			ee = Integer.parseInt(e);
			eee = helper.getLabel(ee);
		} catch(Exception ex){
			ee = helper.getId(e);
			eee = e;
		}
		try {
			cc = Integer.parseInt(c);
			ccc = helper.getLabel(ee);
		} catch(Exception ex){
			cc = helper.getId(c);
			ccc = c;
		}
		
		System.out.println("relatedness e "+eee+ " "+ee);
		System.out.println("relatedness c "+ccc+ " "+cc);
		System.out.println (" = "+relatedness.getScore(ee, cc));
		
		StringBuffer array = new StringBuffer();
		for (int i : nodes.getNeighbours(ee)) array.append(i).append(", ");
				
		System.out.println("incoming("+eee+") = "+array.toString());
		array.setLength(0);
		for (int i : nodes.getNeighbours(cc)) array.append(i).append(", ");
		
		System.out.println("incoming("+ccc+") = "+array.toString());
		
		
		
	}



	public GetRelatednessCLI(String[] args) {
		super(args, params, USAGE);
	}
}
