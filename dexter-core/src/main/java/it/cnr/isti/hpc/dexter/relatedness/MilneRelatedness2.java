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
package it.cnr.isti.hpc.dexter.relatedness;

import it.cnr.isti.hpc.dexter.graph.IncomingNodes;
import it.cnr.isti.hpc.dexter.graph.NodeFactory;

/**
 * Implements the standard relatedness function proposed by Milne and Witten
 * [1].
 * 
 * <br>
 * <br>
 * [1] Learning to link with wikipedia, Milne, David and Witten, Ian H,
 * Proceedings of the 17th ACM conference on Information and knowledge
 * management 2008
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Oct 12, 2013
 */
public class MilneRelatedness2 extends Relatedness {

	private static IncomingNodes in = NodeFactory
			.getIncomingNodes(NodeFactory.STD_TYPE);

	private static final int W = in.size();
	private static final double logW = Math.log(W);

	public MilneRelatedness2() {

	}

	protected MilneRelatedness2(int x, int y) {
		super(x, y);

	}

	@Override
	protected double score() {
		int[] inX = in.getNeighbours(x);
		int[] inY = in.getNeighbours(y);
		int sizex = inX.length;
		int sizey = inY.length;

		int maxXY = Math.max(sizex, sizey);
		int minXY = Math.min(sizex, sizey);
		if (minXY == 0)
			return 0;

		int intersection = intersectionSize(inX, inY);
		if (intersection <= 1)
			return 0;
		double rel = 1 - ((Math.log(maxXY) - Math.log(intersection)) / (logW - Math
				.log(minXY)));
		if (rel < 0)
			rel = 0;
		return rel;

	}

	public int intersectionSize(int[] a, int[] b) {
		int i = 0, j = 0;
		int size = 0;
		int aSize = a.length;
		int bSize = b.length;
		while ((i < aSize) && (j < bSize)) {
			if (a[i] < b[j]) {
				i++;
				continue;
			}
			if (a[i] > b[j]) {
				j++;
				continue;
			}
			// => (a[i] == a[j])
			size++;
			i++;
			j++;

		}
		return size;
	}

	@Override
	public String getName() {
		return "milne";
	}

	@Override
	public Relatedness copy() {
		MilneRelatedness2 rel = new MilneRelatedness2(x, y);
		rel.setScore(score);
		return rel;
	}

	@Override
	public boolean hasNegativeScores() {
		return false;
	}

}
