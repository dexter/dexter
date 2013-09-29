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

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 * Created on Oct 10, 2012
 */
public class RelatednessTest {

	@Test
	public void testIntersection() {
		int[] a = new int[] { 1,3,5,10,12};
		int[] b = new int[] {1,12,20};
		int[] inter = new int[] {1,12};
		int[] union = new int[] {1,3,5,10,12,20};
		
		Assert.assertArrayEquals(inter, Relatedness.intersection(a, b, 5, 3));
		Assert.assertArrayEquals(union, Relatedness.sortedunion(a, b, 5, 3));
		
	}

}
