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

import static org.junit.Assert.assertEquals;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.Pipe.OutputCollector;

import java.util.List;

import org.junit.Test;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 * Created on Sep 19, 2013
 */
public class PipeTest {

	@Test
	public void pipe() {
		Pipe<String> pipe = new Pipe<String>(new AppendOne());
		pipe = new Pipe<String>(pipe, new AppendOne());
		List<String> result = pipe.process("");
		assertEquals("11",result.get(0));
		
	}
	
	@Test
	public void pipe2() {
		Pipe<String> pipe = new Pipe<String>(new AppendOne());
		pipe = new Pipe<String>(pipe, new AppendTwo());
		List<String> result = pipe.process("");
		assertEquals("12",result.get(0));
		
	}
	
	
	public class AppendOne extends Function<String>{

		@Override
		protected void eval(String elem, OutputCollector collector) {
			collector.pushResult(elem+"1");
			
		}
		
	}
	
	public class AppendTwo extends Function<String>{

		@Override
		protected void eval(String elem, OutputCollector collector) {
			collector.pushResult(elem+"2");
			
		}
		
	}

}
