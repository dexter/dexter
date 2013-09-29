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
package it.cnr.isti.hpc.dexter.tagme;

import it.cnr.isti.hpc.dexter.entity.EntityMatch;
import it.cnr.isti.hpc.dexter.entity.EntityMatchList;
import it.cnr.isti.hpc.dexter.label.IdHelper;
import it.cnr.isti.hpc.dexter.label.IdHelperFactory;
import it.cnr.isti.hpc.dexter.milnewitten.Result;
import it.cnr.isti.hpc.dexter.milnewitten.ResultList;
import it.cnr.isti.hpc.dexter.spot.Spot;
import it.cnr.isti.hpc.dexter.spot.SpotManager;
import it.cnr.isti.hpc.dexter.spot.SpotMatch;
import it.cnr.isti.hpc.dexter.spot.SpotMatchList;
import it.cnr.isti.hpc.dexter.spot.repo.SpotRepository;
import it.cnr.isti.hpc.dexter.spot.repo.SpotRepositoryFactory;
import it.cnr.isti.hpc.log.ProgressLogger;
import it.cnr.isti.hpc.property.ProjectProperties;
import it.cnr.isti.hpc.wikipedia.article.Article;
import it.cnr.isti.hpc.wikipedia.article.Link;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Apr 30, 2013
 */
public class Disambiguator {

	ProjectProperties properties = new ProjectProperties(Disambiguator.class);
	private static final Logger logger = LoggerFactory
			.getLogger(Disambiguator.class);

	SpotRepository spotRepo;
	IdHelper idHelper;

	private double epsilon = 0;
	private int window = 10;

	private int sensesConsidered = 0;

	public Disambiguator(int windowsize, double epsilon) {
		this.window = windowsize;
		this.epsilon = epsilon;
		SpotRepositoryFactory factory = new SpotRepositoryFactory();
		spotRepo = factory.getStdInstance();
		idHelper = IdHelperFactory.getStdIdHelper();
	}

	public Disambiguator() {
		window = properties.getInt("tagme.window.size");
		epsilon = Double.parseDouble(properties.get("tagme.epsilon"));
		SpotRepositoryFactory factory = new SpotRepositoryFactory();
		spotRepo = factory.getStdInstance();
		idHelper = IdHelperFactory.getStdIdHelper();
		
	}

	public void saveClassifier(File file) {

	}

	public void loadClassifier(File file) {
		logger.info("loading classifier");

	}

	public void loadDefaultClassifier() {
		String file = properties.get("wikiminer.classifier");

		loadClassifier(new File(file));
		;
	}

	public double train(Iterable<Article> articles) {
		double max = 0;
		double maxEpsilon = 0;
		for (double j = 0.5; j <= 1; j += 0.05) {
			epsilon = j;
			double precision = disambiguatePrecision(articles);
			logger.info("epsilon = {}, precision = {}", epsilon, precision);
			if (precision > max) {
				max = precision;
				maxEpsilon = epsilon;
			}
		}
		return maxEpsilon;
	}

	public double disambiguatePrecision(Iterable<Article> articles) {

		ProgressLogger pl = new ProgressLogger("tested on {} articles", 1);
		ResultList<Integer> rl = new ResultList<Integer>();

		for (Article art : articles) {

			disambiguatePrecision(art, rl);
			pl.up();
			System.out.println(rl);
		}

		return rl.getPrecision();

	}

	/**
	 * @param art
	 */
	private void disambiguatePrecision(Article art, ResultList<Integer> rl) {
		SpotMatchList sml = new SpotMatchList();

		SpotManager manager = SpotManager.getStandardSpotManager();
		Map<String, List<Integer>> correctIds = new HashMap<String, List<Integer>>();
		// split references into ambiguous and unambiguous
		for (Link a : art.getLinks()) {
			String wikiname = a.getCleanId();
			int wikiId = idHelper.getId(wikiname);
			if (wikiId == IdHelper.NOID)
				continue;
			Set<String> spots = manager.process(a.getDescription());
			for (String spot : spots) {
				Spot s = spotRepo.getSpot(spot);
				if (s == null)
					continue;
				sml.add(new SpotMatch(s, s.getEntities()));

				if (!correctIds.containsKey(spot)) {
					correctIds.put(spot, new ArrayList<Integer>());
				}
				correctIds.get(spot).add(wikiId);

			}

		}
		Set<Integer> corrects = new HashSet<Integer>();
		Set<Integer> disambiguated = new HashSet<Integer>();
			Context context = new Context(sml, window);
			List<SpotMatch> ambiguousSpots = context.getAmbiguousSpots();
			
			
			for (SpotMatch sm : ambiguousSpots) {
				String spot = sm.getSpot().getMention();
				if (correctIds.containsKey(spot))
					corrects.addAll(correctIds.get(spot));
				
				
			}
			for (EntityMatch em : context.getAmbiguousEntities(epsilon)){
				disambiguated.add(em.getId());
			}
			
		
		rl.add(new Result<Integer>(disambiguated, corrects));

	}
	
