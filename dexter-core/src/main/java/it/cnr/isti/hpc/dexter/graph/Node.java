/**

 *  Copyright 2012 Diego Ceccarelli
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the license at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package it.cnr.isti.hpc.dexter.graph;

import it.cnr.isti.hpc.dexter.label.IdHelper;
import it.cnr.isti.hpc.dexter.label.IdHelperFactory;
import it.cnr.isti.hpc.io.reader.RecordParser;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Node implements a generic node in a entity-graph, represented as its node-id,
 * plus a set of incoming or outcoming nodes (the neighbours, represented as an array of integers)
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 05/lug/2012
 */
public class Node  {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(Node.class);

	int node;
	int[] neighbours;
	private static IdHelper ih;

	public Node() {
		
	}

	public Node(int node, int[] neighbours) {
		super();
		this.node = node;
		this.neighbours = neighbours;
	}

	/**
	 * @return the node
	 */
	public int getNode() {
		return node;
	}

	/**
	 * @param node
	 *            the node to set
	 */
	public void setNode(int node) {
		this.node = node;
	}

	/**
	 * @return the neighbours
	 */
	public int[] getNeighbours() {
		return neighbours;
	}

	/**
	 * @param neighbours
	 *            the neighbours to set
	 */
	public void setNeighbours(int[] neighbours) {
		this.neighbours = neighbours;
	}

	
	public List<String> getNeighbourNames(){
		if (ih == null)  ih = IdHelperFactory.getStdIdHelper();
		List<String> neighbourNames = new LinkedList<String>();
		for (int i : neighbours){
			String name = ih.getLabel(i);
			if (name.isEmpty()){
				logger.warn("no name for entity id {} ",i);
				continue;
			}
			neighbourNames.add(name);
			
		}
		Collections.sort(neighbourNames);
		return neighbourNames;
	}
	
	public static class Parser implements RecordParser<Node>{

		/* (non-Javadoc)
		 * @see it.cnr.isti.hpc.io.reader.RecordParser#decode(java.lang.String)
		 */
		public Node decode(String record) {
			Scanner sc = new Scanner(record).useDelimiter("[\t ]");
			int node = sc.nextInt();
			List<Integer> neighbours = new LinkedList<Integer>();
			while (sc.hasNextInt()) {
				neighbours.add(sc.nextInt());
			}
			int size = neighbours.size();
			int[] ngh = new int[size];
			for (int i = 0; i < size; i++) {
				ngh[i] = neighbours.get(i);
			}

			return new Node(node, ngh);
		}

		/* (non-Javadoc)
		 * @see it.cnr.isti.hpc.io.reader.RecordParser#encode(java.lang.Object)
		 */
		public String encode(Node obj) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	

}
