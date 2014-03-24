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

import it.cnr.isti.hpc.structure.LRUCache;
import it.cnr.isti.hpc.wikipedia.article.Article;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikipedia.Wiki;

import com.google.gson.Gson;

/**
 * An entity description, contains several metadata extracted from the Wikipedia
 * article, describing the entity.
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 */
public class ArticleDescription {

	private String title;
	private String description;
	private String image;

	private String uri;
	private int id;
	private Map<String, String> infobox;
	private List<ArticleDescription> incomingEntities;
	private List<ArticleDescription> outcomingEntities;
	private List<ArticleDescription> parentCategories;
	private List<ArticleDescription> childCategories;

	// private static final int MAX_LENGTH = 200;
	private static Gson gson = new Gson();

	private static LRUCache<String, ArticleDescription> cache = new LRUCache<String, ArticleDescription>(
			1000);

	public static final ArticleDescription EMPTY = new ArticleDescription();

	private static final Logger logger = LoggerFactory
			.getLogger(ArticleDescription.class);

	public ArticleDescription() {
		title = "NOTITLE";
		description = "NODESC";
		infobox = null;
		image = "";
		id = -1;

	}

	public ArticleDescription(Article a) {
		title = a.getTitle();
		id = a.getWid();
		image = "http://wikiname2image.herokuapp.com/" + a.getWikiTitle();
		uri = "http://en.wikipedia.org/wiki/" + a.getWikiTitle();
		infobox = null;
		description = a.getSummary();
		// FreebaseEntity fe = new FreebaseEntity(a.getTitleInWikistyle());
		// if (fe.hasId()) {
		// image = fe.getHtmlCode();
		// }
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
		if (infobox.isEmpty())
			infobox = null;
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

	public List<ArticleDescription> getIncomingEntities() {
		return incomingEntities;
	}

	public void setIncomingEntities(List<ArticleDescription> incomingEntities) {
		this.incomingEntities = incomingEntities;
	}

	public List<ArticleDescription> getOutcomingEntities() {
		return outcomingEntities;
	}

	public void setOutcomingEntities(List<ArticleDescription> outcomingEntities) {
		this.outcomingEntities = outcomingEntities;
	}

	public List<ArticleDescription> getParentCategories() {
		return parentCategories;
	}

	public void setParentCategories(List<ArticleDescription> parentCategories) {
		this.parentCategories = parentCategories;
	}

	public List<ArticleDescription> getChildCategories() {
		return childCategories;
	}

	public void setChildCategories(List<ArticleDescription> childCategories) {
		this.childCategories = childCategories;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	@Override
	public String toString() {
		return "ArticleDescription [title=" + title + ", description="
				+ description + ", image=" + image + ", infobox=" + infobox
				+ "]";
	}

}