	public EntityMatchList disambiguate(SpotMatchList sml){
		EntityMatchList eml = new EntityMatchList();
		
			Context context = new Context(sml,window);
			eml.addAll(context.getAllEntities(epsilon));
			eml = context.score(eml);
		
		eml = eml.removeOverlappings();
		eml.sort();
		return eml;
			
		
		
	}

	private List<SpotMatchList> splitSpotsInWindows(SpotMatchList sml) {
		if (sml.isEmpty())
			return Collections.emptyList();
		int size = (sml.size() - 1) / window + 1;
		List<SpotMatchList> windows = new ArrayList<SpotMatchList>(size);
		for (int i = 0; i < size; i++) {
			SpotMatchList w = new SpotMatchList();
			for (int j = 0; (j < window) && ( (i * size + j) < sml.size()) ; j++) {
				w.add(sml.get(i * size + j));
			}
			windows.add(w);
		}
		return windows;
	}

	/**
	 * Tests the disambiguator on a set of Wikipedia articles, to see how well
	 * it makes the same decisions as the original article editors did. You need
	 * to train the disambiguator and build a classifier before using this.
	 * 
	 * @param testSet
	 *            the set of articles to use for testing. You should make sure
	 *            these are reasonably tidy, and roughly representative (in
	 *            size, link distribution, etc) as the documents you intend to
	 *            process automatically.
	 * @param snippetLength
	 *            the portion of each article that should be considered for
	 *            testing (see ArticleCleaner).
	 * @param rc
	 *            a cache in which relatedness measures will be saved so they
	 *            aren't repeatedly calculated. Make this null if using
	 *            extremely large testing sets, so that caches will be reset
	 *            from document to document, and won't grow too large.
	 * @return Result a result (including recall, precision, f-measure) of how
	 *         well the classifier did.
	 * @throws SQLException
	 *             if there is a problem with the WikipediaMiner database.
	 * @throws Exception
	 *             if there is a problem with the classifier
	 */
	// public ResultList<Integer> test(Iterable<Article> testSet) {
	//
	// // if (!decider.isReady())
	// // throw new WekaException("You must build (or load) classifier first.")
	// // ;
	//
	// ResultList<Integer> r = new ResultList<Integer>();
	//
	// double worstRecall = 1;
	// double worstPrecision = 1;
	//
	// int articlesTested = 0;
	// int perfectRecall = 0;
	// int perfectPrecision = 0;
	//
	// ProgressLogger pl = new ProgressLogger("Tested on {} articles", 1);
	// for (Article art : testSet) {
	//
	// articlesTested++;
	//
	// Result<Integer> ir = test(art);
	//
	// if (ir.getRecall() == 1)
	// perfectRecall++;
	// if (ir.getPrecision() == 1)
	// perfectPrecision++;
	//
	// worstRecall = Math.min(worstRecall, ir.getRecall());
	// worstPrecision = Math.min(worstPrecision, ir.getPrecision());
	//
	// r.add(ir);
	//
	// pl.up();
	// }
	// System.out.println(r);
	//
	// return r;
	// }
	//

