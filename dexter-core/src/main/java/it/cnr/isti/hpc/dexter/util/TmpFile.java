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
package it.cnr.isti.hpc.dexter.util;

import it.cnr.isti.hpc.io.IOUtils;
import it.cnr.isti.hpc.property.ProjectProperties;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RedirectsMap.java
 *
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it
 * created on 05/lug/2012
 */
public class TmpFile {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(TmpFile.class);
	
	File tempFile;
	
	
	public TmpFile(String name){
		ProjectProperties properties = new ProjectProperties(this.getClass());
		File dir = new File(properties.get("tmp.dir"));
		if (! dir.exists()) dir.mkdir();
		try {
			tempFile = File.createTempFile(name, ".tmp", dir);
		} catch (IOException e) {
			logger.error("creating a temp file");
			System.exit(-1);
		}
		logger.info("created tmp file {}",tempFile);
	}
	
	public void destroy(){
		logger.info("deleted tmp file {}",tempFile);
		tempFile.delete();
	}
	
	public Writer open(){
		return IOUtils.getPlainOrCompressedWriter(tempFile.getAbsolutePath());
	}
	
	public BufferedReader read(){
		return IOUtils.getPlainOrCompressedReader(tempFile.getAbsolutePath());
		
	}
	
	

}
