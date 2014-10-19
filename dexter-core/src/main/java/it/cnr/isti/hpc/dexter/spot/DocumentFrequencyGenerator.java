/**
 *  Copyright 2014 Diego Ceccarelli
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
package it.cnr.isti.hpc.dexter.spot;

import it.cnr.isti.hpc.benchmark.Stopwatch;
import it.cnr.isti.hpc.dexter.analysis.DexterAnalyzer;
import it.cnr.isti.hpc.dexter.analysis.DexterAnalyzer.ArticleIterator;
import it.cnr.isti.hpc.io.Serializer;
import it.cnr.isti.hpc.log.ProgressLogger;
import it.cnr.isti.hpc.wikipedia.article.Article;
import it.unimi.dsi.util.BloomFilter;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Aug 8, 2014
 */
public class DocumentFrequencyGenerator {

	BloomFilter<Void> bf = BloomFilter.create(10000000L);
	DexterAnalyzer analyzer = new DexterAnalyzer();

	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(DocumentFrequencyGenerator.class);

	public DocumentFrequencyGenerator(Iterator<String> spotSrcTargetIterator) {
		File bloom = new File("/tmp/bf.bin");
		if (!bloom.exists()) {
			initBloomFilter(spotSrcTargetIterator);
			Serializer serializer = new Serializer();
			logger.info("dump bloom filter in {}", bloom.getAbsolutePath());
			serializer.dump(bf, bloom.getAbsolutePath());
		} else {
			Serializer serializer = new Serializer();
			logger.info("load bloom filter in {}", bloom.getAbsolutePath());
			bf = (BloomFilter<Void>) serializer.load(bloom.getAbsolutePath());
		}
	}

	private void initBloomFilter(Iterator<String> spotIterator) {
		String spot = spotIterator.next();
		analyzer.setShingles(false);

		ProgressLogger pl = new ProgressLogger(
				"added {} spots to the bloom filter", 100000);
		pl.up();
		while (spotIterator.hasNext()) {
			String next = spotIterator.next();
			if (next.equals(spot))
				continue;
			pl.up();
			spot = next;
			TokenStream ts = null;
			try {
				ts = analyzer.tokenStream("content", new StringReader(spot));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			CharTermAttribute termAtt = ts
					.addAttribute(CharTermAttribute.class);
			try {
				ts.reset();

				if (ts.incrementToken()) {
					spot = termAtt.toString();
					bf.add(spot);

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	Stopwatch watch = new Stopwatch();
	ArticleIterator iterator = new DexterAnalyzer.ArticleIterator();
	Multiset<String> freqs = HashMultiset.create();

	public Multiset<String> getSpotsAndFrequencies(Article a) {
		freqs.clear();
		try {
			iterator.setArticle(a);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (iterator.hasNext()) {
			String key = iterator.next();
			if (bf.contains(key)) {
				freqs.add(key);
			}

		}
		return freqs;

	}

}
