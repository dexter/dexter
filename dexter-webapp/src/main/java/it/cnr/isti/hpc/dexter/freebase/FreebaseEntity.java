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
package it.cnr.isti.hpc.dexter.freebase;

import it.cnr.isti.hpc.dexter.entity.Entity;
import it.cnr.isti.hpc.dexter.label.IdHelper;
import it.cnr.isti.hpc.dexter.label.IdHelperFactory;
import it.cnr.isti.hpc.net.FakeBrowser;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.nnsoft.sameas4j.DefaultSameAsServiceFactory;
import org.nnsoft.sameas4j.Equivalence;
import org.nnsoft.sameas4j.SameAsService;
import org.nnsoft.sameas4j.SameAsServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * FreebaseEntity.java
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 10/mag/2012
 */
public class FreebaseEntity {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(FreebaseEntity.class);
	private static IdHelper helper = IdHelperFactory.getStdIdHelper();

	private final static String DBPEDIA_RESOURCE = "http://dbpedia.org/resource/";
	private static final Gson gson = new Gson();
	private static SameAsService sameAsService = DefaultSameAsServiceFactory
			.getSingletonInstance();
	String wikiId;
	String freebaseId;
	static FakeBrowser browser = new FakeBrowser();

	private FreebaseEntity() {
	}

	public FreebaseEntity(String wikiId) {
		if (wikiId == null) {
			logger.warn("no wiki id");
			return;
		}
		this.wikiId = wikiId;
		Equivalence equivalence = null;
		try {
			equivalence = sameAsService.getDuplicates(new URI(DBPEDIA_RESOURCE
					+ wikiId));
			for (URI uri : equivalence) {
				String path = uri.getPath().toString();
				if (path.startsWith("/ns/m")) {
					freebaseId = "m/" + path.substring(6, path.length());
					logger.info("uri {} \t freebaseId {} ", uri, freebaseId);
					break;
				}
			}

		} catch (SameAsServiceException e) {
			logger.error("cannot match freebase entity for {} ", wikiId);
		} catch (URISyntaxException e) {
			logger.error("cannot match freebase entity for {} ", wikiId);
		}
		if (freebaseId != null && !freebaseId.isEmpty()) {
			try {
				// StringBuilder images =
				// browser.fetchAsString("https://www.googleapis.com/freebase/v1/mqlread/?lang=%2Flang%2Fen&query=[{%20%22id%22:%20%22"+freebaseId+"%22,%20%22/common/topic/image%22:%20[{%22id%22:%20null}]%20}]");
				String url = browser
						.fetchAsString("https://www.googleapis.com/freebase/v1/mqlread/?lang=%2Flang%2Fen&query=[{%20%22id%22:%20%22/"
								+ freebaseId
								+ "%22,%20%22/common/topic/image%22:%20[{%22id%22:%20null}]%20}]");

				https: // www.googleapis.com/freebase/v1/mqlread/?lang=%2Flang%2Fen&query=[{%20%22id%22:%20%22/m/01zt7w%22,%20%22/common/topic/image%22:%20[{%22id%22:%20null}]%20}]
				if (url == null || url.contains("[]")) {
					freebaseId = "";
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				freebaseId = "";
			}
		} else {
			freebaseId = "";
		}

	}

	public String getHtmlCode() {
		return "<img class='fb' alt='"
				+ wikiId
				+ "' src='https://usercontent.googleapis.com/freebase/v1/image/"
				+ freebaseId + "?maxwidth=150&maxheight=150'>";
	}

	public FreebaseEntity(Entity e) {
		this(helper.getLabel(e.getId()));
	}

	public boolean hasId() {
		return freebaseId != "";
	}

	public String getId() {
		return freebaseId;
	}

	public String toJson() {
		return gson.toJson(this);
	}

}
