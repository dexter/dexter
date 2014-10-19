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
package it.cnr.isti.hpc.dexter.spot.clean;

import it.cnr.isti.hpc.dexter.spot.cleanpipe.Function;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.NopFunction;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.Pipe;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.cleaner.Cleaner;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.cleaner.HtmlCleaner;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.cleaner.JuniorAndInitialsCleaner;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.cleaner.LowerCaseCleaner;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.cleaner.ParenthesesCleaner;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.cleaner.QuotesCleaner;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.cleaner.StripCleaner;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.cleaner.SymbolCleaner;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.cleaner.TypeCleaner;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.cleaner.UnicodeCleaner;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.filter.AsciiFilter;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.filter.ImageFilter;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.filter.LengthFilter;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.filter.LongSpotFilter;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.filter.SymbolFilter;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.filter.TemplateFilter;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.mapper.CityMapper;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.mapper.QuotesMapper;
import it.cnr.isti.hpc.wikipedia.article.Article;
import it.cnr.isti.hpc.wikipedia.article.Link;

import java.util.HashSet;
import java.util.Set;

/**
 * A SpotManager takes care of cleaning the anchor texts extracted from the
 * Wikipedia articles in order to produce a dictionary of spots. Each anchor
 * text is processed over a pipeline of functions that could filter out it,
 * clean it, or generate several different variations of the text.
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 20/lug/2012
 */
public class SpotManager {

	private Pipe<String> pipe;
	private Pipe<String> cleanPipe;

	// private final SpotManager standardSpotManager = null;
	// private final SpotManager standardSpotCleaner = null;

	/**
	 * Uses the standard cleaner to clean a given text
	 */
	public static String cleanText(String text) {
		SpotManager sm = getStandardSpotCleaner();
		return sm.clean(text);
	}

	/**
	 * Generates a new pipeline
	 */
	public SpotManager() {
		this.pipe = new Pipe<String>(new NopFunction<String>());
		this.cleanPipe = new Pipe<String>(new NopFunction<String>());

	}

	/**
	 * Adds a new function to the pipeline
	 */
	public void add(Function<String> fun) {
		this.pipe = new Pipe<String>(this.pipe, fun);
		if (fun instanceof Cleaner) {
			this.cleanPipe = new Pipe<String>(this.cleanPipe, fun);
		}
	}

	/**
	 * Creates a spot manager performing the cleaning described in the given
	 * pipe.
	 * 
	 * @param pipe
	 *            - a pipe containing all the cleaning functions to apply to an
	 *            anchor.
	 * 
	 */
	public SpotManager(Pipe<String> pipe) {
		this.pipe = pipe;

	}

	/**
	 * Returns a StandardSpotManager used by Dexter to process the anchors.
	 */
	public static SpotManager getStandardSpotManager() {
		SpotManager standardSpotManager;

		standardSpotManager = new SpotManager();
		// pre filter
		standardSpotManager.add(new SymbolFilter());
		standardSpotManager.add(new TemplateFilter());
		standardSpotManager.add(new LengthFilter(2));
		standardSpotManager.add(new AsciiFilter());
		standardSpotManager.add(new ImageFilter());

		// pre clean
		standardSpotManager.add(new HtmlCleaner());
		standardSpotManager.add(new UnicodeCleaner());
		standardSpotManager.add(new SymbolCleaner('_'));
		standardSpotManager.add(new SymbolCleaner('-'));
		standardSpotManager.add(new LowerCaseCleaner());
		standardSpotManager.add(new JuniorAndInitialsCleaner());
		// standardSpotCleaner.add(new StripCleaner("#*-!`{}~[]='<>"));
		standardSpotManager.add(new TypeCleaner());

		// map

		standardSpotManager.add(new CityMapper());
		standardSpotManager.add(new QuotesMapper());

		// post clean

		standardSpotManager.add(new ParenthesesCleaner());
		standardSpotManager.add(new QuotesCleaner());
		standardSpotManager.add(new StripCleaner(",#*-!`{}~[]='<>:/;.&%|=+"));
		standardSpotManager.add(new TypeCleaner());

		// post filter
		standardSpotManager.add(new SymbolFilter());
		standardSpotManager.add(new TemplateFilter());
		standardSpotManager.add(new LengthFilter(2));
		standardSpotManager.add(new ImageFilter());
		standardSpotManager.add(new LongSpotFilter());
		standardSpotManager.add(new StripCleaner(",#*-!`{}~[]='<>:/;.&%|=+ "));

		return standardSpotManager;
	}

	/**
	 * Returns a StandardSpotCleaner used by Dexter to clean the anchors.
	 */
	public static SpotManager getStandardSpotCleaner() {
		// FIXME removed singleton (could be a problem for concurrency
		// if (standardSpotCleaner == null) {
		SpotManager standardSpotCleaner = new SpotManager();
		standardSpotCleaner.add(new HtmlCleaner());

		// pre clean pipe = new Pipe<String>(pipe,new UnicodeCleaner());
		standardSpotCleaner.add(new UnicodeCleaner());
		standardSpotCleaner.add(new SymbolCleaner('_'));
		standardSpotCleaner.add(new SymbolCleaner('-'));
		standardSpotCleaner.add(new StripCleaner("#*-!`{}~[]='<>:/"));
		// post clean
		standardSpotCleaner.add(new LowerCaseCleaner());
		standardSpotCleaner.add(new ParenthesesCleaner());
		standardSpotCleaner.add(new QuotesCleaner());
		standardSpotCleaner.add(new StripCleaner(",#*-!`{}~[]='<>:/;.&%|=+"));

		// }
		return standardSpotCleaner;
	}

	/**
	 * Cleans an anchor, i.e., performs over the text only the {@link Cleaner
	 * cleaners} previously added to the pipe.
	 */
	public String clean(String spot) {
		return cleanPipe.process(spot).iterator().next();
	}

	protected boolean isFilter(String s) {
		Set<String> res = process(s);
		return res.isEmpty();
	}

	/**
	 * Given a Wikipedia {@link Article article} returns a set containing all
	 * the processed anchors in the article.
	 * 
	 */
	public Set<String> getAllSpots(Article a) {
		Set<String> spots = new HashSet<String>();
		spots.addAll(process(a.getTitle()));
		if (a.isRedirect()) {
			spots.addAll(process(a.getRedirectNoAnchor()));
		} else {

			for (Link l : a.getLinks()) {
				spots.addAll(process(l.getDescription()));
			}
		}
		return spots;
	}

	public Set<String> process(String spot) {
		return new HashSet<String>(pipe.process(spot));
	}

}
