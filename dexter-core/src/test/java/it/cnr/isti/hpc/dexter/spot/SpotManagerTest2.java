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
package it.cnr.isti.hpc.dexter.spot;

import it.cnr.isti.hpc.io.IOUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Set;

import org.junit.Test;

/**
 * SpotManagerTest.java
 *
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it
 * created on 20/lug/2012
 */
public class SpotManagerTest2 {

	@Test
	public void test() throws IOException {
		BufferedReader br = IOUtils.getPlainOrCompressedReader("./src/test/resources/labels.gz");
		String spot = null;
		BufferedWriter writer = IOUtils.getPlainOrCompressedUTF8Writer("/tmp/spot-manager-test.txt");
		
		while ((spot = br.readLine())!= null){
			writer.write("spot: ["+spot+"]\n");
			Set<String> labels = SpotManager.getStandardSpotManager().process(spot);
			if (labels.isEmpty()) writer.write("DELETED\n");
			for (String l : labels){
				writer.write("["+l+"]\n");
			}
		}
		writer.close();
	}
		
	
	

}
