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
import it.cnr.isti.hpc.dexter.graph.OutcomingNodes;
import it.cnr.isti.hpc.dexter.label.IdHelper;
import it.cnr.isti.hpc.dexter.label.IdHelperFactory;
import it.cnr.isti.hpc.dexter.util.DexterParams;
import it.cnr.isti.hpc.structure.LRUCache;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

/**
 * Contains the relatedness between two entities, and several functions on the
 * graph useful to implement a relatedness function.
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Oct 10, 2012
 */
public abstract class Relatedness implements Comparable<Relatedness> {

	protected int x;
	protected int y;
	protected double score;

	private static DexterParams params = DexterParams.getInstance();
	private static IntList list = new IntArrayList();

	private IncomingNodes in;// =
								// NodeFactory.getIncomingNodes(NodeFactory.STD_TYPE);
	private OutcomingNodes out; // =
								// NodeFactory.getOutcomingNodes(NodeFactory.STD_TYPE);

	private static final int CACHE_SIZE = params.getCacheSize("relatedness");
	private final static boolean CACHE_ENABLED = CACHE_SIZE > 0;
	private static LRUCache<Couple, Double> cache = new LRUCache<Couple, Double>(
			CACHE_SIZE);

	public Relatedness() {
		super();
	}

	protected Relatedness(int x, int y) {
		this();
		set(x, y);

	}

	public void set(int x, int y) {
		this.x = x;
		this.y = y;
		Couple k = new Couple(x, y);
		if (CACHE_ENABLED && cache.containsKey(k)) {
			score = cache.get(k);
			return;
		}
		score = score();
		if (CACHE_ENABLED)
			cache.put(k, score);
	}

	public void setScore(double score) {
		this.score = score;
	}

	protected abstract double score();

	@Override
	public int compareTo(Relatedness r) {
		if (this.equals(r))
			return 0;
		if (r.getScore() > score)
			return 1;
		return -1;
	}

	public abstract String getName();

	public boolean hasNegativeScores() {
		return false;
	}

	public abstract Relatedness copy();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Relatedness other = (Relatedness) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	public double getScore() {
		return score;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x
	 *            the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y
	 *            the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}

	public int[] outX() {
		return in.getNeighbours(x);
	}

	public int[] outY() {
		return in.getNeighbours(y);
	}

	public int[] inIntersection() {
		int[] inX = outX();
		int[] inY = outY();
		return intersection(inX, inY, inX.length, inY.length);

	}

	public int[] outIntersection() {
		int[] inX = outX();
		int[] inY = outY();
		return intersection(inX, inY, inX.length, inY.length);
	}

	public int[] inoutX() {
		int[] inX = outX();
		int[] outX = outX();
		return sortedunion(inX, outX, inX.length, outX.length);

	}

	public int[] inoutY() {
		int[] inY = outY();
		int[] outY = outY();
		int[] u = sortedunion(inY, outY, inY.length, outY.length);
		return removeDuplicates(u);

	}

	private static int[] removeDuplicates(int[] array) {
		list.clear();
		int previous = -1;
		for (int i : array) {
			if (i > previous)
				list.add(i);
		}
		return list.toIntArray();
	}

	public static int[] sortedunion(int[] a, int[] b, int aSize, int bSize) {
		int i = 0, j = 0;
		list.clear();

		while ((i < aSize) && (j < bSize)) {
			if (a[i] < b[j]) {
				list.add(a[i]);
				i++;
				continue;
			}
			if (a[i] > b[j]) {
				list.add(b[j]);
				j++;
				continue;
			}
			// => (a[i] == a[j])
			list.add(a[i]);
			i++;
			j++;
		}
		while (i < aSize) {
			list.add(a[i++]);
		}
		while (j < bSize) {
			list.add(b[j++]);
		}
		return list.toIntArray();
	}

	public static int[] intersection(int[] a, int[] b, int aSize, int bSize) {
		int i = 0, j = 0;
		list.clear();
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
			list.add(a[i]);
			i++;
			j++;

		}
		return list.toIntArray();
	}

	public static int intersectionSize(int[] a, int[] b, int aSize, int bSize) {
		int i = 0, j = 0;
		int size = 0;
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

	public static int unionSize(int[] a, int[] b, int aSize, int bSize) {
		int i = 0, j = 0;
		int size = 0;
		while ((i < aSize) && (j < bSize)) {
			if (a[i] < b[j]) {
				size++;
				i++;
				continue;
			}
			if (a[i] > b[j]) {
				size++;
				j++;
				continue;
			}
			// => (a[i] == a[j])
			size++;
			i++;
			j++;
		}
		while (i < aSize) {
			size++;
		}
		while (j < bSize) {
			size++;
		}
		return size;
	}

	@Override
	public String toString() {
		return "<" + x + "," + y + "> \t" + score;
	}

	public String getNames() {
		IdHelper ih = IdHelperFactory.getStdIdHelper();
		String xStr = ih.getLabel(x);
		String yStr = ih.getLabel(y);
		return "rel:" + score + "\t[" + xStr + "] [" + yStr + "]>";
	}

	private class Couple {
		int x;
		int y;

		public Couple(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Couple other = (Couple) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}

		private Relatedness getOuterType() {
			return Relatedness.this;
		}

	}

}
