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
import it.cnr.isti.hpc.dexter.common.ArticleDescription;
import it.cnr.isti.hpc.dexter.label.IdHelper;
import it.cnr.isti.hpc.dexter.label.IdHelperFactory;
import it.cnr.isti.hpc.dexter.lucene.LuceneHelper;
import it.cnr.isti.hpc.structure.LRUCache;
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

	private static LRUCache<Query, List<ArticleDescription>> lruCache = new LRUCache<Query, List<ArticleDescription>>(
			2000);
	private static LRUCache<Integer, ArticleDescription> cache = new LRUCache<Integer, ArticleDescription>(
			1000);
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
		if (cache.containsKey(id))
			return cache.get(id);
		ArticleDescription desc;
		Article a = new Article();
		if (lucene != null) {
			timer.start("retrieve");
			a = lucene.getArticleSummary(id);
			timer.stop("retrieve");
			logger.info("retrieve {} ", id);
			logger.info(timer.stat("retrieve"));

			desc = new ArticleDescription(a.getTitle(),
					a.getTitleInWikistyle(), a.getWid(), a.getSummary());
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
		synchronized (cache) {
			cache.put(id, desc);
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

	public List<ArticleDescription> getEntities(String query, String field,
			int n) {
		Query q = new Query(query, field, n);
		logger.info("query lucene index: {} hc{}", q, q.hashCode());
		if (lruCache.containsKey(q)) {
			logger.info("cache hit for {} ", q);
			return lruCache.get(q);
		}
		List<Integer> entities = lucene.query(query, field, n);
		logger.info("results: ", entities);
		List<ArticleDescription> descriptions = new LinkedList<ArticleDescription>();
		for (Integer entity : entities) {
			descriptions.add(get(entity));

		}
		synchronized (lruCache) {
			lruCache.put(q, descriptions);
		}

		logger.info("addde to cash {}", q.hashCode());
		return descriptions;
	}

	private static class Query {
		String query;
		String field;
		int n;

		public Query(String query, String field, int n) {
			super();
			this.query = query;
			this.field = field;
			this.n = n;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((field == null) ? 0 : field.hashCode());
			result = prime * result + n;
			result = prime * result + ((query == null) ? 0 : query.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Query other = (Query) obj;
			if (field == null) {
				if (other.field != null)
					return false;
			} else if (!field.equals(other.field))
				return false;
			if (n != other.n)
				return false;
			if (query == null) {
				if (other.query != null)
					return false;
			} else if (!query.equals(other.query))
				return false;
			return true;
		}

		public String getQuery() {
			return query;
		}

		public void setQuery(String query) {
			this.query = query;
		}

		public String getField() {
			return field;
		}

		public void setField(String field) {
			this.field = field;
		}

		public int getN() {
			return n;
		}

		public void setN(int n) {
			this.n = n;
		}

		@Override
		public String toString() {
			return "Query [query=" + query + ", field=" + field + ", n=" + n
					+ "]";
		}

	}
}
