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

import it.cnr.isti.hpc.dexter.graph.CategoryNodeFactory;
import it.cnr.isti.hpc.dexter.graph.EntityCategoryNodeFactory;
import it.cnr.isti.hpc.dexter.graph.IncomingNodes;
import it.cnr.isti.hpc.dexter.graph.NodeFactory;
import it.cnr.isti.hpc.dexter.graph.OutcomingNodes;
import it.cnr.isti.hpc.dexter.label.IdHelper;
import it.cnr.isti.hpc.dexter.label.IdHelperFactory;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Nov 17, 2013
 */

@Path("/graph")
public class GraphRestService {

	private static Gson gson = new GsonBuilder()
			.serializeSpecialFloatingPointValues().create();

	private static final Logger logger = LoggerFactory
			.getLogger(GraphRestService.class);

	private static IdHelper helper = IdHelperFactory.getStdIdHelper();

	@GET
	@Path("get-target-entities")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getTargetEntities(@QueryParam("wid") String wikiId,
			@QueryParam("asWikiNames") @DefaultValue("false") String asWikiNames) {
		boolean convert = new Boolean(asWikiNames);
		int id = Integer.parseInt(wikiId);
		OutcomingNodes entityOutcomingNodes = NodeFactory
				.getOutcomingNodes(NodeFactory.STD_TYPE);
		int[] out = entityOutcomingNodes.getNeighbours(id);
		if (!convert) {
			return gson.toJson(out);
		}
		List<String> names = new ArrayList<String>(out.length);
		for (int entity : out) {
			names.add(helper.getLabel(entity));
		}
		return gson.toJson(names);
	}

	@GET
	@Path("get-source-entities")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getSourceEntities(@QueryParam("wid") String wikiId,
			@QueryParam("asWikiNames") @DefaultValue("false") String asWikiNames) {
		boolean convert = new Boolean(asWikiNames);
		int id = Integer.parseInt(wikiId);
		IncomingNodes entityIncomingNodes = NodeFactory
				.getIncomingNodes(NodeFactory.STD_TYPE);
		int[] in = entityIncomingNodes.getNeighbours(id);
		if (!convert) {
			return gson.toJson(in);
		}
		List<String> names = new ArrayList<String>(in.length);
		for (int entity : in) {
			names.add(helper.getLabel(entity));
		}
		return gson.toJson(names);
	}

	@GET
	@Path("get-entity-categories")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getEntityCategories(@QueryParam("wid") String wikiId,
			@QueryParam("asWikiNames") @DefaultValue("false") String asWikiNames) {
		boolean convert = new Boolean(asWikiNames);
		int id = Integer.parseInt(wikiId);
		OutcomingNodes entityOutcomingNodes = EntityCategoryNodeFactory
				.getOutcomingNodes(EntityCategoryNodeFactory.STD_TYPE);
		int[] out = entityOutcomingNodes.getNeighbours(id);
		if (!convert) {
			return gson.toJson(out);
		}
		List<String> names = new ArrayList<String>(out.length);
		for (int entity : out) {
			names.add(helper.getLabel(entity));
		}
		return gson.toJson(names);
	}

	@GET
	@Path("get-belonging-entities")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getBelongingEntities(@QueryParam("wid") String wikiId,
			@QueryParam("asWikiNames") @DefaultValue("false") String asWikiNames) {
		boolean convert = new Boolean(asWikiNames);
		int id = Integer.parseInt(wikiId);
		IncomingNodes entityIncomingNodes = EntityCategoryNodeFactory
				.getIncomingNodes(EntityCategoryNodeFactory.STD_TYPE);
		int[] in = entityIncomingNodes.getNeighbours(id);
		if (!convert) {
			return gson.toJson(in);
		}
		List<String> names = new ArrayList<String>(in.length);
		for (int entity : in) {
			names.add(helper.getLabel(entity));
		}
		return gson.toJson(names);
	}

	@GET
	@Path("get-parent-categories")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getParentCategories(@QueryParam("wid") String wikiId,
			@QueryParam("asWikiNames") @DefaultValue("false") String asWikiNames) {
		boolean convert = new Boolean(asWikiNames);
		int id = Integer.parseInt(wikiId);
		IncomingNodes categoryIncomingNodes = CategoryNodeFactory
				.getIncomingNodes(CategoryNodeFactory.STD_TYPE);
		int[] in = categoryIncomingNodes.getNeighbours(id);
		if (!convert) {
			return gson.toJson(in);
		}
		List<String> names = new ArrayList<String>(in.length);
		for (int category : in) {
			names.add(helper.getLabel(category));
		}
		return gson.toJson(names);
	}

	@GET
	@Path("get-child-categories")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getChildCategories(@QueryParam("wid") String wikiId,
			@QueryParam("asWikiNames") @DefaultValue("false") String asWikiNames) {
		boolean convert = new Boolean(asWikiNames);
		int id = Integer.parseInt(wikiId);
		OutcomingNodes categoryOutcomingNodes = CategoryNodeFactory
				.getOutcomingNodes(CategoryNodeFactory.STD_TYPE);
		int[] out = categoryOutcomingNodes.getNeighbours(id);
		if (!convert) {
			return gson.toJson(out);
		}
		List<String> names = new ArrayList<String>(out.length);
		for (int category : out) {
			names.add(helper.getLabel(category));
		}
		return gson.toJson(names);
	}

}
