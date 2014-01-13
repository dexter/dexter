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
package it.cnr.isti.hpc.dexter.graph;

/**
 * Represents a collection of nodes in a graph, for each node (represented by an
 * integer id), can return addictional data (see @link{Node}) and a list of
 * neighbors.
 * 
 * @see IncomingNodes
 * @see OutcomingNodes
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it> Created on Oct 9,
 *         2012
 */
public interface NodeStar {

	public enum Direction {
		IN, OUT
	};

	/**
	 * return the neighbors of the the node id;
	 */
	public abstract int[] getNeighbours(int id);

	/** return a node description */
	public abstract Node getNode(int id);

	public abstract int size();

}
