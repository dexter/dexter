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
package it.cnr.isti.hpc.dexter.util;

import it.cnr.isti.hpc.io.reader.RecordParser;
import it.cnr.isti.hpc.wikipedia.article.Article;

/**
 * TitleDisambiguationId contains the title of an article, its numerical id,
 * and, if the article is a redirect, the redirect string.
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 10/lug/2012
 */

public class TitleRedirectId {
	String title;
	String redirect;
	String id;

	public TitleRedirectId() {

	}

	public TitleRedirectId(Article article) {
		if (article.isRedirect()) {
			title = article.getRedirectNoAnchor();
			redirect = article.getTitleInWikistyle();
		} else {
			redirect = "";
			title = article.getTitleInWikistyle();
		}
		id = String.valueOf(article.getWikiId());
		if (article.isDisambiguation()) {
			id = "-" + id;
		}
	}

	public String getId() {
		return id;
	}

	public String getRedirect() {
		return redirect;
	}

	public String getTitle() {
		return title;
	}

	public boolean isRedirect() {
		return !redirect.isEmpty();
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setRedirect(String redirect) {
		this.redirect = redirect;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * The Parser class encode a TitleRedirectId object as a tab separated value
	 * line, containing in the first position the title (or the target of the
	 * redirect if the article is a redirect), in the second position the
	 * redirect title (or the empty string if the article is not a redirect) and
	 * finally the numerical id of the article.
	 * 
	 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
	 * 
	 *         Created on Aug 25, 2013
	 */
	public static class Parser implements RecordParser<TitleRedirectId> {

		public TitleRedirectId decode(String record) {
			String[] elems = record.split("\t");
			TitleRedirectId t = new TitleRedirectId();
			t.title = elems[0];
			t.redirect = elems[1];
			t.id = elems[2];
			return t;
		}

		public String encode(TitleRedirectId tri) {
			return tri.title + "\t" + tri.redirect + "\t" + tri.id;
		}

	}

}
