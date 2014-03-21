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
import it.cnr.isti.hpc.dexter.entity.Entity;
import it.cnr.isti.hpc.dexter.entity.EntityMatch;
import it.cnr.isti.hpc.dexter.entity.EntityMatchList;
import it.cnr.isti.hpc.dexter.label.IdHelper;
import it.cnr.isti.hpc.dexter.label.IdHelperFactory;
import it.cnr.isti.hpc.dexter.rest.domain.AnnotatedDocument;
import it.cnr.isti.hpc.dexter.rest.domain.AnnotatedSpot;
import it.cnr.isti.hpc.dexter.rest.domain.CandidateEntity;
import it.cnr.isti.hpc.dexter.rest.domain.CandidateSpot;
import it.cnr.isti.hpc.dexter.rest.domain.EntitySpots;
import it.cnr.isti.hpc.dexter.rest.domain.SpottedDocument;
import it.cnr.isti.hpc.dexter.rest.domain.Tagmeta;
import it.cnr.isti.hpc.dexter.spot.Spot;
import it.cnr.isti.hpc.dexter.spot.SpotMatch;
import it.cnr.isti.hpc.dexter.spot.SpotMatchList;
import it.cnr.isti.hpc.dexter.spot.ram.EntityToSpotListMap;
import it.cnr.isti.hpc.dexter.spotter.Spotter;
import it.cnr.isti.hpc.dexter.util.DexterLocalParams;
import it.cnr.isti.hpc.dexter.util.DexterParams;
import it.cnr.isti.hpc.wikipedia.article.Article;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import com.sun.jersey.api.representation.Form;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Feb 2, 2013
 */

@Path("jsonp")
@Api(value = "jsonp", description = "Dexter JSONP Rest Service")
public class JSONPService {

	private static Gson gson = new GsonBuilder()
			.serializeSpecialFloatingPointValues().create();
	private final ArticleServer server = new ArticleServer();

	public static final DexterParams params = DexterParams.getInstance();
	public static final IdHelper helper = IdHelperFactory.getStdIdHelper();

	private static final Logger logger = LoggerFactory
			.getLogger(JSONPService.class);

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
	@Path("/annotate")
	@ApiOperation(value = "Annotate a document with Wikipedia entities", response = AnnotatedDocument.class)
	@Produces({ MediaType.APPLICATION_JSON })
	public String annotateGet(@Context UriInfo ui,
			@QueryParam("callback") @DefaultValue("callback") String callback,
			@QueryParam("text") String text,
			@QueryParam("n") @DefaultValue("5") String n,
			@QueryParam("spt") String spotter,
			@QueryParam("dsb") String disambiguator,
			@QueryParam("wn") @DefaultValue("false") String wikiNames,
			@QueryParam("debug") @DefaultValue("false") String dbg) {
		DexterLocalParams requestParams = getLocalParams(ui);
		return annotate(callback, requestParams, text, n, spotter,
				disambiguator, wikiNames, dbg);

	}

	@POST
	@Path("/annotate")
	@ApiOperation(value = "Annotate a document with Wikipedia entities", response = AnnotatedDocument.class)
	@Produces({ MediaType.APPLICATION_JSON })
	public String annotatePost(Form form,
			@FormParam("callback") @DefaultValue("callback") String callback,
			@FormParam("text") String text,
			@FormParam("n") @DefaultValue("5") String n,
			@FormParam("spt") String spotter,
			@FormParam("dsb") String disambiguator,
			@FormParam("wn") @DefaultValue("false") String wikiNames,
			@FormParam("debug") @DefaultValue("false") String dbg) {

		DexterLocalParams requestParams = getLocalParams(form);
		return annotate(callback, requestParams, text, n, spotter,
				disambiguator, wikiNames, dbg);

	}

