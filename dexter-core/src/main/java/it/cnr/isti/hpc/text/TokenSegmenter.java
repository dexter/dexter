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

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

/**
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 08/giu/2012
 */
public class TokenSegmenter {

	private static Tokenizer tokenizer = null;
	private static TokenSegmenter instance = null;

	private TokenSegmenter() {
		InputStream modelIn = null;
		try {
			// Loading tokenizer model
			modelIn = getClass().getResourceAsStream("/nlp/en-token.bin");
			final TokenizerModel tokenModel = new TokenizerModel(modelIn);
			modelIn.close();

			tokenizer = new TokenizerME(tokenModel);

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

	public static TokenSegmenter getInstance() {
		if (instance == null)
			instance = new TokenSegmenter();
		return instance;
	}

	public String[] tokenize(String sentence) {
		return tokenizer.tokenize(sentence);
	}

	public List<Token> tokenizePos(String sentence) {
		Span[] spans = tokenizer.tokenizePos(sentence);
		List<Token> tokens = new LinkedList<Token>();
		for (Span s : spans) {
			tokens.add(new Token(s.getStart(), s.getEnd()));
		}
		return tokens;
	}

}
