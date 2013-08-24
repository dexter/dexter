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
//import static org.junit.Assert.*;
//
//import java.util.Arrays;
//import java.util.List;
//
//import org.junit.Test;
//
///**
// * ContextExtractorTest.java
// * 
// * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 24/lug/2012
// */
//public class ContextExtractorTest {
//
//	@Test
//	public void test() {
//		ContextExtractor extractor = new ContextExtractor(
//				"diego ceccarelli prova test pippo pluto belen");
//		extractor.setWindowSize(4);
//		assertEquals("diego ceccarelli prova test pippo",
//				extractor.getContext("prova"));
//		assertEquals("pippo pluto belen", extractor.getContext("belen"));
//		extractor = new ContextExtractor(
//				"diego ceccarelli prova test pippo pluto ceccarelli belen");
//		extractor.setWindowSize(2);
//		assertEquals("diego ceccarelli prova pluto ceccarelli belen",
//				extractor.getContext("ceccarelli"));
//	}
//
//	@Test
//	public void testBinSearch() {
//		ContextExtractor extractor = new ContextExtractor(
//				"diego ceccarelli prova test pippo pluto belen");
//		List<Integer> test = Arrays.asList(new Integer[] { 1, 5, 10, 50, 100,
//				110 });
//		assertEquals(0, extractor.closest(2, test));
//		assertEquals(0, extractor.closest(3, test));
//		assertEquals(0, extractor.closest(4, test));
//		assertEquals(1, extractor.closest(5, test));
//		assertEquals(4, extractor.closest(105, test));
//		assertEquals(5, extractor.closest(120, test));
//		assertEquals(3, extractor.closest(99, test));
//
//		test = Arrays.asList(new Integer[] { 0, 1 });
//
//		assertEquals(1, extractor.closest(10000, test));
//		assertEquals(0, extractor.closest(0, test));
//
//		test = Arrays.asList(new Integer[] { 0, 1 });
//
//		assertEquals(1, extractor.closest(10000, test));
//		assertEquals(0, extractor.closest(0, test));
//		test = Arrays.asList(new Integer[] { 1, 5, 10, 50, 100, 110, 1000 });
//		assertEquals(0, extractor.closest(2, test));
//		assertEquals(0, extractor.closest(3, test));
//		assertEquals(0, extractor.closest(4, test));
//		assertEquals(1, extractor.closest(5, test));
//		assertEquals(4, extractor.closest(105, test));
//		assertEquals(5, extractor.closest(120, test));
//
//		assertEquals(6, extractor.closest(1000, test));
//		assertEquals(6, extractor.closest(10000, test));
//
//	}
//
//}
