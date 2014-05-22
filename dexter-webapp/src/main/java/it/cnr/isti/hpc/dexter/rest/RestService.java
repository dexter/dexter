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
import it.cnr.isti.hpc.dexter.article.ArticleServer;
import it.cnr.isti.hpc.dexter.common.ArticleDescription;
import it.cnr.isti.hpc.dexter.common.Field;
import it.cnr.isti.hpc.dexter.common.FlatDocument;
import it.cnr.isti.hpc.dexter.common.MultifieldDocument;
import it.cnr.isti.hpc.dexter.disambiguation.Disambiguator;
import it.cnr.isti.hpc.dexter.entity.Entity;
import it.cnr.isti.hpc.dexter.entity.EntityMatch;
import it.cnr.isti.hpc.dexter.entity.EntityMatchList;
import it.cnr.isti.hpc.dexter.graph.CategoryNodeFactory;
import it.cnr.isti.hpc.dexter.graph.EntityCategoryNodeFactory;
import it.cnr.isti.hpc.dexter.graph.IncomingNodes;
import it.cnr.isti.hpc.dexter.graph.NodeFactory;
import it.cnr.isti.hpc.dexter.graph.OutcomingNodes;
import it.cnr.isti.hpc.dexter.label.IdHelper;
import it.cnr.isti.hpc.dexter.label.IdHelperFactory;
import it.cnr.isti.hpc.dexter.relatedness.Relatedness;
import it.cnr.isti.hpc.dexter.relatedness.RelatednessFactory;
import it.cnr.isti.hpc.dexter.rest.domain.AnnotatedDocument;
import it.cnr.isti.hpc.dexter.rest.domain.AnnotatedSpot;
import it.cnr.isti.hpc.dexter.rest.domain.CandidateEntity;
import it.cnr.isti.hpc.dexter.rest.domain.CandidateSpot;
import it.cnr.isti.hpc.dexter.rest.domain.EntityRelatedness;
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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

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
import javax.ws.rs.core.Response;
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

@Path("rest")
@Api(value = "rest", description = "Dexter Rest Service")
public class RestService {

	private static Gson gson = new GsonBuilder()
			.serializeSpecialFloatingPointValues().create();
	private final ArticleServer server = new ArticleServer();

	private final static int STATUS_ERROR = 500;

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

	private Response error(String msg) {
		Response r = Response.status(STATUS_ERROR)
				.entity("{\"error\":\"" + msg + "\"}").build();
		return r;
	}

