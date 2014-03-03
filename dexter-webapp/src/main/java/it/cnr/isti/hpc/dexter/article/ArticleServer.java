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

import it.cnr.isti.hpc.benchmark.Stopwatch;
import it.cnr.isti.hpc.dexter.label.IdHelper;
import it.cnr.isti.hpc.dexter.label.IdHelperFactory;
import it.cnr.isti.hpc.dexter.lucene.LuceneHelper;
import it.cnr.isti.hpc.wikipedia.article.Article;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Allows to retrieve entity descriptions given the it's wiki-id.
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 */
public class ArticleServer {
	private static final Logger logger = LoggerFactory
			.getLogger(ArticleServer.class);

	private LuceneHelper lucene;
	private final IdHelper idHelper = IdHelperFactory.getStdIdHelper();
	private final Stopwatch timer = new Stopwatch();

	public ArticleServer() {
		if (LuceneHelper.hasDexterLuceneIndex()) {
			lucene = LuceneHelper.getDexterLuceneHelper();
		}
	}

	/**
	 * Retrieves an entity description
	 * 
	 * @param id
	 *            - the wiki-id of the entity
	 */
	public ArticleDescription get(int id) {
		ArticleDescription desc;
		Article a = new Article();
		if (lucene != null) {
			timer.start("retrieve");
			a = lucene.getArticleSummary(id);
			timer.stop("retrieve");
			logger.info("retrieve {} ", id);
			logger.info(timer.stat("retrieve"));

			desc = new ArticleDescription(a);
		} else {
			String name = idHelper.getLabel(id);
			desc = ArticleDescription.fromWikipediaAPI(name);
			if (desc == null) {
				logger.warn(
						"cannot retrieve data from wikipedia for article {}",
						name);
				desc = ArticleDescription.EMPTY;
			}
		}
		return desc;

	}

	/**
	 * Retrieves an entity description, containing only the entity label
	 * 
	 * @param id
	 *            - the wiki-id of the entity
	 */
	public ArticleDescription getOnlyEntityLabel(int id) {
		ArticleDescription desc = new ArticleDescription();
		String label = idHelper.getLabel(id);
		desc.setTitle(label);
		return desc;

	}

	public List<ArticleDescription> getEntities(String query, String field) {
		logger.info("query lucene index: {}:{}", field, query);
		List<Integer> entities = lucene.query(query, field);
		logger.info("results: ", entities);
		List<ArticleDescription> descriptions = new LinkedList<ArticleDescription>();
		for (Integer entity : entities) {
			descriptions.add(get(entity));

		}
		return descriptions;
	}
}
