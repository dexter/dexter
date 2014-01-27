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
import it.cnr.isti.hpc.dexter.util.DexterParams;
import it.cnr.isti.hpc.io.IOUtils;
import it.cnr.isti.hpc.io.Serializer;
import it.cnr.isti.hpc.io.reader.RecordReader;
import it.cnr.isti.hpc.io.reader.TsvRecordParser;
import it.cnr.isti.hpc.io.reader.TsvTuple;
import it.cnr.isti.hpc.log.ProgressLogger;
import it.unimi.dsi.bits.TransformationStrategies;
import it.unimi.dsi.sux4j.mph.MinimalPerfectHashFunction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Mar 8, 2013
 */
public class SpotMinimalPerfectHash {

	private static final Logger logger = LoggerFactory
			.getLogger(SpotMinimalPerfectHash.class);

	private MinimalPerfectHashFunction<String> hash;

	private static DexterParams params = DexterParams.getInstance();

	private static SpotMinimalPerfectHash instance = null;

	private SpotMinimalPerfectHash() {
		load();
	}

	public long hash(String spot) {
		return hash.getLong(spot);
	}

	public static SpotMinimalPerfectHash getInstance() {
		if (instance == null)
			instance = new SpotMinimalPerfectHash();
		return instance;
	}

	public void dumpKeys(String hashValuesFile) {
		File spotFile = params.getPlainSpots();

		dumpKeys(spotFile.getAbsolutePath(), hashValuesFile);
	}

	/**
	 * @param output
	 */
	private void dumpKeys(String spotsFile, String output) {
		SpotIterable reader = new SpotIterable(spotsFile);
		ProgressLogger pl = new ProgressLogger("dumped {} keys", 100000);
		BufferedWriter writer = IOUtils.getPlainOrCompressedWriter(output);
		for (String s : reader) {
			pl.up();
			try {
				writer.write(String.valueOf(hash.getLong(s)));
				writer.newLine();
			} catch (IOException e) {
				logger.error("writing in {} ({})", output, e.toString());
				System.exit(-1);
			}
		}
		try {
			writer.close();
		} catch (IOException e) {
			logger.error("closing {} ({})", output, e.toString());
			System.exit(-1);
		}

	}

	private static MinimalPerfectHashFunction<String> generateHash() {

		return generateHash(params.getPlainSpots());

	}

	private static MinimalPerfectHashFunction<String> generateHash(
			String spotFile) {
		return generateHash(new File(spotFile));

	}

	private static MinimalPerfectHashFunction<String> generateHash(File spotFile) {
		SpotIterable iterator = new SpotIterable(spotFile);
		MinimalPerfectHashFunction<String> mph = null;
		try {
			mph = new MinimalPerfectHashFunction<String>(iterator,
					TransformationStrategies.utf16());
		} catch (IOException e) {
			logger.error("generating minimal perfect hash ({}) ", e.toString());
			System.exit(-1);
		}
		return mph;
	}

	public static void dump() {


		dump(params.getSpotsPerfectHash());
	}

	private static void dump(String outputFile) {

		dump(new File(outputFile));

	}

	private static void dump(File outputFile) {
		logger.info("dump minimal perfect hashing in {} ", outputFile);
		MinimalPerfectHashFunction<String> mph = generateHash();

		Serializer serializer = new Serializer();
		serializer.dump(mph, outputFile.getAbsolutePath());
	}

	private void load() {
		load(params.getSpotsPerfectHash());
	}

	private void load(File file) {
		Serializer serializer = new Serializer();
		logger.info("loading minimal perfect hashing in {} ",
				file.getAbsolutePath());
		Stopwatch progress = new Stopwatch();
		progress.start("load");
		hash = (MinimalPerfectHashFunction<String>) serializer.load(file
				.getAbsolutePath());
		progress.stop("load");
		logger.info(progress.stat("load"));
	}

	public long getLong(String spot) {
		return hash.getLong(spot);
	}

	private static class SpotIterable implements Iterable<String> {

		File spotFile;

		public SpotIterable(String spotFile) {
			this(new File(spotFile));
		}

		public SpotIterable(File spotFile) {
			this.spotFile = spotFile;
		}

		@Override
		public Iterator<String> iterator() {
			return new SpotIterator(spotFile);
		}

	}

	private static class SpotIterator implements Iterator<String> {
		private final Iterator<TsvTuple> iterator;
		private static final String FIELD = "spot";

		public SpotIterator(File spotFile) {
			RecordReader<TsvTuple> reader = new RecordReader<TsvTuple>(
					spotFile.getAbsolutePath(), new TsvRecordParser(FIELD));
			iterator = reader.iterator();
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public String next() {
			return iterator.next().get(FIELD);
		}

		@Override
		public void remove() {
			iterator.remove();
		}

	}

}