	private Response ok(Object o) {
		Response r = Response.ok(gson.toJson(o)).build();
		return r;
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
	public Response annotateGet(@Context UriInfo ui,
			@QueryParam("text") String text,
			@QueryParam("n") @DefaultValue("5") String n,
			@QueryParam("spt") String spotter,
			@QueryParam("dsb") String disambiguator,
			@QueryParam("wn") @DefaultValue("false") String wikiNames,
			@QueryParam("debug") @DefaultValue("false") String dbg,
			@QueryParam("format") @DefaultValue("text") String format) {
		DexterLocalParams requestParams = getLocalParams(ui);
		return annotate(requestParams, text, n, spotter, disambiguator,
				wikiNames, dbg, format);

	}

	@GET
	@Path("/relatedness")
	@ApiOperation(value = "Return the semantic relatedness between two entities", response = Relatedness.class)
	@Produces({ MediaType.APPLICATION_JSON })
	public String relatedness(@Context UriInfo ui, @QueryParam("e1") String e1,
			@QueryParam("e2") String e2,
			@QueryParam("rel") @DefaultValue("milnewitten") String rel,
			@QueryParam("wn") @DefaultValue("false") String wikiNames,
			@QueryParam("debug") @DefaultValue("false") String dbg) {

		int x = Integer.parseInt(e1);
		int y = Integer.parseInt(e2);
		EntityRelatedness relatedness = new EntityRelatedness(x, y, rel);
		RelatednessFactory rf = new RelatednessFactory(rel);
		double r = rf.getRelatedness(x, y).getScore();
		relatedness.setRelatedness(r);
		boolean addWikinames = new Boolean(wikiNames);
		if (addWikinames) {
			relatedness.setEntity1Wikiname(helper.getLabel(x));
			relatedness.setEntity2Wikiname(helper.getLabel(y));
		}
		return gson.toJson(relatedness);

	}

	private List<Integer> parseEntities(String e) {
		List<Integer> list = new ArrayList<Integer>();
		Scanner scanner = new Scanner(e).useDelimiter(",");
		while (scanner.hasNextInt()) {
			list.add(scanner.nextInt());
		}
		return list;
	}

	@GET
	@Path("/spot-relatedness")
	@ApiOperation(value = "Return the semantic relatedness between two entities", response = Relatedness.class)
	@Produces({ MediaType.APPLICATION_JSON })
	public String spotRelatedness(@Context UriInfo ui,
			@QueryParam("s1") String s1id, @QueryParam("s2") String s2id,
			@QueryParam("e1") String s1candidates,
			@QueryParam("e2") String s2candidates,
			@QueryParam("rel") @DefaultValue("milnewitten") String rel,
			@QueryParam("wn") @DefaultValue("false") String wikiNames,
			@QueryParam("debug") @DefaultValue("false") String dbg) {

		List<Integer> e1list = parseEntities(s1candidates);
		List<Integer> e2list = parseEntities(s2candidates);

		double max = 0;
		int maxi = -1;
		int maxj = -1;
		RelatednessFactory rf = new RelatednessFactory(rel);
		for (int i = 0; i < e1list.size(); i++) {
			for (int j = 0; j < e2list.size(); j++) {
				double r = rf.getRelatedness(e1list.get(i), e2list.get(j))
						.getScore();
				if (r > max) {
					maxi = i;
					maxj = j;
					max = r;
				}
			}
		}

		EntityRelatedness relatedness = new EntityRelatedness(
				Integer.parseInt(s1id), Integer.parseInt(s2id), rel);
		relatedness.setRelatedness(max);

		boolean addWikinames = new Boolean(wikiNames);
		if (addWikinames) {
			if (maxi > 0)
				relatedness.setEntity1Wikiname(helper.getLabel(maxi));
			if (maxj > 0)
				relatedness.setEntity2Wikiname(helper.getLabel(maxj));
		}
		return gson.toJson(relatedness);

	}

	@POST
	@Path("/annotate")
	@ApiOperation(value = "Annotate a document with Wikipedia entities", response = AnnotatedDocument.class)
	@Produces({ MediaType.APPLICATION_JSON })
	public Response annotatePost(Form form, @FormParam("text") String text,
			@FormParam("n") @DefaultValue("5") String n,
			@FormParam("spt") String spotter,
			@FormParam("dsb") String disambiguator,
			@FormParam("wn") @DefaultValue("false") String wikiNames,
			@FormParam("debug") @DefaultValue("false") String dbg,
			@FormParam("format") @DefaultValue("text") String format) {

		DexterLocalParams requestParams = getLocalParams(form);
		return annotate(requestParams, text, n, spotter, disambiguator,
				wikiNames, dbg, format);

	}

	private DexterLocalParams getLocalParams(Form form) {
		MultivaluedMap<String, String> queryParams = form;
		DexterLocalParams params = new DexterLocalParams();
		for (String key : queryParams.keySet()) {
			params.addParam(key, queryParams.getFirst(key));
		}
		return params;
	}

	private MultifieldDocument parseDocument(String text, String format) {
		Tagmeta.DocumentFormat df = Tagmeta.getDocumentFormat(format);
		MultifieldDocument doc = null;
		if (df == Tagmeta.DocumentFormat.TEXT) {
			doc = new FlatDocument(text);
		}
		if (df == Tagmeta.DocumentFormat.JSON) {
			doc = gson.fromJson(text, MultifieldDocument.class);

		}
		return doc;

	}

	public Response annotate(DexterLocalParams requestParams, String text,
			String n, String spotter, String disambiguator, String wikiNames,
			String dbg, String format) {
		if (text == null) {
			return error("text parameter is null");
		}

		Spotter s = params.getSpotter(spotter);
		Disambiguator d = params.getDisambiguator(disambiguator);
		Tagger tagger = new StandardTagger("std", s, d);
		Boolean debug = new Boolean(dbg);
		boolean addWikinames = new Boolean(wikiNames);

		Integer entitiesToAnnotate = Integer.parseInt(n);
		MultifieldDocument doc = null;
		try {
			doc = parseDocument(text, format);
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
			return error(e.getMessage());
		}

		EntityMatchList eml = tagger.tag(requestParams, doc);

		AnnotatedDocument adoc = new AnnotatedDocument(doc);

		if (debug) {
			Tagmeta meta = new Tagmeta();
			meta.setDisambiguator(d.getClass().toString());
			meta.setSpotter(s.getClass().toString());
			meta.setFormat(format);
			meta.setRequestParams(requestParams.getParams());

			adoc.setMeta(meta);

		}
		annotate(adoc, eml, entitiesToAnnotate, addWikinames);

		// logger.info("annotate: {}", annotated);
		return ok(adoc);
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
			spot.setField(em.getSpot().getField().getName());
			if (addWikiNames) {
				spot.setWikiname(helper.getLabel(em.getId()));
			}

			spots.add(spot);
		}
		MultifieldDocument annotatedDocument = getAnnotatedDocument(adoc,
				emlSub);
		adoc.setAnnotatedDocument(annotatedDocument);
	}

