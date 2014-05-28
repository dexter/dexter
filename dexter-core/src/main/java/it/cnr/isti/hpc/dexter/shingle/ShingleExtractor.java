/**
s *  Copyright 2012 Salvatore Trani
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
package it.cnr.isti.hpc.dexter.shingle;

import it.cnr.isti.hpc.dexter.spot.clean.SpotManager;
import it.cnr.isti.hpc.text.Sentence;
import it.cnr.isti.hpc.text.SentenceSegmenter;
import it.cnr.isti.hpc.text.Token;
import it.cnr.isti.hpc.text.TokenSegmenter;
import it.cnr.isti.hpc.wikipedia.article.Article;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ShingleExtractor extracts all the ngrams (of fixed length) from the text of
 * an article. Please observe that the ShingleExtractor perform a cleaning step
 * over each shingle using the {@link SpotManager standard spot cleaner}. Each
 * shingle produced could be different but the 'original' fragment, but it
 * contains the original start/end position in the original test.
 * 
 * 
 * @see Shingle
 * @see SpotManager
 * @author Salvatore Trani, salvatore.trani@isti.cnr.it created on 02/aug/2012
 */
public class ShingleExtractor implements Iterable<Shingle> {

	private static final Logger logger = LoggerFactory
			.getLogger(ShingleExtractor.class);
	private int maxShingleSize;

	private static final int DEFAULT_MAX_SHINGLE_SIZE = 6;

	private final List<List<Token>> cleanedSentences;
	private static SpotManager sm;
	private static TokenSegmenter ts;
	private static SentenceSegmenter ss;

	static {
		// sm = new SpotManager();
		sm = SpotManager.getStandardSpotCleaner();
		ts = TokenSegmenter.getInstance();
		ss = SentenceSegmenter.getInstance();

		// sm.add(new LowerCaseCleaner());
		// sm.add(new UnicodeCleaner());
		// // sm.add(new ParenthesesCleaner());
		// sm.add(new QuotesCleaner());
		// sm.add(new UnderscoreCleaner());
		// // sm.add(new JuniorAndInitialsCleaner());
		// sm.add(new StripCleaner());
		// // sm.add(new TypeCleaner());
		// }
	}

	private ShingleExtractor() {
		cleanedSentences = new ArrayList<List<Token>>();
		maxShingleSize = DEFAULT_MAX_SHINGLE_SIZE;
	}

	public ShingleExtractor(Article a) {
		this();
		for (String p : a.getParagraphs()) {
			addText(p);
		}
	}

	// public ShingleExtractor(Document doc) {
	// this(doc.getMention());
	// }

	public ShingleExtractor(String text) {
		this();
		addText(text);
	}

	private void addText(String text) {

		List<Sentence> sentences = ss.splitPos(text);

		// int start = 0;
		for (Sentence sentence : sentences) {
			String currSentence = text.substring(sentence.getStart(),
					sentence.getEnd());
			int startSentence = sentence.getStart();

			// System.out.println("SENTENCE [" + currSentence + "]");
			// //List<Token> textShingles = new LinkedList<Token>();
			// FIXME CLEAN SHOULD NO CHANGE THE OFFSETS OF THE TOKENS
			// currSentence = sm.clean(currSentence);
			List<Token> tokens = ts.tokenizePos(currSentence);
			List<Token> cleanTokens = new LinkedList<Token>();
			// experimental
			for (Token t : tokens) {
				t.setStart(t.getStart() + startSentence);
				t.setEnd(t.getEnd() + startSentence);
				String token = text.substring(t.getStart(), t.getEnd());
				String cleanToken = sm.clean(token);
				// System.out.println(token + "-> " + cleanToken);
				// System.out.println("token in text: " + token);

				// Skip empty token (or tokens made only of chars cleaned
				// above)
				if (cleanToken.isEmpty())
					continue;

				t.setText(cleanToken);
				cleanTokens.add(t);
			}
			cleanedSentences.add(cleanTokens);
		}

	}

	public int getMaxShingleSize() {
		return maxShingleSize;
	}

	@Override
	public Iterator<Shingle> iterator() {
		return new ShingleIterator();
	}

	public void setMaxShingleSize(int maxShingleSize) {
		this.maxShingleSize = maxShingleSize;
	}

	private class ShingleIterator implements Iterator<Shingle> {
		private int currentPos;
		private int currentSen;
		private final Deque<Shingle> tempContainer;

		public ShingleIterator() {
			currentPos = 0;
			currentSen = 0;
			tempContainer = new ArrayDeque<Shingle>();
		}

		private void generateNextShingles() {
			if (currentSen >= cleanedSentences.size())
				return;

			List<Token> currentSentence = cleanedSentences.get(currentSen);
			// Generate the shingles of size (1 .. maxShingleSize) starting at
			// currentPos
			for (int sz = 1; sz <= maxShingleSize
					&& currentPos + sz <= currentSentence.size(); sz++) {
				tempContainer.add(new Shingle(currentSentence.subList(
						currentPos, currentPos + sz)));
			}

			if (++currentPos >= currentSentence.size()) {
				currentSen++;
				currentPos = 0;
			}
		}

		@Override
		public boolean hasNext() {
			if (tempContainer.size() == 0)
				generateNextShingles();
			return tempContainer.size() > 0;
		}

		@Override
		public Shingle next() {
			if (hasNext())
				return tempContainer.pop();
			else
				throw new NoSuchElementException();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

}
