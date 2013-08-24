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
package it.cnr.isti.hpc.dexter.spot;

import it.cnr.isti.hpc.dexter.spot.clean.Cleaner;
import it.cnr.isti.hpc.dexter.spot.clean.HtmlCleaner;
import it.cnr.isti.hpc.dexter.spot.clean.JuniorAndInitialsCleaner;
import it.cnr.isti.hpc.dexter.spot.clean.LowerCaseCleaner;
import it.cnr.isti.hpc.dexter.spot.clean.ParenthesesCleaner;
import it.cnr.isti.hpc.dexter.spot.clean.QuotesCleaner;
import it.cnr.isti.hpc.dexter.spot.clean.StripCleaner;
import it.cnr.isti.hpc.dexter.spot.clean.TypeCleaner;
import it.cnr.isti.hpc.dexter.spot.clean.UnderscoreCleaner;
import it.cnr.isti.hpc.dexter.spot.clean.UnicodeCleaner;
import it.cnr.isti.hpc.dexter.spot.filter.AsciiFilter;
import it.cnr.isti.hpc.dexter.spot.filter.Filter;
import it.cnr.isti.hpc.dexter.spot.filter.ImageFilter;
import it.cnr.isti.hpc.dexter.spot.filter.LengthFilter;
import it.cnr.isti.hpc.dexter.spot.filter.LongSpotFilter;
import it.cnr.isti.hpc.dexter.spot.filter.SymbolFilter;
import it.cnr.isti.hpc.dexter.spot.filter.TemplateFilter;
import it.cnr.isti.hpc.dexter.spot.mapper.CityMapper;
import it.cnr.isti.hpc.dexter.spot.mapper.Mapper;
import it.cnr.isti.hpc.dexter.spot.mapper.QuotesMapper;
import it.cnr.isti.hpc.wikipedia.article.Article;
import it.cnr.isti.hpc.wikipedia.article.Link;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * SpotCleaner.java
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 20/lug/2012
 */
public class SpotManager {

	private List<Cleaner> preCleaners;
	private List<Cleaner> postCleaners;
	private List<Filter> preFilters;
	private List<Filter> postFilters;
	private List<Mapper> mappers;

	private static SpotManager standardSpotManager = null;
	private static SpotManager standardSpotCleaner = null;

	public static String cleanText(String text) {
		getStandardSpotCleaner();
		return standardSpotCleaner.clean(text);
	}

	public SpotManager() {
		preCleaners = new LinkedList<Cleaner>();
		postCleaners = new LinkedList<Cleaner>();

		preFilters = new LinkedList<Filter>();
		postFilters = new LinkedList<Filter>();

		mappers = new LinkedList<Mapper>();
	}

	public static SpotManager getStandardSpotManager() {
		if (standardSpotManager == null) {
			standardSpotManager = new SpotManager();
			standardSpotManager.add(new SymbolFilter());
			standardSpotManager.add(new TemplateFilter());
			standardSpotManager.add(new LengthFilter());
			standardSpotManager.add(new AsciiFilter());
			standardSpotManager.add(new ImageFilter());

			standardSpotManager.add(new LowerCaseCleaner());
			standardSpotManager.add(new HtmlCleaner());
			standardSpotManager.add(new UnicodeCleaner());
			standardSpotManager.add(new ParenthesesCleaner());
			standardSpotManager.add(new QuotesCleaner());
			standardSpotManager.add(new UnderscoreCleaner());
			standardSpotManager.add(new JuniorAndInitialsCleaner());
			standardSpotManager.add(new StripCleaner(",#*-!`{}~[]='<>:/"));
			standardSpotManager.add(new StripCleaner(";.&%", false, true));
			standardSpotManager.add(new TypeCleaner());
			standardSpotManager.add(new LongSpotFilter());
			standardSpotManager.add(new CityMapper());
			standardSpotManager.add(new QuotesMapper());
		}
		return standardSpotManager;
	}

	public static SpotManager getStandardSpotCleaner() {
		if (standardSpotCleaner == null) {
			standardSpotCleaner = new SpotManager();
			standardSpotCleaner.add(new LowerCaseCleaner());
			standardSpotCleaner.add(new HtmlCleaner());
			standardSpotCleaner.add(new UnicodeCleaner());
			standardSpotCleaner.add(new QuotesCleaner());
			standardSpotCleaner.add(new UnderscoreCleaner());
			standardSpotCleaner.add(new ParenthesesCleaner());
			standardSpotCleaner.add(new StripCleaner());
		}
		return standardSpotCleaner;
	}

	public void add(Cleaner c) {
		if (c.pre()) {
			preCleaners.add(c);
		}
		if (c.post()) {
			postCleaners.add(c);
		}
	}

	public void add(Filter f) {
		if (f.pre()) {
			preFilters.add(f);
		}
		if (f.post()) {
			postFilters.add(f);
		}
	}

	public void add(Mapper m) {
		mappers.add(m);
	}

	public String clean(String spot) {
		return postClean(preClean(spot));
	}

	public Set<String> getAllSpots(Article a) {
		Set<String> spots = new HashSet<String>();
		if (a.isRedirect()) {
			spots.addAll(process(a.getRedirectNoAnchor()));
		} else {
			for (Link l : a.getLinks()) {
				spots.addAll(process(l.getDescription()));
			}
		}
		return spots;
	}

	protected boolean isPostFilter(String spot) {
		for (Filter f : postFilters) {
			if (f.isFilter(spot))
				return true;
		}
		return false;
	}

	protected boolean isPreFilter(String spot) {
		for (Filter f : preFilters) {
			if (f.isFilter(spot))
				return true;
		}
		return false;
	}

	protected Set<String> map(String spot) {

		Set<String> sets = new HashSet<String>();
		sets.add(spot);
		for (Mapper m : mappers) {
			sets.addAll(m.mapper(spot));
		}
		return sets;
	}

	protected String postClean(String spot) {
		for (Cleaner c : postCleaners) {
			spot = c.clean(spot);
		}
		return spot;
	}

	protected String preClean(String spot) {
		for (Cleaner c : preCleaners) {
			spot = c.clean(spot);
		}
		return spot;
	}

	public Set<String> process(String spot) {
		if (isPreFilter(spot))
			return Collections.emptySet();
		spot = preClean(spot);
		Set<String> spots = new HashSet<String>();
		for (String s : map(spot)) {
			s = postClean(s);
			if (isPostFilter(s))
				continue;
			spots.add(s);
		}
		return spots;
	}

}
