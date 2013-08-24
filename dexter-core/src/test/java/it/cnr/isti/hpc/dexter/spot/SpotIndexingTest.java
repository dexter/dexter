///**
// *  Copyright 2012 Diego Ceccarelli
// *
// *  Licensed under the Apache License, Version 2.0 (the "License");
// *  you may not use this file except in compliance with the License.
// *  You may obtain a copy of the License at
// * 
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// *  Unless required by applicable law or agreed to in writing, software
// *  distributed under the License is distributed on an "AS IS" BASIS,
// *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *  See the License for the specific language governing permissions and
// *  limitations under the License.
// */
//package it.cnr.isti.hpc.dexter.spot;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
//import org.junit.Test;
//
///**
// * SpotReaderTest.java
// * 
// * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 01/ago/2012
// */
//public class SpotIndexingTest {
//
//	@Test
//	public void test() {
//		SpotReader reader = new SpotReader(
//				"./src/test/resources/spot-file-sample2.txt");
//		SpotMapFileWriter writer = new SpotMapFileWriter("/tmp/test");
//		Spot s = reader.next();
//		System.out.println("indexing: " + s);
//		writer.add(s.getSpot(), s.getSpot());
//		writer.close();
//		SpotMapFile r = new SpotMapFile("/tmp/test");	
//		assertTrue(r.contains(s.getSpot()));
//		assertEquals(s.getSpot(), r.get(s.getSpot()));
//		
//	}
//	
//	@Test
//	public void test2() {
//		SpotReader reader = new SpotReader(
//				"./src/test/resources/spot-file-sample.txt");
//		SpotMapFileWriter writer = new SpotMapFileWriter("/tmp/test");
//		Spot s = reader.next();
//		System.out.println("indexing: " + s);
//		writer.add(s.getSpot(), s.getEntities()+"\t"+s.getDocFreq());
//		writer.close();
//		SpotMapFile r = new SpotMapFile("/tmp/test");	
//		assertTrue(r.contains(s.getSpot()));
//		System.out.println(r.getSpot(s.getSpot()));		
//	}
//
//}
