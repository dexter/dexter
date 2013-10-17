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


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.cnr.isti.hpc.dexter.Dexter;
import it.cnr.isti.hpc.dexter.Document;
import it.cnr.isti.hpc.dexter.FlatDocument;
import it.cnr.isti.hpc.dexter.article.ArticleDescription;
import it.cnr.isti.hpc.dexter.article.ArticleServer;
import it.cnr.isti.hpc.dexter.entity.EntityMatch;
import it.cnr.isti.hpc.dexter.entity.EntityMatchList;
import it.cnr.isti.hpc.dexter.spot.SpotMatch;
import it.cnr.isti.hpc.dexter.spot.SpotMatchList;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 * Created on Feb 2, 2013
 */

@Path("/")
public class RestService {
	
	private static Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
	private ArticleServer server = new ArticleServer();	
	private Dexter tagger = new Dexter();

	
	@GET
	@Path("annotate")
	@Produces({ MediaType.APPLICATION_JSON })
	public String annotate(@QueryParam("text") String text, @QueryParam("n") @DefaultValue( "5" ) String n ) {
		Integer entitiesToAnnotate = Integer.parseInt(n);
		Document doc = new FlatDocument(text);
		EntityMatchList eml = tagger.tag(doc);
		AnnotatedDocument adoc = new AnnotatedDocument(text);
		adoc.annotate(eml, entitiesToAnnotate);
		return gson.toJson(adoc);
	} 
	
	
	@GET
	@Path("get-desc")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getDescription(@QueryParam("id") String id) {
		int i = Integer.parseInt(id);
		
		ArticleDescription desc = server.get(i);
		return desc.toJson();
	}
	
	@GET
	@Path("spot")
	@Produces({ MediaType.APPLICATION_JSON })
	public String spot(@QueryParam("text") String document){
		Document d = new FlatDocument(document);
		SpotMatchList sml = tagger.spot(d);
		List<CandidateSpot> spots = new ArrayList<CandidateSpot>();
		List<CandidateEntity> candidates;
		for (SpotMatch spot : sml){
			CandidateSpot s = new CandidateSpot();
			s.setMention(spot.getMention());
			s.setStart(spot.getStart());
			s.setEnd(spot.getEnd());
			s.setLinkProbability(spot.getLinkProbability());
			candidates = new ArrayList<CandidateEntity>();
			for (EntityMatch entity : spot.getEntities()){
				CandidateEntity c = new CandidateEntity(entity.getId(), entity.getFrequency(), entity.getPriorProbability());
				candidates.add(c);
			}
			Collections.sort(candidates);
			s.setCandidates(candidates);
			spots.add(s);
		}
		return gson.toJson(spots);
	}
	
	
	

}
