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
import it.cnr.isti.hpc.io.IOUtils;
import it.cnr.isti.hpc.io.reader.RecordReader;
import it.cnr.isti.hpc.log.ProgressLogger;
import it.cnr.isti.hpc.property.ProjectProperties;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Mar 8, 2013
 */
public class RamSpotFile  {

	private static final Logger logger = LoggerFactory
			.getLogger(RamSpotFile.class);

	private static ProjectProperties properties = new ProjectProperties(RamSpotFile.class);
	
	private byte[] spotsData;
	private static RamSpotFile instance;
	

	private RamSpotFile() {
		String binarySpotFile = properties.get("ram.spot.data.bin");
		spotsData = load(binarySpotFile);
		 
		
	}
	
	
	public static RamSpotFile getInstance(){
		if (instance == null) instance = new RamSpotFile();
		return instance;
	}
	
	public byte[] getOffset(long from, long to){
		return Arrays.copyOfRange (spotsData, (int) from, (int)to);
	}
	


	public static void dumpSpotFile(String sortedSpotFile){
		String binarySpotFile = properties.get("ram.spot.data.bin");
		String offsetSpotFile = properties.get("ram.spot.offsets");
		dumpSpotFile(sortedSpotFile, binarySpotFile, offsetSpotFile);
	}

	
	private static void dumpSpotFile(String spotFile, String binarySpotFile,
			String offsetSpotFile) {
		RecordReader<Spot> reader = new RecordReader<Spot>(spotFile,
				new Spot.Parser());
		dumpSpotFile(reader, new File(binarySpotFile), new File(offsetSpotFile));
	}

	private static void dumpSpotFile(Iterable<Spot> spots, File output,
			File offsets) {
		long offset = 0;
		FileOutputStream outputWriter = null;
		ProgressLogger pl = new ProgressLogger("dumped {} spots", 10000);
		try {
			outputWriter = new FileOutputStream(output.getAbsolutePath());
		} catch (FileNotFoundException e) {
			logger.error("opening spot repository file ({})", e.toString());
			System.exit(-1);
		}
		BufferedWriter offsetsWriter = IOUtils
				.getPlainOrCompressedWriter(offsets.getAbsolutePath());
		byte[] content;
		for (Spot s : spots) {
			pl.up();
			try {
				offsetsWriter.write(String.valueOf(offset));
				offsetsWriter.newLine();
				content = s.toByteArray();
				offset += content.length;
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
	
	public final static byte[] load(String fileName)
	  {
	    try { 
	      FileInputStream fin=new FileInputStream(fileName);
	      return load(fin);
	    }
	    catch (Exception e) {
	 
	      return new byte[0];
	    }
	  }

	  public final static byte[] load(File file)
	  {
	    try { 
	      FileInputStream fin=new FileInputStream(file);
	      return load(fin);
	    }
	    catch (Exception e) {
	     
	      return new byte[0];
	    }
	  }

	  public final static byte[] load(FileInputStream fin)
	  {
	    byte readBuf[] = new byte[512*1024];
	  
	    try { 
	      ByteArrayOutputStream bout = new ByteArrayOutputStream();
	    
	      int readCnt = fin.read(readBuf);
	      while (0 < readCnt) {
	        bout.write(readBuf, 0, readCnt);
	        readCnt = fin.read(readBuf);
	      }
	      
	      fin.close();
	      
	      return bout.toByteArray();
	    }
	    catch (Exception e) {
	     
	      return new byte[0];
	    }
	  }

	
}
