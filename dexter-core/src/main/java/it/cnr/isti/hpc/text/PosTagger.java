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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;

/**
 * PosTagger allows to annotate text with pos tag. 
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 08/giu/2012
 */

// FIXME depends on language
public class PosTagger {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(PosTagger.class);

	private POSTaggerME posTagger = null;

	private static PosTagger instance = null;

	SentenceSegmenter ss;
	TokenSegmenter ts;

	private PosTagger() {

		ss = SentenceSegmenter.getInstance();
		ts = TokenSegmenter.getInstance();
		InputStream modelIn = null;
		try {
			// Loading tokenizer model
			modelIn = getClass().getResourceAsStream("/nlp/en-pos-maxent.bin");
			final POSModel posModel = new POSModel(modelIn);
			modelIn.close();

			posTagger = new POSTaggerME(posModel);

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

	public static PosTagger getInstance() {
		if (instance == null)
			instance = new PosTagger();
		return instance;
	}

	public List<PosToken> tag(String text) {
		List<PosToken> tokens = new ArrayList<PosToken>();
		
		if (text == null){
			logger.warn("text is null");
			return tokens;
		}
		
		if (text.isEmpty()){
			logger.warn("text is empty");
			return tokens;
		}
		String[] sentences = ss.split(text);
		for (String sentence : sentences) {
			String[] token = ts.tokenize(sentence);
			String[] tags = posTagger.tag(token);
			for (int i = 0; i < token.length; i++) {
				tokens.add(new PosToken(token[i], tags[i]));
			}
		}
		return tokens;
	}

	public List<PosToken> getVerbs(String text) {
		
		List<PosToken> tokens = new ArrayList<PosToken>();
		if (text == null){
			logger.warn("text is null");
			return tokens;
		}
		
		if (text.isEmpty()){
			logger.warn("text is empty");
			return tokens;
		}
		String[] sentences = ss.split(text);
		for (String sentence : sentences) {
			String[] token = ts.tokenize(sentence);
			String[] tags = posTagger.tag(token);

			for (int i = 0; i < token.length; i++) {
				PosToken t = new PosToken(token[i], tags[i]);
				if (t.isVerb()) {
					tokens.add(t);
				}
			}
		}
		return tokens;
	}
	
	

}
