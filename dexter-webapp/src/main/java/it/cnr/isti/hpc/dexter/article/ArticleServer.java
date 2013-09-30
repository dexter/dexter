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

import it.cnr.isti.hpc.dexter.hash.IdHelper;
import it.cnr.isti.hpc.dexter.hash.IdHelperFactory;
import it.cnr.isti.hpc.dexter.lucene.LuceneHelper;
import it.cnr.isti.hpc.net.FakeBrowser;
import it.isti.cnr.hpc.wikipedia.article.Article;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 *
 */
public class ArticleServer  {
	private static final Logger logger = LoggerFactory
			.getLogger(ArticleServer.class);
	
	//static LuceneHelper lucene = LuceneHelper.getDexterLuceneHelper();
	static IdHelper idHelper = IdHelperFactory.getStdIdHelper();
	private static ArticleServer instance = null;
	
	
	
	private ArticleServer(){
	
	}
	
	public Article get(int id){
		Article a = new Article();
		//a = lucene.getArticle(id);
		//a.setWikiId(id);
		String title = idHelper.getLabel(id);
		
		
		
		title = title.replace('_', ' ');
		a.setTitle(title);
		return a;
		
	}
	
	
	public static ArticleServer getInstance(){
		if (instance == null ){
			instance = new ArticleServer();
		}
		return instance;
	}

	 
	
	

}
