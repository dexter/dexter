package it.cnr.isti.hpc.dexter.lucene;

/**
 *  Copyright 2012 Salvatore Trani
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

import it.cnr.isti.hpc.dexter.entity.EntityMatch;
import it.cnr.isti.hpc.dexter.entity.EntityMatchList;
import it.cnr.isti.hpc.dexter.spot.Spot;
import it.cnr.isti.hpc.dexter.spot.SpotManager;
import it.cnr.isti.hpc.dexter.spot.clean.QuotesCleaner;
import it.cnr.isti.hpc.dexter.spot.clean.UnderscoreCleaner;
import it.cnr.isti.hpc.dexter.spot.clean.UnicodeCleaner;
import it.cnr.isti.hpc.log.ProgressLogger;
import it.cnr.isti.hpc.property.ProjectProperties;
import it.cnr.isti.hpc.text.Text;
import it.cnr.isti.hpc.wikipedia.article.Article;
import it.cnr.isti.hpc.wikipedia.article.ArticleSummarizer;
import it.cnr.isti.hpc.wikipedia.article.Link;
import it.cnr.isti.hpc.wikipedia.article.Template;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuceneHelper {

	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(LuceneHelper.class);

	StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_41);

	static LuceneHelper dexterHelper;

	private static final String QUERY_FIELD = "context";

	/**
	 * Logger for this class
	 */

	private Directory index;
	private IndexWriter writer;
	private IndexReader reader;
	private IndexSearcher searcher;
	private IndexWriterConfig config;
	private ArticleSummarizer summarizer;
	private SpotManager sm = new SpotManager();

	private static ProjectProperties properties = new ProjectProperties(
			LuceneHelper.class);

	private StringBuilder sb = new StringBuilder();

	private static SpotManager cleaner = new SpotManager();

	static Map<Integer, Integer> wikiIdToLuceneId;

	static {
		cleaner.add(new UnicodeCleaner());
		cleaner.add(new UnderscoreCleaner());
		cleaner.add(new QuotesCleaner());
	}

	public LuceneHelper(String indexPath) {
		logger.info("opening lucene index in folder {}", indexPath);
		config = new IndexWriterConfig(Version.LUCENE_41, new StandardAnalyzer(
				Version.LUCENE_41));
		// FIXME add empty set of stopworkds?

		BooleanQuery.setMaxClauseCount(1000);

		try {
			index = FSDirectory.open(new File(indexPath));
			// writer.commit();
		} catch (Exception e) {
			logger.error("opening the index: {}", e.toString());
			System.exit(1);
		}
		summarizer = new ArticleSummarizer();

	}

	public static LuceneHelper getDexterLuceneHelper() {
		if (dexterHelper == null) {
			dexterHelper = new LuceneHelper(properties.get("lucene.index"));
		}
		return dexterHelper;
	}

	private void parseWikiIdToLuceneId() {
		logger.warn("no index wikiID -> lucene found - I'll generate");
		reader = getReader();
		wikiIdToLuceneId = new HashMap<Integer, Integer>(reader.numDocs());
		ProgressLogger pl = new ProgressLogger(
				"creating wiki2lucene, readed {} docs", 100000);
		int numDocs = reader.numDocs();
		for (int i = 0; i < numDocs; i++) {
			pl.up();
			try {
				Document doc = reader.document(i);
				IndexableField f = doc.getField("wiki-id");
				Integer wikiId = new Integer(f.stringValue());
				wikiIdToLuceneId.put(wikiId, i);
			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public void dumpWikiIdToLuceneId() {
		String file = properties.get("lucene.wiki.id");
		try {
			// Serialize to a file
			ObjectOutput out = new ObjectOutputStream(
					new FileOutputStream(file));
			out.writeObject(wikiIdToLuceneId);
			out.close();
		} catch (IOException e) {
			logger.info("dumping incoming links in a file ({})", e.toString());
			System.exit(-1);
		}
	}

	public void loadWikiIdToLuceneId() {
		File file = new File(properties.get("lucene.wiki.id"));
		if (!file.exists()) {
			logger.info("{} not exists, generating");
			parseWikiIdToLuceneId();
			logger.info("storing");
			dumpWikiIdToLuceneId();
			return;
		}

		logger.info("loading wiki id to lucene id ");
		try {

			InputStream is = new BufferedInputStream(new FileInputStream(file));
			ObjectInput oi = new ObjectInputStream(is);
			wikiIdToLuceneId = (Map<Integer, Integer>) oi.readObject();

		} catch (Exception e) {
			logger.info("reading serialized object ({})", e.toString());
			System.exit(-1);
		}
		logger.info("done ");
	}

	private int getLuceneId(int wikiId) {
		if (wikiIdToLuceneId == null)
			loadWikiIdToLuceneId();

		if (!wikiIdToLuceneId.containsKey(wikiId))
			return -1;
		return wikiIdToLuceneId.get(wikiId);
	}

	public float getSimilarity(Query query, int wikiId) {
		int docId = getLuceneId(wikiId);
		searcher = getSearcher();
		Explanation e = null;
		try {
			e = searcher.explain(query, docId);
		} catch (IOException e1) {
			logger.error("getting similarity between text and doc {} ", wikiId);
			return 0;
		}
		return e.getValue();
	}

	// public double getCosineSimilarity(int wikiIdX, int wikiIdY) {
	//
	// int idX = getLuceneId(wikiIdX);
	// int idY = getLuceneId(wikiIdY);
	// reader = getReader();
	//
	//
	// Terms tfvX = null;
	// Terms tfvY = null;
	// try{
	// tfvX = reader.getTermVector(idX,"content");
	// tfvY = reader.getTermVector(idY,"content");
	// // try {
	// // tfvX = reader.document(idX).getBinaryValue("asd")
	// // getTermFreqVectors(idX);
	// // tfvY = reader.getTermFreqVectors(idY);
	// } catch (IOException e) {
	// logger.error("computing cosine similarity ({}) ", e.toString());
	// System.exit(-1);
	// }
	// DocVector dvX = new DocVector(tfvX);
	// DocVector dvY = new DocVector(tfvY);
	//
	// return dvX.getCosineSimilarity(dvY);
	//
	// }

	private Document toLuceneDocument(Article a) {
		Document d = new Document();
		d.add(new Field("title", a.getTitle(), Field.Store.YES,
				Field.Index.ANALYZED));
		d.add(new Field("wiki-id", String.valueOf(a.getWid()), Field.Store.YES,
				Field.Index.ANALYZED));
		d.add(new Field("wiki-title", a.getWikiTitle(), Field.Store.YES,
				Field.Index.NOT_ANALYZED));
		d.add(new Field("type", String.valueOf(a.getType()), Field.Store.YES,
				Field.Index.ANALYZED));
		for (List<String> l : a.getLists()) {
			for (String e : l)
				d.add(new Field("lists", e, Field.Store.NO,
						Field.Index.ANALYZED));
		}
		Template t = a.getInfobox();
		d.add(new Field("infobox", t.getName(), Field.Store.YES,
				Field.Index.ANALYZED));
		for (String e : t.getDescription()) {
			d.add(new Field("infobox", e, Field.Store.YES, Field.Index.ANALYZED));
		}
		for (String e : a.getHighlights()) {
			d.add(new Field("emph", e, Field.Store.NO, Field.Index.ANALYZED,
					Field.TermVector.YES));
		}
		for (String e : a.getSections()) {
			d.add(new Field("sections", e, Field.Store.NO, Field.Index.ANALYZED));
		}

		for (Link e : a.getLinks()) {
			d.add(new Field("desc", cleaner.clean(e.getDescription()),
					Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.YES));
			d.add(new Field("link", cleaner.clean(e.getCleanId().replace('_',
					' ')), Field.Store.NO, Field.Index.ANALYZED,
					Field.TermVector.YES));

		}

		d.add(new Field("content", cleaner.clean(a.getText()), Field.Store.NO,
				Field.Index.ANALYZED, Field.TermVector.YES));
		d.add(new Field("summary", summarizer.getSummary(a), Field.Store.YES,
				Field.Index.ANALYZED, Field.TermVector.YES));
		return d;
	}

	public void addDocument(Article art) {
		writer = getWriter();
		// Document doc = new Document();
		// ArticleContextExtractor extractor = new ArticleContextExtractor(art);

		// doc.add(new Field("id", String.valueOf(art.getWikiId()),
		// Field.Store.YES,
		// Field.Index.ANALYZED));
		//
		// String content = cleaner.clean(extractor.getCleanAsciiText(art));
		//
		// // System.out.print("asd");
		// // System.out.println(content);
		//
		// doc.add(new Field("content", content, Field.Store.YES,
		// Field.Index.ANALYZED, Field.TermVector.YES));
		// // doc.add(new Field("label-ids", getLabels(), Field.Store.YES,
		// // Field.Index.ANALYZED));

		logger.debug("add doc {} ", art.getTitle());
		try {
			writer.addDocument(toLuceneDocument(art));
			// writer.addDocument(doc);
		} catch (Exception e) {
			logger.error("exception indexing a document: {} ({})",
					art.getTitle(), e.toString());
			e.printStackTrace();
			System.exit(1);
		}
		logger.info("added doc {}", art.getWid());

		// reset reader and searcher so it will not be inconsistency after the
		// insert into the index
		// reader = null;
		// searcher = null;
	}

	public void addDocument(int id, String content) {
		Article a = new Article();
		a.setWid(id);
		a.setParagraphs(Arrays.asList(content));
		addDocument(a);
		// Document doc = new Document();
		// writer = getWriter();
		// doc.add(new Field("id", id, Field.Store.YES,
		// Field.Index.NOT_ANALYZED));
		// doc.add(new Field("content", content, Field.Store.YES,
		// Field.Index.ANALYZED, Field.TermVector.YES));
		// try {
		// writer.addDocument(doc);
		// } catch (Exception e) {
		// logger.error("exception indexing a document: {} ({})", "id " + id,
		// e.toString());
		// System.exit(1);
		// }
	}

	public void clearIndex() {
		writer = getWriter();
		logger.info("delete all the documents indexed");
		try {
			writer.deleteAll();
			writer.commit();
		} catch (IOException e) {
			logger.error("deleting the index: {}", e.toString());
			System.exit(1);
		}
	}

	public void commit() {
		writer = getWriter();
		try {
			writer.commit();
			// logger.info("commited, index contains {} documents", writer
			// .getReader().numDocs());
		} catch (Exception e) {
			logger.error("committing: {}", e.toString());
			System.exit(1);
		}

		System.out.println();
	}

	public Document getDoc(int wikiId) {
		// System.out.println("get docId "+pos);
		if (wikiId <= 0)
			return null;
		int docId = getLuceneId(wikiId);
		if (docId == 0) {
			logger.warn("no id for wikiId {}", wikiId);

			return null;
		}
		logger.debug("get wikiId {}  ->  docId {}", wikiId, docId);
		Document doc = null;
		try {
			doc = getReader().document(docId);
		} catch (Exception e) {
			logger.error("retrieving doc in position {} {}", docId,
					e.toString());
			System.exit(-1);
		}

		return doc;
	}

	public int getFreq(String query, String field) {
		searcher = getSearcher();
		Query q = null;

		TopScoreDocCollector collector = TopScoreDocCollector.create(1, true);

		// try {

		Text t = new Text(query).disableStopwords();
		PhraseQuery pq = new PhraseQuery();
		int i = 0;
		for (String term : t.getTerms()) {
			pq.add(new Term(field, term), i++);
		}
		q = pq;
		logger.debug(q.toString());
		// } catch (ParseException e) {
		// logger.error("querying the index: {} ", e.toString());
		// return -1;
		// }
		try {
			searcher.search(q, collector);
		} catch (IOException e) {
			logger.error("querying the index: {} ", e.toString());
			return -1;
		}
		return collector.getTotalHits();
	}

	public int getFreq(String query) {

		return getFreq(query, "content");
	}

	public IndexReader getReader() {
		if (reader != null)
			return reader;
		try {
			reader = IndexReader.open(index);
		} catch (Exception e) {
			logger.error("reading the index: {} ", e.toString());
			System.exit(1);
		}
		return reader;
	}

	// public String getLabelHashes(Article article) {
	//
	// }

	public IndexSearcher getSearcher() {
		if (searcher != null)
			return searcher;
		reader = getReader();
		searcher = new IndexSearcher(reader);
		return searcher;
	}

	private IndexWriter getWriter() {
		if (writer == null)
			try {
				writer = new IndexWriter(index, config);
			} catch (CorruptIndexException e1) {
				logger.error("creating the index: {}", e1.toString());
				System.exit(-1);
			} catch (LockObtainFailedException e1) {
				logger.error("creating the index: {}", e1.toString());
				System.exit(-1);
			} catch (IOException e1) {
				logger.error("creating the index: {}", e1.toString());
				System.exit(-1);
			}
		return writer;
	}

	public int numDocs() {
		return getReader().numDocs();

	}

	public List<Integer> query(String query) {

		searcher = getSearcher();
		TopScoreDocCollector collector = TopScoreDocCollector.create(10000,
				true);
		List<Integer> results = new ArrayList<Integer>();
		Query q = null;

		try {
			q = new QueryParser(Version.LUCENE_41, "content",
					new StandardAnalyzer(Version.LUCENE_41)).parse("\"" + query
					+ "\"");
		} catch (ParseException e) {
			logger.error("querying the index: {} ", e.toString());
			return results;
		}

		try {
			searcher.search(q, collector);
		} catch (IOException e) {
			logger.error("querying the index: {} ", e.toString());
			return results;
		}

		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			results.add(docId);
		}

		logger.debug("query {} docs {}", query, results);
		return results;
	}

	public Article getArticle(int id) {
		Article a = new Article();
		reader = getReader();
		a.setWikiId(id);

		Document d = getDoc(id);
		if (d != null) {
			List<String> paragraphs = new ArrayList<String>();
			paragraphs.add(d.getField("content").stringValue());
			a.setParagraphs(paragraphs);
		}
		//
		return a;

	}

	public void rankBySimilarity(Spot spot, EntityMatchList eml, String context) {

		if (context.trim().isEmpty()) {
			logger.warn("no context for spot {}", spot.getText());
			return;
		}

		Query q = null;

		 try {
		// removing all not alphanumerical chars
		context = context.replaceAll("[^A-Za-z0-9 ]", " ");

		q = new QueryParser(Version.LUCENE_41, "content", new StandardAnalyzer(
				Version.LUCENE_41)).parse(QueryParser.escape(context));
		} catch (ParseException e) {
		logger.error("querying the index: {} ", e.toString());
		 logger.error("clauses = {} ",
				 ((BooleanQuery) q).getClauses().length);
		// logger.error("QUERY = {} ", context);
				 return ;
		 }
		// logger.debug("query = {} ", q);
		//
		// logger.debug("filter = {}", filterQuery);
		//
		// try {
		// searcher.search(q, new QueryWrapperFilter(filterQuery), collector);
		// } catch (IOException e) {
		// logger.error("querying the index: {} ", e.toString());
		// return results;
		// }
		// ScoreDoc[] hits = collector.topDocs().scoreDocs;
		// for (int i = 0; i < hits.length; ++i) {
		// int docId = hits[i].doc;
		// int entityId = Integer.parseInt(getDoc(docId).get("id"));
		//
		// double score = hits[i].score;
		// EntityMatch em = new EntityMatch(entityId, score, spot);
		// em.setExplanation(new it.cnr.isti.hpc.dexter.util.Explanation(score,
		// "Compatible Relation"));
		// results.add(em);
		// }
		for (EntityMatch e : eml) {
			Integer luceneId = getLuceneId(e.getId());
			float score = 0.5f;
			// smoothing
			if (luceneId == null || luceneId < 0) {
				// logger.warn("no docs in lucene for wiki id {}, ignoring",
				// e.id());
			} else {
				score += getSimilarity(q, e.getId());

			}
			e.setScore(score);
		}

		return;

	}

	private static int collectionSize = 0;

	// public class DocVector {
	//
	// Map<String, Double> term2tfidf;
	// double norm = 0;
	//
	// public DocVector(Terms terms) {
	// double total = 0;
	// if (searcher == null)
	// searcher = getSearcher();
	// if (collectionSize == 0)
	// collectionSize = searcher.getIndexReader().numDocs();
	// term2tfidf = new HashMap<String, Double>();
	// //for (TermFreqVector tfv : tfvs) {
	// //if (! tfv.getField().equals("content")) continue;
	// TermsEnum te = null;
	// te = terms.iterator(te);
	// while (te.next())
	//
	// String[] termTexts = terms.
	// int[] termFreqs = tfv.getTermFrequencies();
	// // Assert.assertEquals(termTexts.length, termFreqs.length);
	// for (int j = 0; j < termTexts.length; j++) {
	// double tfidf = setEntry(termTexts[j], termFreqs[j]);
	//
	// total += tfidf*tfidf;
	// logger.info("TOTAL {} ", total);
	//
	// }
	//
	// }
	// System.out.println("total = " + total);
	// norm = Math.sqrt(total);
	// //logger.info("---> NORM {} ", norm);
	// }
	//
	// private double getCosineSimilarity(DocVector d2) {
	// double dotproduct = 0;
	// for (Map.Entry<String, Double> entry : d2.term2tfidf.entrySet()) {
	// String key = entry.getKey();
	// System.out.println(key + "\t " + entry.getValue());
	//
	// if (term2tfidf.containsKey(key)) {
	// dotproduct += term2tfidf.get(key) * entry.getValue();
	// //System.out.println("dotproduct = " + dotproduct);
	// }
	// }
	// System.out.println("norm = " + norm);
	// System.out.println("norm2 = " + d2.norm);
	//
	// System.out.println("dot = " + dotproduct);
	//
	//
	// return dotproduct / (norm * d2.norm);
	// }
	//
	// public double setEntry(String term, int freq) {
	//
	// int df = 0;
	// try {
	// df = searcher.docFreq(new Term("content", term));
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// double idf = Math.log(collectionSize / (double) df) / Math.log(2);
	// double tfidf = freq * idf;
	// // logger.info("term \t {}",term);
	// // logger.info("tf \t {}",freq);
	// // logger.info("df \t {}",df);
	// // logger.info("idf \t {}",idf);
	// // logger.info("tfidf \t {}",tfidf);
	// // logger.info("term {}  idf {}",term,tfidf);
	// term2tfidf.put(term, tfidf);
	// return tfidf;
	//
	// }
	//
	// }

}
