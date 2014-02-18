/**
 *  Copyright 2013 Diego Ceccarelli
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

/**
 *  Copyright 2013 Diego Ceccarelli
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
package it.cnr.isti.hpc.dexter.rest.client;

import it.cnr.isti.hpc.dexter.article.ArticleDescription;
import it.cnr.isti.hpc.dexter.rest.domain.AnnotatedDocument;
import it.cnr.isti.hpc.dexter.rest.domain.SpottedDocument;
import it.cnr.isti.hpc.net.FakeBrowser;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * Allows to perform annotation calling the Dexter Rest Service
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Oct 29, 2013
 */
public class DexterRestClient {

	private final URI server;
	private final FakeBrowser browser;

	private Boolean wikinames = false;

	Map<String, String> params = new HashMap<String, String>();

	public double linkProbability = -1;

	private static Gson gson = new Gson();

	private static final Logger logger = LoggerFactory
			.getLogger(DexterRestClient.class);

	/**
	 * Istanciates a Rest client, that invocates the rest service provided by a
	 * Dexter server.
	 * 
	 * @param server
	 *            the url of the rest service
	 */
	public DexterRestClient(String server) throws URISyntaxException {
		this(new URI(server));

	}

	/**
	 * Istanciates a Rest client, that invocates the rest service provided by a
	 * Dexter server.
	 * 
	 * @param server
	 *            the url of the rest service
	 */
	public DexterRestClient(URI server) {
		this.server = server;
		browser = new FakeBrowser();
	}

	/**
	 * Performs the entity linking on a given text, annotating maximum 5
	 * entities.
	 * 
	 * @param text
	 *            the text to annotate
	 * @returns an annotated document, containing the annotated text, and a list
	 *          entities detected.
	 */
	public AnnotatedDocument annotate(String doc) {
		return annotate(doc, -1);

	}

	/**
	 * Performs the entity linking on a given text, annotating maximum n
	 * entities.
	 * 
	 * @param text
	 *            the text to annotate
	 * @param n
	 *            the maximum number of entities to annotate
	 * @returns an annotated document, containing the annotated text, and a list
	 *          entities detected.
	 */
	public AnnotatedDocument annotate(String doc, int n) {
		String text = URLEncoder.encode(doc);
		String json = "";

		String url = "/annotate?" + paramsToRequest() + "&text=" + text;
		if (linkProbability > 0)
			url += "&lp=" + linkProbability;

		if (wikinames) {
			url += "&wn=true";
		}
		try {
			if (n > 0) {
				url += "&n=" + n;
			}
			json = browser.fetchAsString(server.toString() + url).toString();
		} catch (IOException e) {
			logger.error("cannot call the rest api {}", e.toString());
			return null;
		}
		AnnotatedDocument adoc = gson.fromJson(json, AnnotatedDocument.class);
		return adoc;
	}

	/**
	 * It only performs the first step of the entity linking process, i.e., find
	 * all the mentions that could refer to an entity.
	 * 
	 * @param text
	 *            the text to spot
	 * @return all the spots detected in the text together with their link
	 *         probability. For each spot it also returns the list of candidate
	 *         entities associated with it, together with their commonness.
	 */
	public SpottedDocument spot(String doc) {
		String text = URLEncoder.encode(doc);
		String json = "";
		String url = "/spot?text=" + text;
		if (linkProbability > 0)
			url += "&lp=" + linkProbability;
		if (wikinames) {
			url += "&wn=true";
		}

		try {
			json = browser.fetchAsString(server.toString() + url).toString();
		} catch (IOException e) {
			logger.error("cannot call the rest api {}", e.toString());
			return null;
		}
		SpottedDocument sdoc = gson.fromJson(json, SpottedDocument.class);
		return sdoc;
	}

	/**
	 * Given the Wiki-id of an entity, returns a snippet containing some
	 * sentences that describe the entity.
	 * 
	 * @param id
	 *            the Wiki-id of the entity
	 * @returns a short description of the entity represented by the Wiki-id
	 */
	public ArticleDescription getDesc(int id) {

		String json = "";
		try {
			json = browser.fetchAsString(
					server.toString() + "/get-desc?id=" + id).toString();
		} catch (IOException e) {
			logger.error("cannot call the rest api {}", e.toString());
			return null;
		}
		ArticleDescription ad = gson.fromJson(json, ArticleDescription.class);
		return ad;
	}

	/**
	 * Given the Wiki-id entity label (the title, or a redirect), the wiki-id of
	 * the entity
	 * 
	 * @param title
	 *            the label or a redirect title of the entity.
	 * @returns the wiki-id of the entity
	 */
	public int getId(String title) {
		title = URLEncoder.encode(title);
		String json = "";
		try {
			json = browser.fetchAsString(
					server.toString() + "/get-id?title=" + title).toString();
		} catch (IOException e) {
			logger.error("cannot call the rest api {}", e.toString());
			return -1;
		}
		ArticleDescription ad = gson.fromJson(json, ArticleDescription.class);
		return ad.getId();
	}

	public void addParams(String name, String value) {
		params.put(name, value);
	}

	private String paramsToRequest() {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> p : params.entrySet()) {
			sb.append(p.getKey()).append('=');
			sb.append(URLEncoder.encode(p.getValue()));
			sb.append('&');
		}
		return sb.toString();
	}

	public Boolean getWikinames() {
		return wikinames;
	}

	public double getLinkProbability() {
		return linkProbability;
	}

	public void setLinkProbability(double linkProbability) {
		this.linkProbability = linkProbability;
	}

	public void setWikinames(Boolean wikinames) {
		this.wikinames = wikinames;
	}

	public static void main(String[] args) throws URISyntaxException {
		DexterRestClient client = new DexterRestClient(
				"http://node5.novello.isti.cnr.it:8080/dexter-webapp/rest");
		client.setLinkProbability(1);
		AnnotatedDocument ad = client
				.annotate("Dexter is an American television drama series which debuted on Showtime on October 1, 2006. The series centers on Dexter Morgan (Michael C. Hall), a blood spatter pattern analyst for the fictional Miami Metro Police Department (based on the real life Miami-Dade Police Department) who also leads a secret life as a serial killer. Set in Miami, the show's first season was largely based on the novel Darkly Dreaming Dexter, the first of the Dexter series novels by Jeff Lindsay. It was adapted for television by screenwriter James Manos, Jr., who wrote the first episode. ");
		System.out.println(gson.toJson(ad));
		SpottedDocument sd = client
				.spot("Dexter is an American television drama series which debuted on Showtime on October 1, 2006. The series centers on Dexter Morgan (Michael C. Hall), a blood spatter pattern analyst for the fictional Miami Metro Police Department (based on the real life Miami-Dade Police Department) who also leads a secret life as a serial killer. Set in Miami, the show's first season was largely based on the novel Darkly Dreaming Dexter, the first of the Dexter series novels by Jeff Lindsay. It was adapted for television by screenwriter James Manos, Jr., who wrote the first episode. ");
		System.out.println(gson.toJson(sd));
		System.out.println("maradona wid = " + client.getId("maradona"));
		ArticleDescription desc = client.getDesc(5981816);
		System.out.println(desc);

	}
}
