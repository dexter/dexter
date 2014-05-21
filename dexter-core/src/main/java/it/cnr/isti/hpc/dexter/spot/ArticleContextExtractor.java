/**
s *  Copyright 2012 Diego Ceccarelli
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

import it.cnr.isti.hpc.dexter.common.Field;
import it.cnr.isti.hpc.dexter.spot.clean.SpotManager;
import it.cnr.isti.hpc.wikipedia.article.Article;
import it.cnr.isti.hpc.wikipedia.article.Link;

/**
 * ContextExtractor for each label extract the context (text around the the
 * label)
 * 
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 24/lug/2012
 */
public class ArticleContextExtractor extends ContextExtractor {
	/**
	 * Logger for this class
	 */
	static StringBuilder sb = new StringBuilder();
	static SpotManager cleaner = SpotManager.getStandardSpotCleaner();
	
	
//	static{
//		
//		cleaner.add(new TemplateCleaner());
//		cleaner.add(new UnicodeCleaner());	
//	}
	private transient String cleanAsciiText;
	
	
	public ArticleContextExtractor(Article a) {
		
		sb.setLength(0);
		
		this.text = getCleanAsciiText(a);
		init(this.text);
	}

	public ArticleContextExtractor(Field field){
		super(field);
	}
	
	public String getCleanAsciiText(Article a){
		if (cleanAsciiText != null) return cleanAsciiText;
		sb.setLength(0);
		sb.append(a.getTitle()).append(". ");
		for (String paragraph : a.getParagraphs()){
			sb.append(cleaner.clean(paragraph));
			sb.append(" ");
		}
		for (Link l : a.getLinks()){
			sb.append(l.getDescription());
			sb.append(" ");
		}
		cleanAsciiText = sb.toString();
		return cleanAsciiText;
	}
}
