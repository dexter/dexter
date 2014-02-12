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
package it.cnr.isti.hpc.dexter.rest;

import it.cnr.isti.hpc.dexter.StandardTagger;
import it.cnr.isti.hpc.dexter.Tagger;
import it.cnr.isti.hpc.dexter.article.ArticleDescription;
import it.cnr.isti.hpc.dexter.article.ArticleServer;
import it.cnr.isti.hpc.dexter.disambiguation.Disambiguator;
import it.cnr.isti.hpc.dexter.document.Document;
import it.cnr.isti.hpc.dexter.document.FlatDocument;
import it.cnr.isti.hpc.dexter.entity.EntityMatch;
import it.cnr.isti.hpc.dexter.entity.EntityMatchList;
import it.cnr.isti.hpc.dexter.label.IdHelper;
import it.cnr.isti.hpc.dexter.label.IdHelperFactory;
import it.cnr.isti.hpc.dexter.rest.domain.AnnotatedDocument;
import it.cnr.isti.hpc.dexter.rest.domain.AnnotatedSpot;
import it.cnr.isti.hpc.dexter.rest.domain.CandidateEntity;
import it.cnr.isti.hpc.dexter.rest.domain.CandidateSpot;
import it.cnr.isti.hpc.dexter.rest.domain.SpottedDocument;
import it.cnr.isti.hpc.dexter.rest.domain.Tagmeta;
import it.cnr.isti.hpc.dexter.spot.SpotMatch;
import it.cnr.isti.hpc.dexter.spot.SpotMatchList;
import it.cnr.isti.hpc.dexter.spotter.Spotter;
import it.cnr.isti.hpc.dexter.util.DexterLocalParams;
import it.cnr.isti.hpc.dexter.util.DexterParams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Feb 2, 2013
 */

@Path("/")
public class RestService {

	private static Gson gson = new GsonBuilder()
			.serializeSpecialFloatingPointValues().create();
	private final ArticleServer server = new ArticleServer();

	public static final DexterParams params = DexterParams.getInstance();
	public static final IdHelper helper = IdHelperFactory.getStdIdHelper();

	private static final Logger logger = LoggerFactory
			.getLogger(RestService.class);

