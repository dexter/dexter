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


import it.cnr.isti.hpc.dexter.Dexter;
import it.cnr.isti.hpc.dexter.Document;
import it.cnr.isti.hpc.dexter.FlatDocument;
import it.cnr.isti.hpc.dexter.article.ArticleDescription;
import it.cnr.isti.hpc.dexter.entity.EntityMatchList;
import it.cnr.isti.hpc.dexter.label.IdHelper;
import it.cnr.isti.hpc.dexter.label.IdHelperFactory;
import it.cnr.isti.hpc.dexter.relatedness.MilneRelatedness;
import it.cnr.isti.hpc.dexter.relatedness.RelatednessFactory;
import it.cnr.isti.hpc.dexter.tagme.Tagme;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 * Created on Feb 2, 2013
 */

@Path("/")
public class RestService {
	
	private static Gson gson = new Gson();
	//private static ArticleServer server = ArticleServer.getInstance();
	IdHelper helper = IdHelperFactory.getStdIdHelper();
	
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
		//Article a = server.get(i);
		//if (a == null) return "{\"error\":\"no id for article \"}";
		String name = helper.getLabel(i);
		
		//ArticleDescription desc = new ArticleDescription(a);
		ArticleDescription desc = ArticleDescription.fromWikipediaAPI(name);
		return desc.toJson();
	}
	
	
	

}
