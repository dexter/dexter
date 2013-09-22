/**
 *  Copyright 2013 Diego Ceccarelli
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

/**
 *  Copyright 2013 Diego Ceccarelli
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
package it.cnr.isti.hpc.dexter.spot.cleanpipe;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Class pipe allows to create a chain of functions manipulating T objects. 
 *  
 * 
 * Pipes are chained together through their constructors.
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Sep 18, 2013
 */
public class Pipe<T> {

	private Pipe<T> next;

	public LinkedList<T> output;

	private Function<T> fun;

	private Pipe<T> head;

	private OutputCollector collector;

	public Pipe(Function<T> fun) {
		this.fun = fun;
		output = new LinkedList<T>();
		collector = new OutputCollector();
		head = this;
	}

	public Pipe(Pipe<T> previous, Function<T> fun) {
		this(fun);
		previous.next = this;
		head = previous.getHead();
	}

	private Pipe<T> getHead() {
		return head;
	}

	private Pipe<T> getNext() {
		return next;
	}

	private Iterator<T> getOutput() {
		return output.iterator();
	}

	private void clearOutput() {
		output.clear();
	}

	protected List<T> getResults() {
		if (output.isEmpty())
			return Collections.emptyList();
		List<T> list = output;
		output = new LinkedList<T>();
		return list;
	}

	/**
	 * Performs all the pipeline over the object elem, and returns one 
	 * or multiple manipulations of the object elem.
	 */
	public List<T> process(T elem) {
		Pipe<T> p = head;
		List<T> elems = new LinkedList<T>();
		p.fun.eval(elem, p.collector);
		while (p.getNext() != null) {

			Iterator<T> iter = p.getOutput();
			elems.clear();
			while (iter.hasNext()) {
				elems.add(iter.next());
			}
			p.clearOutput();

			p = p.getNext();
			for (T t : elems) {
				p.fun.eval(t, p.collector);
			}
		}

		return getResults();
	}

	protected void pushResult(T elem) {
		output.add(elem);
	}

	public class OutputCollector {

		private OutputCollector() {

		}

		public void pushResult(T elem) {
			output.add(elem);
		}
	}

}