	private DexterLocalParams getLocalParams(UriInfo ui) {
		MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
		DexterLocalParams params = new DexterLocalParams();
		for (String key : queryParams.keySet()) {
			params.addParam(key, queryParams.getFirst(key));
		}
		return params;
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
	@GET
	@Path("annotate")
	@Produces({ MediaType.APPLICATION_JSON })
	public String annotate(@Context UriInfo ui,
			@QueryParam("text") String text,
			@QueryParam("n") @DefaultValue("5") String n,
			@QueryParam("spt") String spotter,
			@QueryParam("dsb") String disambiguator,
			@QueryParam("wn") @DefaultValue("false") String wikiNames,
			@QueryParam("debug") @DefaultValue("false") String dbg) {

		Spotter s = params.getSpotter(spotter);
		Disambiguator d = params.getDisambiguator(disambiguator);
		Tagger tagger = new StandardTagger("std", s, d);
		Boolean debug = new Boolean(dbg);
		boolean addWikinames = new Boolean(wikiNames);

		DexterLocalParams requestParams = getLocalParams(ui);

		Integer entitiesToAnnotate = Integer.parseInt(n);
		Document doc = new FlatDocument(text);
		EntityMatchList eml = tagger.tag(requestParams, doc);

		AnnotatedDocument adoc = new AnnotatedDocument(text);

		if (debug) {
			Tagmeta meta = new Tagmeta();
			meta.setDisambiguator(d.getClass().toString());
			meta.setSpotter(s.getClass().toString());
			meta.setRequestParams(requestParams.getParams());

			adoc.setMeta(meta);

		}
		annotate(adoc, eml, entitiesToAnnotate, addWikinames);
		String annotated = gson.toJson(adoc);
		logger.info("annotate: {}", annotated);
		return annotated;
	}

	public void annotate(AnnotatedDocument adoc, EntityMatchList eml,
			boolean addWikiNames) {
		annotate(adoc, eml, eml.size(), addWikiNames);
	}

	public void annotate(AnnotatedDocument adoc, EntityMatchList eml,
			int nEntities, boolean addWikiNames) {
		eml.sort();
		EntityMatchList emlSub = new EntityMatchList();
		int size = Math.min(nEntities, eml.size());
		List<AnnotatedSpot> spots = adoc.getSpots();
		spots.clear();
		for (int i = 0; i < size; i++) {
			emlSub.add(eml.get(i));
			EntityMatch em = eml.get(i);
			AnnotatedSpot spot = new AnnotatedSpot(em.getMention(),
					em.getSpotLinkProbability(), em.getStart(), em.getEnd(), em
							.getSpot().getLinkFrequency(), em.getSpot()
							.getFrequency(), em.getId(), em.getFrequency(),
					em.getCommonness(), em.getScore());
			if (addWikiNames) {
				spot.setWikiname(helper.getLabel(em.getId()));
			}

			spots.add(spot);
		}
		String annotatedText = getAnnotatedText(adoc, emlSub);
		adoc.setAnnotatedText(annotatedText);
	}

	private String getAnnotatedText(AnnotatedDocument adoc, EntityMatchList eml) {
		Collections.sort(eml, new EntityMatch.SortByPosition());
		StringBuffer sb = new StringBuffer();
		int pos = 0;
		String text = adoc.getText();
		for (EntityMatch em : eml) {
			assert em.getStart() >= 0;
			assert em.getEnd() >= 0;

			sb.append(text.substring(pos, em.getStart()));
			// the spot has been normalized, i want to retrieve the real one
			String realSpot = text.substring(em.getStart(), em.getEnd());
			sb.append(
					"<a href=\"#\" onmouseover='manage(" + em.getId() + ")' >")
					.append(realSpot).append("</a>");
			pos = em.getEnd();
		}
		if (pos < text.length()) {
			sb.append(text.substring(pos));
		}

		return sb.toString();
	}

	/**
	 * Given the Wiki-id of an entity, returns a snippet containing some
	 * sentences that describe the entity.
	 * 
	 * @param id
	 *            the Wiki-id of the entity
	 * @param title
	 *            (optional, false by default) "true" if the function only
	 *            should return the label of the entity for the Wiki-id,
	 *            otherwise it will return all the metadata available.
	 * 
	 * @returns a short description of the entity represented by the Wiki-id
	 */
	@GET
	@Path("get-desc")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getDescription(@QueryParam("id") String id,
			@QueryParam("title-only") @DefaultValue("false") String titleonly) {

		int i = Integer.parseInt(id);
		boolean titleOnly = new Boolean(titleonly);
		if (titleOnly) {
			ArticleDescription desc = server.getOnlyEntityLabel(i);
			return desc.toJson();

		}

		ArticleDescription desc = server.get(i);
		if (desc == null) {
			logger.warn("description for id {} is null ", i);
			desc = ArticleDescription.EMPTY;
		}
		// desc.setImage("");
		// desc.setInfobox(new HashMap<String, String>());
		String description = desc.toJson();

		logger.info("getDescription: {}", description);
		return description;

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
	@GET
	@Path("spot")
	@Produces({ MediaType.APPLICATION_JSON })
	public String spot(@QueryParam("text") String text,
			@QueryParam("spt") String spt,
			@QueryParam("wn") @DefaultValue("false") String wikiNames) {
		long start = System.currentTimeMillis();
		Spotter spotter = params.getSpotter(spt);
		Document d = new FlatDocument(text);
		boolean addWikinames = new Boolean(wikiNames);
		SpotMatchList sml = spotter.match(null, d);
		List<CandidateSpot> spots = new ArrayList<CandidateSpot>();
		List<CandidateEntity> candidates;

		for (SpotMatch spot : sml) {
			CandidateSpot s = new CandidateSpot();
			s.setMention(spot.getMention());
			s.setStart(spot.getStart());
			s.setEnd(spot.getEnd());
			s.setLinkProbability(spot.getLinkProbability());
			s.setLinkFrequency(spot.getLinkFrequency());
			s.setDocumentFrequency(spot.getFrequency());
			candidates = new ArrayList<CandidateEntity>();
			for (EntityMatch entity : spot.getEntities()) {
				CandidateEntity c = new CandidateEntity(entity.getId(),
						entity.getFrequency(), entity.getCommonness());
				if (addWikinames) {
					c.setWikiname(helper.getLabel(entity.getId()));
				}
				candidates.add(c);
			}
			Collections.sort(candidates);
			s.setCandidates(candidates);
			spots.add(s);
		}
		SpottedDocument sd = new SpottedDocument(text, spots, spots.size(),
				System.currentTimeMillis() - start);
		String spotted = gson.toJson(sd);
		logger.info("spot: {}", spotted);
		return spotted;
	}
}
