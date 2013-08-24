///**
// *  Copyright 2011 Diego Ceccarelli
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
//package it.cnr.isti.hpc.dexter.cli.spot;
//
//import it.cnr.isti.hpc.cli.AbstractCommandLineInterface;
//import it.cnr.isti.hpc.dexter.spot.Spot;
//import it.cnr.isti.hpc.dexter.spot.SpotReader;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * print on the stout the spot file
// * 
// */
//public class SpotReaderCLI extends AbstractCommandLineInterface {
//	/**
//	 * Logger for this class
//	 */
//	private static final Logger logger = LoggerFactory
//			.getLogger(SpotReaderCLI.class);
//
//	private static String[] params = new String[] { INPUT };
//
//	private static final String USAGE = "java -cp $jar "
//			+ SpotReaderCLI.class
//			+ " -input spot-file";
//
//	public static void main(String[] args) {
//		SpotReaderCLI cli = new SpotReaderCLI(args);
//		String input = cli.getInput();
//		SpotReader reader = new SpotReader(input);
//		while ( reader.hasNext()){
//			Spot s = reader.next();
//			System.out.println(s.getSpot()+"\t"+s.get+"\t"+s.getDocFreq()+"\t"+s.getSpotProbability());
//			logger.debug("adding spot {}",s.getSpot());
//		}
//		logger.info("done");
//
//	}
//
//	public SpotReaderCLI(String[] args) {
//		super(args, params, USAGE);
//	}
//}
