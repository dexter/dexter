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

import it.cnr.isti.hpc.dexter.util.DexterParams;
import it.cnr.isti.hpc.io.Serializer;
import it.cnr.isti.hpc.io.reader.RecordReader;
import it.cnr.isti.hpc.io.reader.TsvRecordParser;
import it.cnr.isti.hpc.io.reader.TsvTuple;
import it.unimi.dsi.fastutil.longs.LongIterable;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.sux4j.util.EliasFanoMonotoneLongBigList;

import java.io.File;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Mar 8, 2013
 */
public class SpotEliasFanoOffsets {

	private static final Logger logger = LoggerFactory
			.getLogger(SpotEliasFanoOffsets.class);

	private final EliasFanoMonotoneLongBigList ef;

	private static DexterParams params = DexterParams.getInstance();

	private static SpotEliasFanoOffsets instance;

	private SpotEliasFanoOffsets() {
		Serializer serializer = new Serializer();
		File offsetsBinFile = params.getSpotsEliasFano();
		ef = (EliasFanoMonotoneLongBigList) serializer.load(offsetsBinFile
				.getAbsolutePath());

	}

	public static void dumpEliasFanoFile() {
		File offsetsFile = params.getSpotsOffsetData();
		File offsetsBinFile = params.getSpotsEliasFano();

		dumpEliasFanoFile(offsetsFile.getAbsolutePath(),
				offsetsBinFile.getAbsolutePath());
	}

	public long getOffset(long index) {
		return ef.getLong(index);
	}

	public static void dumpEliasFanoFile(String offsetsFile, String outputFile) {
		EliasFanoMonotoneLongBigList ef = new EliasFanoMonotoneLongBigList(
				new OffsetsFile(offsetsFile));

		Serializer serializer = new Serializer();
		logger.info("serializing EliasFano in {} ", outputFile);
		serializer.dump(ef, outputFile);

	}

	public static SpotEliasFanoOffsets getInstance() {
		if (instance == null)
			instance = new SpotEliasFanoOffsets();
		return instance;
	}

	public static class OffsetsFile implements LongIterable {

		private final String file;

		public OffsetsFile(String file) {
			this.file = file;

		}

		@Override
		public LongIterator iterator() {
			return new OffsetsFileIterator(file);
		}

	}

	public static class OffsetsFileIterator implements LongIterator {

		Iterator<TsvTuple> iterator;
		public static final String FIELD = "offset";

		public OffsetsFileIterator(String file) {
			RecordReader<TsvTuple> reader = new RecordReader<TsvTuple>(file,
					new TsvRecordParser(FIELD));
			iterator = reader.iterator();
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public Long next() {
			return Long.parseLong(iterator.next().get(FIELD));
		}

		@Override
		public void remove() {
			iterator.remove();

		}

		@Override
		public long nextLong() {
			return next();
		}

		@Override
		public int skip(int arg0) {
			throw new UnsupportedOperationException();
		}

	}

}
