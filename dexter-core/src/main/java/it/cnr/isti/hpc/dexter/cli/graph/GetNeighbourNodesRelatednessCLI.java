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
import it.cnr.isti.hpc.dexter.graph.Node;
import it.cnr.isti.hpc.dexter.graph.NodeFactory;
import it.cnr.isti.hpc.dexter.graph.NodeStar;
import it.cnr.isti.hpc.dexter.hash.IdHelper;
import it.cnr.isti.hpc.dexter.hash.IdHelperFactory;
import it.cnr.isti.hpc.dexter.relatedness.Relatedness;
import it.cnr.isti.hpc.dexter.relatedness.RelatednessFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 21/nov/2011
 */
public class GetNeighbourNodesRelatednessCLI extends AbstractCommandLineInterface {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(GetNeighbourNodesRelatednessCLI.class);


	private static String[] params = new String[] { "input" , "direction"};

	private static final String USAGE = "java -cp $jar "
			+ GetNeighbourNodesRelatednessCLI.class + " -input id/label -direction in/out";

	public static void main(String[] args) {
		GetNeighbourNodesRelatednessCLI cli = new GetNeighbourNodesRelatednessCLI(args);
		
		String direction = cli.getParam("direction");
		NodeStar sn = null;
		if (direction.equals("in")){
			sn = NodeFactory.getIncomingNodes(NodeFactory.STD_TYPE);
		} else if (direction.equals("out")){
			sn = NodeFactory.getOutcomingNodes(NodeFactory.STD_TYPE);
		} else {
			logger.error("invalid direction (in or out)");
			System.exit(-1);
		}
		IdHelper ih = IdHelperFactory.getStdIdHelper();
		String title = cli.getInput();
		String name = "";
		int id = -1;
		try {
			id = Integer.parseInt(title);
			name = ih.getLabel(id);
			if (name.isEmpty()){
				logger.error("no entity with name {}",id);
				System.exit(-1);
			}
		} catch (Exception e){
			// i know is ugly.. but it works
			// so we are here, then title is a string
			id = ih.getId(title);
			if (id == IdHelper.NOID){
				logger.error("no entity with name {}",title);
				System.exit(-1);
			}
			name = title;
		}
		Node n = sn.getNode(id);
		System.out.println(direction+" nodes: "+name);
		RelatednessFactory relFact = new RelatednessFactory(RelatednessFactory.STD);
		List<Relatedness> rel = new ArrayList<Relatedness>();
		for (int i : n.getNeighbours()){
			rel.add(relFact.getRelatedness(id, i));
		}
		Collections.sort(rel);
		for (Relatedness r : rel){
			System.out.println(r.getNames());
		}
		

	} 
	
	

	public GetNeighbourNodesRelatednessCLI(String[] args) {
		super(args, params, USAGE);
	}
 }
