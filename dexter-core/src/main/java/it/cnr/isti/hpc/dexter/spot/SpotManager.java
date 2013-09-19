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
import it.cnr.isti.hpc.dexter.spot.cleanpipe.cleaner.TypeCleaner;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.cleaner.UnderscoreCleaner;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.cleaner.UnicodeCleaner;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.filter.AsciiFilter;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.filter.Filter;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.filter.ImageFilter;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.filter.LengthFilter;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.filter.LongSpotFilter;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.filter.SymbolFilter;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.filter.TemplateFilter;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.mapper.CityMapper;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.mapper.Mapper;
import it.cnr.isti.hpc.dexter.spot.cleanpipe.mapper.QuotesMapper;
import it.cnr.isti.hpc.wikipedia.article.Article;
import it.cnr.isti.hpc.wikipedia.article.Link;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * SpotCleaner.java
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 20/lug/2012
 */
public class SpotManager {

	
	
	private Pipe<String> pipe;
	private Pipe<String> cleanPipe;


	private static SpotManager standardSpotManager = null;
	private static SpotManager standardSpotCleaner = null;

	public static String cleanText(String text) {
		getStandardSpotCleaner();
		return standardSpotCleaner.clean(text);
	}
	
	public SpotManager(){
		this.pipe = new Pipe<String>(new NopFunction<String>());
		this.cleanPipe = new Pipe<String>(new NopFunction<String>());

		
	}
	
	public void add(Function<String> fun){
		this.pipe = new Pipe<String>(this.pipe, fun);
		if (fun instanceof Cleaner){
			this.cleanPipe = new Pipe<String>(this.cleanPipe, fun);
		}
	}

	public SpotManager(Pipe<String> pipe) {
		this.pipe = pipe;
		
		
	}
	
	

	public static SpotManager getStandardSpotManager() {
		if (standardSpotManager == null) {
			
			standardSpotManager = new SpotManager();
			// pre filter 
			standardSpotManager.add(new SymbolFilter());
			standardSpotManager.add(new TemplateFilter());
			standardSpotManager.add(new LengthFilter());
			standardSpotManager.add(new AsciiFilter());
			standardSpotManager.add(new ImageFilter());

			// pre clean 
			standardSpotManager.add(new HtmlCleaner());
			standardSpotManager.add(new UnicodeCleaner());
			standardSpotManager.add(new UnderscoreCleaner());
			standardSpotManager.add(new JuniorAndInitialsCleaner());
			standardSpotManager.add(new StripCleaner(",#*-!`{}~[]='<>:/"));
			standardSpotManager.add(new TypeCleaner());

			// map
			
			standardSpotManager.add(new CityMapper());
			standardSpotManager.add(new QuotesMapper());
			
			// post clean 
			standardSpotManager.add(new LowerCaseCleaner());
			standardSpotManager.add(new ParenthesesCleaner());
			standardSpotManager.add(new QuotesCleaner());
			standardSpotManager.add(new StripCleaner(",#*-!`{}~[]='<>:/;.&%"));
			standardSpotManager.add(new TypeCleaner());


			//post filter
			standardSpotManager.add(new SymbolFilter());
			standardSpotManager.add(new TemplateFilter());
			standardSpotManager.add(new LengthFilter());
			standardSpotManager.add(new ImageFilter());
			standardSpotManager.add(new LongSpotFilter());
			
			

		}
		return standardSpotManager;
	}

	public static SpotManager getStandardSpotCleaner() {
		if (standardSpotCleaner == null) {
			Pipe<String> pipe = new Pipe<String>(new HtmlCleaner());

			// pre clean pipe = new Pipe<String>(pipe,new UnicodeCleaner());
			pipe = new Pipe<String>(pipe,new UnderscoreCleaner());
			pipe = new Pipe<String>(pipe,new StripCleaner(",#*-!`{}~[]='<>:/"));
			// post clean 
			pipe = new Pipe<String>(pipe,new LowerCaseCleaner());
			pipe = new Pipe<String>(pipe,new ParenthesesCleaner());
			pipe = new Pipe<String>(pipe,new QuotesCleaner());
			pipe = new Pipe<String>(pipe,new StripCleaner(",#*-!`{}~[]='<>:/;.&%"));
			standardSpotCleaner = new SpotManager(pipe);
			
		}
		return standardSpotCleaner;
	}

	

	
	public String clean(String spot) {
		return cleanPipe.process(spot).iterator().next();
	}
	
	protected boolean isFilter(String s){
		Set<String> res = process(s);
		return res.isEmpty();
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


	
	
	

	public Set<String> process(String spot) {
		return new HashSet<String>(pipe.process(spot));
	}

}
