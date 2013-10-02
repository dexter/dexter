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
package it.cnr.isti.hpc.dexter.article;

import it.cnr.isti.hpc.dexter.freebase.FreebaseEntity;
import it.cnr.isti.hpc.structure.LRUCache;
import it.cnr.isti.hpc.wikipedia.article.Article;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikipedia.Wiki;

import com.google.gson.Gson;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 */
public class ArticleDescription {

	private String title;
	private String description;
	private String image;
	private Map<String, String> infobox;
	private static final int MAX_LENGTH = 200;
	private static Gson gson = new Gson();

	private static LRUCache<String, ArticleDescription> cache = new LRUCache<String, ArticleDescription>(
			1000);

	private static final ArticleDescription EMPTY = new ArticleDescription();

	private static final Logger logger = LoggerFactory
			.getLogger(ArticleDescription.class);

	private ArticleDescription() {
		title = "NODESC";
	}

	public ArticleDescription(Article a) {
		title = a.getTitle();
		infobox = new HashMap<String, String>();
		description = a.getSummary();
//		FreebaseEntity fe = new FreebaseEntity(a.getTitleInWikistyle());
//		if (fe.hasId()) {
//			image = fe.getHtmlCode();
//		}
	}

	public static ArticleDescription fromWikipediaAPI(String name) {

		if (cache.containsKey(name)) {
			return cache.get(name);
		}
		Wiki wiki = new Wiki();

		ArticleDescription desc = new ArticleDescription();
		desc.setTitle(name);
		try {
			String rendered = wiki.getRenderedText(name);
			// logger.info("render {} ",rendered);
			desc.setDescription(rendered);
		} catch (IOException e) {
			logger.error("cannot retrieve description for {} using the wikipedia api");

			desc = EMPTY;
		}
		cache.put(name, desc);
		return desc;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArticleDescription other = (ArticleDescription) obj;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	static String getImageUrl(String imageName) {
		String wiki = Article.getTitleInWikistyle(imageName);
		String hash = md5(wiki);
		StringBuilder sb = new StringBuilder();
		sb.append("http://upload.wikimedia.org/wikipedia/commons/");
		sb.append(hash.charAt(0));
		sb.append("/");
		sb.append(hash.charAt(0));
		sb.append(hash.charAt(1));
		sb.append("/");
		sb.append(wiki);
		return sb.toString();

	}

	public String toJson() {
		return gson.toJson(this);
	}

	private static String md5(String str) {
		MessageDigest m = null;
		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		m.reset();
		m.update(str.getBytes());
		byte[] digest = m.digest();
		BigInteger bigInt = new BigInteger(1, digest);
		String hashtext = bigInt.toString(16);
		return hashtext;
	}

	// private String seekImage(List<Template> templates) {
	// for (Template t : templates) {
	// for (String s : t.getDescription()) {
	// // System.out.println(s);
	// if (s.startsWith("image =")) {
	// String name = s.substring(10);
	// return name.trim();
	// }
	// }
	// }
	// return "";
	//
	// }

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * @param image
	 *            the image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * @return the infobox
	 */
	public Map<String, String> getInfobox() {
		return infobox;
	}

	/**
	 * @param infobox
	 *            the infobox to set
	 */
	public void setInfobox(Map<String, String> infobox) {
		this.infobox = infobox;
	}

}