	// private Result<Integer> test(Article art) {
	//
	// System.out.println(" - testing " + art.getTitle());
	//
	// SpotMatchList sml = new SpotMatchList();
	//
	// SpotManager manager = SpotManager.getStandardSpotManager();
	// Map<String, List<Integer>> correctIds = new HashMap<String,
	// List<Integer>>();
	// Set<Integer> goldStandard = new HashSet<Integer>();
	// Map<String,Integer> spot2golden = new HashMap<String,Integer>();
	// // split references into ambiguous and unambiguous
	// for (Link a : art.getLinks()) {
	// String wikiname = a.getCleanId();
	// Integer wikiId = idHelper.getId(wikiname);
	// if (wikiId == IdHelper.NOID)
	// continue;
	//
	// Set<String> spots = manager.process(a.getDescription());
	// for (String spot : spots) {
	// Spot s = spotRepo.getSpot(spot);
	// if (s == null)
	// continue;
	//
	// sml.add(new SpotMatch(s, s.getEntities()));
	//
	// spot2golden.put(s.getSpot(),wikiId);
	//
	//
	// }
	//
	// }
	// Context context = new Context(sml);
	// List<SpotMatch> ambiguousSpots = context.getAmbiguousSpots();
	// for (SpotMatch sm : ambiguousSpots){
	// goldStandard.add(spot2golden.get(sm.getSpot().getSpot()));
	// }
	// float priorProbabilityThreshold = context
	// .getPriorProbabilityThreshold();
	// // only use links
	// // Context context = new Context(unambigLabels, rc, maxContextSize) ;
	//
	// // resolve senses
	// Set<Integer> disambiguatedLinks = new HashSet<Integer>();
	// EntityMatch best;
	//
	// // FIXME refactor using getDisambiguatetEntities
	// for (SpotMatch sm : ambiguousSpots) {
	// best = null;
	// for (EntityMatch em : sm.getEntities()) {
	// if (em.getPriorProbability() < priorProbabilityThreshold)
	// continue;
	//
	// double prob = 0;
	// try {
	// prob = getProbabilityOfSense(em.getPriorProbability(),
	// context.getRelatednessTo(em), context);
	//
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// if (prob < 0.5)
	// continue;
	// em.setScore(prob);
	// if (best == null || best.getScore() < prob) {
	// best = em;
	// }
	//
	// }
	// if (best != null) {
	// disambiguatedLinks.add(best.getId());
	// }
	//
	// }
	//
	// Result<Integer> result = new Result<Integer>(disambiguatedLinks,
	// goldStandard);
	// logger.info("-----------");
	// logger.info("disambiguated {}", disambiguatedLinks);
	//
	// logger.info("goldStandard {}", goldStandard);
	// logger.info("-----------");
	//
	// return result;
	// }
	//
	//
	// public EntityMatchList getDisambiguatedEntities(Context context){
	// List<SpotMatch> ambiguousSpots = context.getAmbiguousSpots();
	// EntityMatchList disambiguatedLinks = new EntityMatchList();
	// EntityMatch best = null;
	// for (SpotMatch sm : ambiguousSpots) {
	// best = null;
	// for (EntityMatch em : sm.getEntities()) {
	// if (em.getPriorProbability() < priorProbabilityThreshold)
	// continue;
	//
	// double prob = 0;
	// try {
	// prob = getProbabilityOfSense(em.getPriorProbability(),
	// context.getRelatednessTo(em), context);
	//
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// if (prob < 0.5)
	// continue;
	// em.setScore(prob);
	// if (best == null || best.getScore() < prob) {
	// best = em;
	// }
	//
	// }
	// if (best != null) {
	// disambiguatedLinks.add(best);
	// }
	//
	// }
	// return disambiguatedLinks;
	// }
	//
	// public EntityMatchList getNonAmbiguousEntities(Context context){
	// return context.getNotAmbiguousEntities();
	// }
	//
	// public EntityMatchList disambiguate(Context context){
	// EntityMatchList eml = new EntityMatchList();
	// eml.addAll(getDisambiguatedEntities(context));
	//
	// EntityMatchList nonAmbiguous = getNonAmbiguousEntities(context);
	// eml.addAll(nonAmbiguous);
	// for (EntityMatch em : eml){
	// em.setScore((em.getSpotProbability()*em.getPriorProbability()*context.getRelatednessTo(em)));
	//
	//
	// }
	// return eml;
	//
	// }
	//
	// /**
	// * returns the probability (between 0 and 1) of a sense with the given
	// * commonness and relatedness being valid given the available context.
	// *
	// * @param commonness
	// * the commonness of the sense (it's prior probability,
	// * irrespective of context)
	// * @param relatedness
	// * the relatedness of the sense to the given context (the result
	// * of calling context.getRelatednessTo()
	// * @param context
	// * the available context.
	// * @return the probability that the sense implied here is valid.
	// * @throws Exception
	// * if we cannot classify this sense.
	// */
	// public double getProbabilityOfSense(double commonness, double
	// relatedness,
	// Context context) {
	//
	// Instance i = null;
	// try {
	// i = decider
	// .getInstanceBuilder()
	// .setAttribute(Attributes.COMMONNESS, commonness)
	// .setAttribute(Attributes.RELATEDNESS, relatedness)
	// .setAttribute(Attributes.CONTEXT_QUALITY,
	// (double) context.getQuality()).build();
	// } catch (ClassMissingException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (AttributeMissingException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// sensesConsidered++;
	//
	// try {
	// return decider.getDecisionDistribution(i).get(true);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// System.exit(-1);
	// }
	// return -1;
	// }
	//
	// public void buildDefaultClassifier() {
	// Classifier classifier = new Bagging();
	// try {
	// classifier
	// .setOptions(Utils
	// .splitOptions("-P 10 -S 1 -I 10 -W weka.classifiers.trees.J48 -- -U -M 2"));
	//
	// decider.train(classifier, dataset);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

}
