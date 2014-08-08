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

import it.cnr.isti.hpc.dexter.shingle.Shingle;
import it.cnr.isti.hpc.dexter.shingle.ShingleExtractor;
import it.cnr.isti.hpc.dexter.spot.SpotReader.SpotSrcTarget;
import it.cnr.isti.hpc.dexter.spot.clean.SpotManager;
import it.cnr.isti.hpc.log.ProgressLogger;
import it.cnr.isti.hpc.wikipedia.article.Article;
import it.unimi.dsi.util.BloomFilter;

import java.util.Iterator;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Aug 8, 2014
 */
public class DocumentFrequencyGenerator {

	BloomFilter bf = BloomFilter.create(12000000L);
	SpotManager spotManager = SpotManager.getStandardSpotManager();

	public DocumentFrequencyGenerator(
			Iterator<SpotSrcTarget> spotSrcTargetIterator) {

		initBloomFilter(spotSrcTargetIterator);
	}

	private void initBloomFilter(Iterator<SpotSrcTarget> spotSrcTargetIterator) {
		SpotSrcTarget first = spotSrcTargetIterator.next();
		String spot = first.getSpot();
		bf.add(spot);
		ProgressLogger pl = new ProgressLogger(
				"added {} spots to the bloom filter", 100000);
		pl.up();
		while (spotSrcTargetIterator.hasNext()) {
			String next = spotSrcTargetIterator.next().getSpot();
			if (next.equals(spot))
				continue;
			pl.up();
			spot = next;
			bf.add(spot);
		}

	}

	public Multiset<String> getSpotsAndFrequencies(Article a) {
		Multiset<String> freqs = HashMultiset.create();
		ShingleExtractor se = new ShingleExtractor(a);

		for (Shingle shingle : se) {
			for (String s : spotManager.process(shingle.getText())) {
				if (bf.contains(s)) {
					freqs.add(s);
				}
			}
		}
		return freqs;

	}

}
