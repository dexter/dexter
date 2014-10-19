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

import it.cnr.isti.hpc.dexter.spot.Spot;
import it.cnr.isti.hpc.dexter.util.DexterParams;
import it.cnr.isti.hpc.io.IOUtils;
import it.cnr.isti.hpc.io.reader.RecordReader;
import it.cnr.isti.hpc.log.ProgressLogger;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Mar 8, 2013
 */
public class RamSpotFile {

	private static final Logger logger = LoggerFactory
			.getLogger(RamSpotFile.class);

	// MAX CHUNK SIZE (byte)
	private static int CHUNK_SIZE = 100000000;

	private static DexterParams params = DexterParams.getInstance();
	private final List<byte[]> chunks;
	private static RamSpotFile instance;

	private RamSpotFile() {
		chunks = new LinkedList<byte[]>();
		File binarySpotFile = params.getSpotsData();
		int i = 0;
		if (binarySpotFile.exists()) {
			// just to support the old format
			byte[] spotsData = load(binarySpotFile);
			chunks.add(i, spotsData);
			CHUNK_SIZE = Integer.MAX_VALUE;
			logger.info("loaded unique chunk {} : {}", i,
					binarySpotFile.getAbsolutePath());
		} else {
			File chunk = new File(binarySpotFile.getAbsolutePath() + "." + i);
			logger.info("loading chunk {} ", chunk.getAbsolutePath());
			while (chunk.exists()) {

				byte[] spotsData = load(chunk);
				chunks.add(i, spotsData);
				logger.info("loaded spot chunk {} : {}", i,
						chunk.getAbsolutePath());
				i++;
				chunk = new File(binarySpotFile.getAbsolutePath() + "." + i);
			}
		}

	}

	public static RamSpotFile getInstance() {
		if (instance == null)
			instance = new RamSpotFile();
		return instance;
	}

	public byte[] getOffset(long from, long to) {
		int fromchunkid = (int) from / CHUNK_SIZE;
		int tochunkid = (int) to / CHUNK_SIZE;
		int fromOffset = (int) from % CHUNK_SIZE;
		int toOffset = (int) to % CHUNK_SIZE;
		logger.info("chunks: [{},{}]", fromchunkid, tochunkid);
		logger.info("offset: [{},{}]", from, to);
		if (fromchunkid != tochunkid) {
			fromchunkid = tochunkid;
			fromOffset = 0;
		}
		logger.info("chunk: [{}]", fromchunkid);
		logger.info("offset: [{},{}]", from, to);
		byte[] spotsData = chunks.get(fromchunkid);
		return Arrays.copyOfRange(spotsData, fromOffset, toOffset);
	}

	public static void dumpSpotFile(String sortedSpotFile) {
		File binarySpotFile = params.getSpotsData();
		File offsetSpotFile = params.getSpotsOffsetData();
		dumpSpotFile(sortedSpotFile, binarySpotFile, offsetSpotFile);
	}

	public static void dumpSpotFile(String spotFile, File binarySpotFile,
			File offsetSpotFile) {
		RecordReader<Spot> reader = new RecordReader<Spot>(spotFile,
				new Spot.Parser());
		dumpSpotFile(reader, binarySpotFile, offsetSpotFile);
	}

	private static void dumpSpotFile(Iterable<Spot> spots, File output,
			File offsets) {
		long offset = 0;
		int currentChunk = 0;
		FileOutputStream outputWriter = null;
		ProgressLogger pl = new ProgressLogger("dumped {} spots", 10000);
		try {
			outputWriter = new FileOutputStream(output.getAbsolutePath() + "."
					+ currentChunk);
		} catch (FileNotFoundException e) {
			logger.error("opening spot repository file ({})", e.toString());
			System.exit(-1);
		}
		BufferedWriter offsetsWriter = IOUtils
				.getPlainOrCompressedWriter(offsets.getAbsolutePath());
		byte[] content;
		try {
			offsetsWriter.write(String.valueOf(offset));
			offsetsWriter.newLine();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (Spot s : spots) {
			int relativeOffset = (int) offset % CHUNK_SIZE;

			pl.up();
			try {

				content = s.toByteArray();
				// if there's not enought space for storing this content in the
				// current chunk,
				// put the final offset in the offset file and write a addvance
				// offset in the next
				// chunk
				if (relativeOffset + content.length > CHUNK_SIZE) {
					currentChunk++;

					outputWriter.close();
					outputWriter = new FileOutputStream(
							output.getAbsolutePath() + "." + currentChunk);
					offset += content.length + (CHUNK_SIZE - relativeOffset);
					offsetsWriter.write(String.valueOf(offset));
					offsetsWriter.newLine();
				} else {

					offset += content.length;
					offsetsWriter.write(String.valueOf(offset));
					offsetsWriter.newLine();
				}
				outputWriter.write(content);

			} catch (IOException e) {
				logger.error("writing spot repository ({})", e.toString());
				System.exit(-1);
			}
		}
		try {
			offsetsWriter.write(String.valueOf(offset));
		} catch (IOException e) {
			logger.error("writing spot repository ({})", e.toString());
			System.exit(-1);
		}
		try {
			offsetsWriter.close();
			outputWriter.close();
		} catch (IOException e) {
			logger.error("closing spot repository ({})", e.toString());
			System.exit(-1);
		}

	}

	public final static byte[] load(String fileName) {
		try {
			FileInputStream fin = new FileInputStream(fileName);
			return load(fin);
		} catch (Exception e) {

			return new byte[0];
		}
	}

	public final static byte[] load(File file) {
		try {
			FileInputStream fin = new FileInputStream(file);
			return load(fin);
		} catch (Exception e) {

			return new byte[0];
		}
	}

	public final static byte[] load(FileInputStream fin) {
		byte readBuf[] = new byte[512 * 1024];

		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();

			int readCnt = fin.read(readBuf);
			while (0 < readCnt) {
				bout.write(readBuf, 0, readCnt);
				readCnt = fin.read(readBuf);
			}

			fin.close();

			return bout.toByteArray();
		} catch (Exception e) {

			return new byte[0];
		}
	}

}
