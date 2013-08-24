/**
 *  Copyright 2011 Diego Ceccarelli
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
package it.cnr.isti.hpc.dexter.cli.spot;

import it.cnr.isti.hpc.cli.AbstractCommandLineInterface;
import it.cnr.isti.hpc.dexter.spot.Spot;
import it.cnr.isti.hpc.dexter.spot.repo.SpotRepository;
import it.cnr.isti.hpc.dexter.spot.repo.SpotRepositoryFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create a file with the spot commonness
 * 
 */
public class GetSpotCommonnessCLI extends AbstractCommandLineInterface {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(GetSpotCommonnessCLI.class);

	
	private static String[] params = new String[] { "spot" };

	private static final String USAGE = "java -cp $jar " + GetSpotCommonnessCLI.class
			+ " -spot spot";

	/**
	 * @param args
	 */
	public GetSpotCommonnessCLI(String[] args) {
		super(args, params, USAGE);
}



	public static void main(String[] args) {
		GetSpotCommonnessCLI cli = new GetSpotCommonnessCLI(args);
		SpotRepositoryFactory factory = new SpotRepositoryFactory();
		SpotRepository spotRepo = factory.getStdInstance();
		Spot spot = spotRepo.getSpot(cli.getParam("spot"));
		System.out.println(spot);

//		SpotMapDB db = SpotMapDB.getInstance();
//		Stopwatch s = new Stopwatch();
//		s.start("mapdb");
//		Spot spot = db.getSpot(cli.getParam("spot"));
//		s.stop("mapdb");
//		System.out.println(spot);
//		SpotMapFile mapfile = new SpotMapFile();
//		s.start("mapfile");
//		spot = mapfile.getSpot(cli.getParam("spot"));
//		s.stop("mapfile");
//		System.out.println(spot);
//		
//		
//		
//		System.out.println(s.stat());
		

	}
}