	private MultifieldDocument getAnnotatedDocument(AnnotatedDocument adoc,
			EntityMatchList eml) {
		Collections.sort(eml, new EntityMatch.SortByPosition());

		Iterator<Field> iterator = adoc.getDocument().getFields();
		MultifieldDocument annotated = new MultifieldDocument();
		while (iterator.hasNext()) {
			int pos = 0;
			StringBuffer sb = new StringBuffer();
			Field field = iterator.next();
			String currentField = field.getName();
			String currentText = field.getValue();

			for (EntityMatch em : eml) {
				if (!em.getSpot().getField().getName().equals(currentField)) {
					continue;
				}
				assert em.getStart() >= 0;
				assert em.getEnd() >= 0;
				try {
					sb.append(currentText.substring(pos, em.getStart()));
				} catch (java.lang.StringIndexOutOfBoundsException e) {
					logger.warn(
							"error annotating text output of bound for range {} - {} ",
							pos, em.getStart());
					logger.warn("text: \n\n {}\n\n", currentText);
				}
				// the spot has been normalized, i want to retrieve the real one
				String realSpot = "none";
				try {
					realSpot = currentText
							.substring(em.getStart(), em.getEnd());
				} catch (java.lang.StringIndexOutOfBoundsException e) {
					logger.warn(
							"error annotating text output of bound for range {} - {} ",
							pos, em.getStart());
					logger.warn("text: \n\n {}\n\n", currentText);
				}
				sb.append(
						"<a href=\"#\" onmouseover='manage(" + em.getId()
								+ ")' >").append(realSpot).append("</a>");
				pos = em.getEnd();
			}
			if (pos < currentText.length()) {
				try {
					sb.append(currentText.substring(pos));
				} catch (java.lang.StringIndexOutOfBoundsException e) {
					logger.warn(
							"error annotating text output of bound for range {} - end ",
							pos);
					logger.warn("text: \n\n {}\n\n", currentText);
				}

			}
			annotated.addField(new Field(field.getName(), sb.toString()));

		}

		return annotated;
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
			@QueryParam("id") @DefaultValue("11983070") String id,
			@QueryParam("title-only") @DefaultValue("false") String titleonly) {

		int i = Integer.parseInt(id);
		boolean titleOnly = new Boolean(titleonly);
		if (titleOnly) {
			ArticleDescription desc = server.getOnlyEntityLabel(i);
			desc.setDescription(null);
			desc.setImage(null);
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

	@GET
	@Path("/get-id")
	@ApiOperation(value = "Provides the wiki-id of an entity", response = ArticleDescription.class)
	@Produces({ MediaType.APPLICATION_JSON })
	public String getDescription(
			@QueryParam("title") @DefaultValue("Johnny_Cash") String title) {
		String label = Article.getTitleInWikistyle(title);
		int id = helper.getId(label);

		ArticleDescription desc = new ArticleDescription();
		desc.setTitle(label);
		desc.setId(id);
		desc.setDescription(null);
		desc.setImage(null);
		String description = desc.toJson();
		logger.info("getId: {}", description);
		return description;

	}

	@GET
	@Path("/get-spots")
	@ApiOperation(value = "Provides all the spots that could refer to the given entity", response = EntitySpots.class)
	@Produces({ MediaType.APPLICATION_JSON })
	public String getEntitySpots(
			@QueryParam("id") @DefaultValue("11983070") String id,
			@QueryParam("title") @DefaultValue("Johnny_Cash") String title,
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
		return gson.toJson(entitySpots);

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
	private Response spot(DexterLocalParams requestParams, String text,
			String spt, String wikiNames, String dbg, String format) {
		long start = System.currentTimeMillis();
		Spotter spotter = params.getSpotter(spt);
		boolean debug = new Boolean(dbg);
		MultifieldDocument doc = null;

		try {
			doc = parseDocument(text, format);
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
			return error(e.getMessage());
		}

		boolean addWikinames = new Boolean(wikiNames);
		SpotMatchList sml = spotter.match(requestParams, doc);
		List<CandidateSpot> spots = new ArrayList<CandidateSpot>();
		List<CandidateEntity> candidates;

		for (SpotMatch spot : sml) {
			CandidateSpot s = new CandidateSpot();
			s.setMention(spot.getMention());
			s.setStart(spot.getStart());
			s.setField(spot.getField().getName());
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
		SpottedDocument sd = new SpottedDocument(doc, spots, spots.size(),
				System.currentTimeMillis() - start);

		if (debug) {

			Tagmeta meta = new Tagmeta();
			meta.setSpotter(spotter.getClass().toString());
			meta.setRequestParams(requestParams.getParams());
			meta.setFormat(format);
			sd.setMeta(meta);

		}

		// String spotted = gson.toJson(sd);
		// logger.info("spot: {}", spotted);
		return ok(sd);
	}

	@GET
	@Path("/spot")
	@ApiOperation(value = "Detects all the mentions that could refer to an entity in the text", response = EntitySpots.class)
	@Produces({ MediaType.APPLICATION_JSON })
	public Response spotGet(
			@Context UriInfo ui,
			@QueryParam("text") @DefaultValue("Bob Dylan and Johnny Cash had formed a mutual admiration society even before they met in the early 1960s") String text,
			@QueryParam("spt") String spt,
			@QueryParam("wn") @DefaultValue("false") String wikiNames,
			@QueryParam("debug") @DefaultValue("false") String dbg,
			@QueryParam("format") @DefaultValue("text") String format) {
		DexterLocalParams requestParams = getLocalParams(ui);
		return spot(requestParams, text, spt, wikiNames, dbg, format);
	}

	@POST
	@Path("/spot")
	@ApiOperation(value = "Detects all the mentions that could refer to an entity in the text", response = EntitySpots.class)
	@Produces({ MediaType.APPLICATION_JSON })
	public Response spotPost(
			Form form,
			@FormParam("text") @DefaultValue("Bob Dylan and Johnny Cash had formed a mutual admiration society even before they met in the early 1960s") String text,
			@FormParam("spt") @DefaultValue("wiki-dictionary") String spt,
			@FormParam("wn") @DefaultValue("false") String wikiNames,
			@FormParam("debug") @DefaultValue("false") String dbg,
			@FormParam("format") @DefaultValue("text") String format) {
		DexterLocalParams requestParams = getLocalParams(form);
		return spot(requestParams, text, spt, wikiNames, dbg, format);
	}

	@GET
	@Path("/get-candidates")
	@ApiOperation(value = "Given a query, returns a list of candidates entities represented by the query", response = EntitySpots.class)
	@Produces({ MediaType.APPLICATION_JSON })
	public Response queryLucene(@Context UriInfo ui,
			@QueryParam("field") @DefaultValue("title") String field,
			@QueryParam("n") @DefaultValue("10") String results,
			@QueryParam("query") @DefaultValue("johnny cash") String query) {
		Integer n = Integer.parseInt(results);
		int status = 500;
		List<ArticleDescription> rankedArticles = Collections.emptyList();
		if (n > 0) {
			rankedArticles = server.getEntities(query, field, n);

		}
		if (rankedArticles.size() > 0) {
			status = 200;
		}
		String json = gson.toJson(rankedArticles);
		Response r = Response.status(status).entity(json).build();
		return r;
	}

	// graph

	private List<ArticleDescription> getNodes(int id, ArticleDescription e,
			List<ArticleDescription> list, int[] array, boolean addWikinames) {
		List<ArticleDescription> nodes = new ArrayList<ArticleDescription>(
				array.length);
		e.setId(id);
		e.setDescription(null);
		e.setImage(null);
		e.setInfobox(null);

		for (int entity : array) {
			ArticleDescription desc = new ArticleDescription();
			desc.setId(entity);
			desc.setImage(null);
			desc.setDescription(null);
			desc.setInfobox(null);
			if (addWikinames) {
				desc.setTitle(helper.getLabel(entity));
				e.setTitle(helper.getLabel(id));
			} else {
				desc.setTitle(null);
				e.setTitle(null);
			}
			nodes.add(desc);
		}
		return nodes;
	}

	@GET
	@Path("/get-target-entities")
	@ApiOperation(value = "Given an entity, returns the entities linked by given entity", response = ArticleDescription.class)
	@Produces({ MediaType.APPLICATION_JSON })
	public String getTargetEntities(
			@QueryParam("id") @DefaultValue("11983070") String wikiId,
			@QueryParam("wn") @DefaultValue("false") String asWikiNames) {
		boolean addWikinames = new Boolean(asWikiNames);

		ArticleDescription e = new ArticleDescription();

		int id = Integer.parseInt(wikiId);

		if (addWikinames) {

		}
		OutcomingNodes entityOutcomingNodes = NodeFactory
				.getOutcomingNodes(NodeFactory.STD_TYPE);
		int[] out = entityOutcomingNodes.getNeighbours(id);
		e.setOutcomingEntities(getNodes(id, e,
				new ArrayList<ArticleDescription>(out.length), out,
				addWikinames));
		return gson.toJson(e);
	}

	@GET
	@Path("/get-source-entities")
	@ApiOperation(value = "Given an entity, returns the entities that link to the given entity", response = ArticleDescription.class)
	@Produces({ MediaType.APPLICATION_JSON })
	public String getSourceEntities(
			@QueryParam("id") @DefaultValue("11983070") String wikiId,
			@QueryParam("wn") @DefaultValue("false") String asWikiNames) {
		boolean addWikinames = new Boolean(asWikiNames);
		ArticleDescription e = new ArticleDescription();
		int id = Integer.parseInt(wikiId);

		IncomingNodes entityIncomingNodes = NodeFactory
				.getIncomingNodes(NodeFactory.STD_TYPE);
		int[] in = entityIncomingNodes.getNeighbours(id);
		e.setIncomingEntities(getNodes(id, e,
				new ArrayList<ArticleDescription>(in.length), in, addWikinames));
		return gson.toJson(e);
	}

	@GET
	@Path("/get-entity-categories")
	@ApiOperation(value = "Given an entity, returns its categories", response = ArticleDescription.class)
	@Produces({ MediaType.APPLICATION_JSON })
	public String getEntityCategories(
			@QueryParam("id") @DefaultValue("11983070") String wikiId,
			@QueryParam("wn") @DefaultValue("false") String asWikiNames) {
		boolean addWikinames = new Boolean(asWikiNames);

		int id = Integer.parseInt(wikiId);
		OutcomingNodes entityOutcomingNodes = EntityCategoryNodeFactory
				.getOutcomingNodes(EntityCategoryNodeFactory.STD_TYPE);
		int[] out = entityOutcomingNodes.getNeighbours(id);
		ArticleDescription e = new ArticleDescription();
		e.setParentCategories(getNodes(id, e,
				new ArrayList<ArticleDescription>(out.length), out,
				addWikinames));
		return gson.toJson(e);

	}

	@GET
	@Path("/get-belonging-entities")
	@ApiOperation(value = "Given a category, returns the entities that belong to the category", response = ArticleDescription.class)
	@Produces({ MediaType.APPLICATION_JSON })
	public String getBelongingEntities(
			@QueryParam("id") @DefaultValue("30061715") String wikiId,
			@QueryParam("wn") @DefaultValue("false") String asWikiNames) {
		boolean addWikinames = new Boolean(asWikiNames);
		int id = Integer.parseInt(wikiId);
		IncomingNodes entityIncomingNodes = EntityCategoryNodeFactory
				.getIncomingNodes(EntityCategoryNodeFactory.STD_TYPE);
		int[] in = entityIncomingNodes.getNeighbours(id);
		ArticleDescription e = new ArticleDescription();
		e.setOutcomingEntities(getNodes(id, e,
				new ArrayList<ArticleDescription>(in.length), in, addWikinames));
		return gson.toJson(e);

	}

	@GET
	@Path("/get-parent-categories")
	@ApiOperation(value = "Given a category, returns its parent categories", response = ArticleDescription.class)
	@Produces({ MediaType.APPLICATION_JSON })
	public String getParentCategories(
			@QueryParam("id") @DefaultValue("30061715") String wikiId,
			@QueryParam("wn") @DefaultValue("false") String asWikiNames) {
		boolean addWikinames = new Boolean(asWikiNames);
		int id = Integer.parseInt(wikiId);
		IncomingNodes categoryIncomingNodes = CategoryNodeFactory
				.getIncomingNodes(CategoryNodeFactory.STD_TYPE);
		int[] in = categoryIncomingNodes.getNeighbours(id);
		ArticleDescription e = new ArticleDescription();
		e.setParentCategories(getNodes(id, e,
				new ArrayList<ArticleDescription>(in.length), in, addWikinames));
		return gson.toJson(e);
	}

	@GET
	@Path("/get-child-categories")
	@ApiOperation(value = "Given a category, returns its child categories", response = ArticleDescription.class)
	@Produces({ MediaType.APPLICATION_JSON })
	public String getChildCategories(
			@QueryParam("id") @DefaultValue("8251471") String wikiId,
			@QueryParam("wn") @DefaultValue("false") String asWikiNames) {
		boolean addWikinames = new Boolean(asWikiNames);
		int id = Integer.parseInt(wikiId);
		OutcomingNodes categoryOutcomingNodes = CategoryNodeFactory
				.getOutcomingNodes(CategoryNodeFactory.STD_TYPE);
		int[] out = categoryOutcomingNodes.getNeighbours(id);
		ArticleDescription e = new ArticleDescription();
		e.setChildCategories(getNodes(id, e, new ArrayList<ArticleDescription>(
				out.length), out, addWikinames));
		return gson.toJson(e);
	}
}