	private DexterLocalParams getLocalParams(Form form) {
		MultivaluedMap<String, String> queryParams = form;
		DexterLocalParams params = new DexterLocalParams();
		for (String key : queryParams.keySet()) {
			params.addParam(key, queryParams.getFirst(key));
		}
		return params;
	}

	public String annotate(String callback, DexterLocalParams requestParams,
			String text, String n, String spotter, String disambiguator,
			String wikiNames, String dbg) {
		if (text == null) {
			return callback + "({\"error\":\"text param is null\"})";
		}
		Spotter s = params.getSpotter(spotter);
		Disambiguator d = params.getDisambiguator(disambiguator);
		Tagger tagger = new StandardTagger("std", s, d);
		Boolean debug = new Boolean(dbg);
		boolean addWikinames = new Boolean(wikiNames);

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
		return callback + "(" + annotated + ")";
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
			try {
				sb.append(text.substring(pos, em.getStart()));
			} catch (java.lang.StringIndexOutOfBoundsException e) {
				logger.warn(
						"error annotating text output of bound for range {} - {} ",
						pos, em.getStart());
				logger.warn("text: \n\n {}\n\n", text);
			}
			// the spot has been normalized, i want to retrieve the real one
			String realSpot = "none";
			try {
				realSpot = text.substring(em.getStart(), em.getEnd());
			} catch (java.lang.StringIndexOutOfBoundsException e) {
				logger.warn(
						"error annotating text output of bound for range {} - {} ",
						pos, em.getStart());
				logger.warn("text: \n\n {}\n\n", text);
			}
			sb.append(
					"<a href=\"#\" onmouseover='manage(" + em.getId() + ")' >")
					.append(realSpot).append("</a>");
			pos = em.getEnd();
		}
		if (pos < text.length()) {
			try {
				sb.append(text.substring(pos));
			} catch (java.lang.StringIndexOutOfBoundsException e) {
				logger.warn(
						"error annotating text output of bound for range {} - end ",
						pos);
				logger.warn("text: \n\n {}\n\n", text);
			}
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
	@Path("/get-desc")
	@ApiOperation(value = "Provides the description of an entity", response = ArticleDescription.class)
	@Produces({ MediaType.APPLICATION_JSON })
	public String getDescription(
			@QueryParam("callback") @DefaultValue("callback") String callback,
			@QueryParam("id") String id,
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
		return callback + "(" + description + ")";

	}

	@GET
	@Path("/get-id")
	@ApiOperation(value = "Provides the wiki-id of an entity", response = ArticleDescription.class)
	@Produces({ MediaType.APPLICATION_JSON })
	public String getDescription(
			@QueryParam("callback") @DefaultValue("callback") String callback,
			@QueryParam("title") String title) {
		String label = Article.getTitleInWikistyle(title);
		int id = helper.getId(label);

		ArticleDescription desc = new ArticleDescription();
		desc.setTitle(label);
		desc.setId(id);
		String description = desc.toJson();
		logger.info("getId: {}", description);
		return callback + "(" + description + ")";

	}

	@GET
	@Path("/get-spots")
	@ApiOperation(value = "Provides all the spots that could refer to the given entity", response = EntitySpots.class)
	@Produces({ MediaType.APPLICATION_JSON })
	public String getEntitySpots(
			@QueryParam("callback") @DefaultValue("callback") String callback,
			@QueryParam("id") String id, @QueryParam("title") String title,
			@QueryParam("wn") @DefaultValue("false") String wikiNames) {
		int wid = 0;
		boolean addWikinames = new Boolean(wikiNames);
		if (title != null) {
			String label = Article.getTitleInWikistyle(title);
			wid = helper.getId(label);
		}
		if (id != null) {
			wid = Integer.parseInt(id);
		}
		if (wid == 0) {
			return "{ \"error\":\"retrieving the spot for the given entity\"}";
		}
		EntityToSpotListMap map = EntityToSpotListMap.getInstance();
		List<Spot> spots = map.getSpots(wid);
		List<CandidateSpot> cspots = new LinkedList<CandidateSpot>();
		for (Spot spot : spots) {
			CandidateSpot s = new CandidateSpot();
			s.setMention(spot.getMention());
			s.setLinkProbability(spot.getLinkProbability());
			s.setLinkFrequency(spot.getLink());
			s.setDocumentFrequency(spot.getFrequency());
			List<CandidateEntity> candidates = new ArrayList<CandidateEntity>();
			for (Entity entity : spot.getEntities()) {
				CandidateEntity c = new CandidateEntity(entity.getId(),
						entity.getFrequency(), spot.getEntityCommonness(entity));
				if (addWikinames) {
					c.setWikiname(helper.getLabel(entity.getId()));
				}
				candidates.add(c);
			}
			Collections.sort(candidates);
			s.setCandidates(candidates);
			cspots.add(s);
		}
		EntitySpots entitySpots = new EntitySpots();
		entitySpots.setEntity(wid);
		if (addWikinames) {
			entitySpots.setWikiname(helper.getLabel(wid));
		}
		entitySpots.setSpots(cspots);
		return callback + "(" + gson.toJson(entitySpots) + ")";

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
	private String spot(DexterLocalParams requestParams, String text,
			String spt, String wikiNames, String dbg) {
		long start = System.currentTimeMillis();
		Spotter spotter = params.getSpotter(spt);
		boolean debug = new Boolean(dbg);
		Document d = new FlatDocument(text);
		boolean addWikinames = new Boolean(wikiNames);
		SpotMatchList sml = spotter.match(requestParams, d);
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

		if (debug) {
			Tagmeta meta = new Tagmeta();
			meta.setSpotter(spotter.getClass().toString());
			meta.setRequestParams(requestParams.getParams());

			sd.setMeta(meta);

		}

		String spotted = gson.toJson(sd);
		logger.info("spot: {}", spotted);
		return spotted;
	}

	@GET
	@Path("/spot")
	@ApiOperation(value = "Detects all the mentions that could refer to an entity in the text", response = EntitySpots.class)
	@Produces({ MediaType.APPLICATION_JSON })
	public String spotGet(@Context UriInfo ui,
			@QueryParam("callback") @DefaultValue("callback") String callback,
			@QueryParam("text") String text, @QueryParam("spt") String spt,
			@QueryParam("wn") @DefaultValue("false") String wikiNames,
			@QueryParam("debug") @DefaultValue("false") String dbg) {
		DexterLocalParams requestParams = getLocalParams(ui);
		return callback + "(" + spot(requestParams, text, spt, wikiNames, dbg)
				+ ")";
	}

	@POST
	@Path("/spot")
	@ApiOperation(value = "Detects all the mentions that could refer to an entity in the text", response = EntitySpots.class)
	@Produces({ MediaType.APPLICATION_JSON })
	public String spotPost(Form form,
			@FormParam("callback") @DefaultValue("callback") String callback,
			@FormParam("text") String text, @FormParam("spt") String spt,
			@FormParam("wn") @DefaultValue("false") String wikiNames,
			@FormParam("debug") @DefaultValue("false") String dbg) {
		DexterLocalParams requestParams = getLocalParams(form);
		return callback + "(" + spot(requestParams, text, spt, wikiNames, dbg)
				+ ")";
	}

	@GET
	@Path("/get-candidates")
	@ApiOperation(value = "Given a query, returns a list of candidates entities represented by the query", response = EntitySpots.class)
	@Produces({ MediaType.APPLICATION_JSON })
	public String queryLucene(@Context UriInfo ui,
			@QueryParam("callback") @DefaultValue("callback") String callback,
			@QueryParam("field") @DefaultValue("content") String field,
			@QueryParam("n") @DefaultValue("10") String results,
			@QueryParam("query") String query) {
		Integer n = Integer.parseInt(results);
		return callback + "("
				+ gson.toJson(server.getEntities(query, field, n)) + ")";
	}
}
