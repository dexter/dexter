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
package it.cnr.isti.hpc.text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.Span;

/**
 * SentenceSegmenter allows to segment text in sentences.
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 08/giu/2012
 */
public class SentenceSegmenter {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(SentenceSegmenter.class);

	SentenceDetector sentenceDetector = null;

	private static SentenceSegmenter instance = null;

	private SentenceSegmenter() { 
		InputStream modelIn = null;
		try {
			// Loading sentence detection model
			modelIn = getClass().getResourceAsStream("/nlp/en-sent.bin");
			final SentenceModel sentenceModel = new SentenceModel(modelIn);
			modelIn.close();

			sentenceDetector = new SentenceDetectorME(sentenceModel);

		} catch (final IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (final IOException e) {
				} // oh well!
			}
		}
	}
	
	

	public static SentenceSegmenter getInstance() {
		if (instance == null)
			instance = new SentenceSegmenter();
		return instance;
	}

	public String[] split(String text) {
		return sentenceDetector.sentDetect(text);
	}

	public String[] split(Reader reader) {
		BufferedReader br = new BufferedReader(reader);
		String line = "";
		StringBuilder sb = new StringBuilder();
		try {
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			logger.error("reading the string to segment");
			System.exit(-1);
		}
		return sentenceDetector.sentDetect(sb.toString());
	}
	
	public List<Sentence> splitPos(String text){
		List<Sentence> sentences = new LinkedList<Sentence>();
		for (Span s : sentenceDetector.sentPosDetect(text)){
			sentences.add(new Sentence(s.getStart(), s.getEnd()));
			
		}
		return sentences;
	}

}
