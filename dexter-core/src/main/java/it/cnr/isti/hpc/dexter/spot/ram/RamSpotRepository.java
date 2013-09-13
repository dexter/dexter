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
package it.cnr.isti.hpc.dexter.spot.ram;

import it.cnr.isti.hpc.benchmark.Stopwatch;
import it.cnr.isti.hpc.dexter.spot.Spot;
import it.cnr.isti.hpc.dexter.spot.repo.SpotRepository;
import it.cnr.isti.hpc.property.ProjectProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Mar 8, 2013
 */
public class RamSpotRepository implements SpotRepository {

	private static final Logger logger = LoggerFactory
			.getLogger(RamSpotRepository.class);
	

	RamSpotFile spots;
	SpotMinimalPerfectHash hash;
	SpotEliasFanoOffsets offsets;

	public RamSpotRepository() {
		hash = SpotMinimalPerfectHash.getInstance();
		offsets = SpotEliasFanoOffsets.getInstance();
		spots = RamSpotFile.getInstance();
	}

	// public RamSpotRepository(String spotBinFile, String spotBinOffsets,
	// String spotPerfectHash) {
	// Serializer serializer = new Serializer();
	// logger.info("loading spots binary data");
	// spotsData = load(spotBinFile);
	// logger.info("loading spots perfect hash");
	// hash =load(spotPerfectHash);
	// logger.info("loading spots offsets");
	//
	// }

	public Spot getSpot(String spot) {
		Stopwatch s = new Stopwatch();
		s.start("hash");
		long index = hash.hash(spot);
		s.stop("hash");
		// logger.info("index = {} ",index);
		s.start("offsets");
		long from = offsets.getOffset(index);
		long to = offsets.getOffset(index + 1);

		// logger.info("offsetStart = {} ",from);
		// logger.info("offsetEnd = {} ",to);
		byte[] binspot = spots.getOffset(from, to);
		s.stop("offsets");
		s.start("spot");
		Spot sp = Spot.fromByteArray(spot, binspot);
		s.stop("spot");
		// System.out.println("retrieved: "+s.stat());
		return sp;

	}

	public static void main(String[] args) {
		RamSpotRepository rs = new RamSpotRepository();
		Stopwatch s = new Stopwatch();
		s.start("spot");
		Spot spot = rs.getSpot("glass");
		s.stop("spot");
		// System.out.println(spot);
		// System.out.println("retrieved: "+s.stat("spot"));

	}

}
