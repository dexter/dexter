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
package it.cnr.isti.hpc.dexter.milnewitten;

import it.cnr.isti.hpc.dexter.entity.Entity;
import it.cnr.isti.hpc.dexter.entity.EntityMatch;
import it.cnr.isti.hpc.dexter.entity.EntityMatchList;
import it.cnr.isti.hpc.dexter.hash.IdHelper;
import it.cnr.isti.hpc.dexter.hash.IdHelperFactory;
import it.cnr.isti.hpc.dexter.spot.Spot;
import it.cnr.isti.hpc.dexter.spot.SpotManager;
import it.cnr.isti.hpc.dexter.spot.SpotMatch;
import it.cnr.isti.hpc.dexter.spot.SpotMatchList;
import it.cnr.isti.hpc.dexter.spot.Spotter;
import it.cnr.isti.hpc.dexter.spot.repo.SpotRepository;
import it.cnr.isti.hpc.dexter.spot.repo.SpotRepositoryFactory;
import it.cnr.isti.hpc.log.ProgressLogger;
import it.cnr.isti.hpc.property.ProjectProperties;
import it.cnr.isti.hpc.wikipedia.article.Article;
import it.cnr.isti.hpc.wikipedia.article.Link;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.id.uuid.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tudarmstadt.ukp.wikipedia.mwdumper.dumper.ProgressFilter;
import weka.classifiers.Classifier;
import weka.classifiers.meta.Bagging;
import weka.core.Instance;
import weka.core.Utils;
import weka.wrapper.AttributeMissingException;
import weka.wrapper.ClassMissingException;
import weka.wrapper.Dataset;
import weka.wrapper.Decider;
import weka.wrapper.DeciderBuilder;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Apr 30, 2013
 */
public class Disambiguator {

	
	

	ProjectProperties properties = new ProjectProperties(Disambiguator.class);
	private static final Logger logger = LoggerFactory
			.getLogger(Disambiguator.class);
	private double priorProbabilityThreshold;
	private int maxLabelLength = 20;
	private double minLinkProbability;
	private int maxContextSize;
	
	

	private enum Attributes {
		COMMONNESS, RELATEDNESS, CONTEXT_QUALITY
	};

	SpotRepository spotRepo;
	IdHelper idHelper;

	private Dataset<Attributes, Boolean> dataset;
	private Decider<Attributes, Boolean> decider;

	private int sensesConsidered = 0;

	public Disambiguator() {
		SpotRepositoryFactory factory = new SpotRepositoryFactory();
		spotRepo = factory.getStdInstance();
		idHelper = IdHelperFactory.getStdIdHelper();
		String threshold = properties.get("prior.threshold");
		if (threshold == null){
			logger.error("no value set for prior threshold");
			System.exit(-1);
		}
		priorProbabilityThreshold = Float.parseFloat(threshold);
		init(0.05, 20);
	}

	private void init(double minLinkProbability,
			int maxContextSize) {

		this.minLinkProbability = minLinkProbability;
		this.maxContextSize = maxContextSize;

		try {
			decider = (Decider<Attributes, Boolean>) new DeciderBuilder<Attributes>(
					"LinkDisambiguator", Attributes.class)
					.setDefaultAttributeTypeNumeric()
					.setClassAttributeTypeBoolean("isCorrectSense").build();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * attributes = new FastVector() ;
	 * 
	 * attributes.addElement(new Attribute("commoness")) ;
	 * attributes.addElement(new Attribute("relatedness")) ;
	 * attributes.addElement(new Attribute("context_quality")) ;
	 * 
	 * FastVector bool = new FastVector(); bool.addElement("TRUE") ;
	 * bool.addElement("FALSE") ; attributes.addElement(new
	 * Attribute("isValidSense", bool)) ;
	 * 
	 * this.header = new Instances("disambiguation_headerOnly", attributes, 0) ;
	 * header.setClassIndex(header.numAttributes() -1) ;
	 */
	
	public void saveClassifier(File file)  {
		logger.info("saving training data") ;
		
		try {
			decider.save(file) ;
		} catch (Exception e) {
			logger.error("saving training data ({})",e.toString());
			System.exit(-1);
		}
	}
	
	
	public void loadClassifier(File file) {
		logger.info("loading classifier") ;

		try {
			decider.load(file) ;
		} catch (Exception e) {
			logger.error("loading wikiminer classifier ({})",e.toString());
			System.exit(-1);
		}
	}
	
	public void loadDefaultClassifier(){
		String file = properties.get("wikiminer.classifier");
		
		loadClassifier(new File(file));;
	}
	
	
	

	public void train(Iterable<Article> articles) {

		dataset = decider.createNewDataset();

		ProgressLogger pl = new ProgressLogger("trained on {} articles", 1);
		for (Article art : articles) {

			train(art);
			pl.up();
		}

		weightTrainingInstances();

		// training data is very likely to be skewed. So lets resample to even
		// out class values
		// Resample resampleFilter = new Resample() ;
		// resampleFilter.setBiasToUniformClass(1) ;

		// decider.applyFilter(resampleFilter) ;
	}

	/**
	 * 
	 */
	private void weightTrainingInstances() {
		double positiveInstances = 0;
		double negativeInstances = 0;

		Enumeration<Instance> e = dataset.enumerateInstances();

		while (e.hasMoreElements()) {
			Instance i = (Instance) e.nextElement();

			double isValidSense = i.value(3);

			if (isValidSense == 0)
				positiveInstances++;
			else
				negativeInstances++;
		}

		double p = (double) positiveInstances
				/ (positiveInstances + negativeInstances);

		e = dataset.enumerateInstances();

		while (e.hasMoreElements()) {
			Instance i = (Instance) e.nextElement();

			double isValidSense = i.value(3);

			if (isValidSense == 0)
				i.setWeight(0.5 * (1.0 / p));
			else
				i.setWeight(0.5 * (1.0 / (1 - p)));
		}

	}

	/**
	 * @param art
	 */
	private void train(Article art) {
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
		Context context = new Context(sml);
		List<SpotMatch> ambiguousSpots = context.getAmbiguousSpots();
		float priorProbabilityThreshold = context
				.getPriorProbabilityThreshold();
		
		for (SpotMatch sm : ambiguousSpots) {
			List<Integer> corrects = correctIds.get(sm.getSpot().getMention());
			if (corrects == null)
				corrects = Collections.emptyList();
			float contextQuality = context.getQuality();
			logger.info("context quality: {}",contextQuality);
			for (EntityMatch em : sm.getEntities()) {
				if (em.getPriorProbability() < priorProbabilityThreshold)
					continue;
				Instance i = null;
				
				double entityRelatedness = context.getRelatednessTo(em);
				double priorProbability = em.getPriorProbability();
				try {
					i = decider
							.getInstanceBuilder()
							.setAttribute(Attributes.COMMONNESS,
									priorProbability)
							.setAttribute(Attributes.RELATEDNESS,
									entityRelatedness)
							.setAttribute(Attributes.CONTEXT_QUALITY,
									(double) contextQuality)
							.setClassAttribute(corrects.contains(em.getId()))
							.build();
					
//					logger.info("entity relatedness: {}",entityRelatedness);
//					logger.info("entity priorProbability: {}",priorProbability);
//					logger.info("entity priorProbability: {}",corrects.contains(em.getId()));

				} catch (ClassMissingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (AttributeMissingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				

				dataset.add(i);
			}
		}

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
	public ResultList<Integer> test(Iterable<Article> testSet) {

		// if (!decider.isReady())
		// throw new WekaException("You must build (or load) classifier first.")
		// ;

		ResultList<Integer> r = new ResultList<Integer>();

		double worstRecall = 1;
		double worstPrecision = 1;

		int articlesTested = 0;
		int perfectRecall = 0;
		int perfectPrecision = 0;

		ProgressLogger pl = new ProgressLogger("Tested on {} articles", 1);
		for (Article art : testSet) {

			articlesTested++;

			Result<Integer> ir = test(art);

			if (ir.getRecall() == 1)
				perfectRecall++;
			if (ir.getPrecision() == 1)
				perfectPrecision++;

			worstRecall = Math.min(worstRecall, ir.getRecall());
			worstPrecision = Math.min(worstPrecision, ir.getPrecision());

			r.add(ir);

			pl.up();
		}
		System.out.println(r);

		return r;
	}
	
	
	

	private Result<Integer> test(Article art) {

		System.out.println(" - testing " + art.getTitle());

		SpotMatchList sml = new SpotMatchList();

		SpotManager manager = SpotManager.getStandardSpotManager();
		Map<String, List<Integer>> correctIds = new HashMap<String, List<Integer>>();
		Set<Integer> goldStandard = new HashSet<Integer>();
		Map<String,Integer> spot2golden = new HashMap<String,Integer>();
		// split references into ambiguous and unambiguous
		for (Link a : art.getLinks()) {
			String wikiname = a.getCleanId();
			Integer wikiId = idHelper.getId(wikiname);
			if (wikiId == IdHelper.NOID)
				continue;
			
			Set<String> spots = manager.process(a.getDescription());
			for (String spot : spots) {
				Spot s = spotRepo.getSpot(spot);
				if (s == null)
					continue;
				
				sml.add(new SpotMatch(s, s.getEntities()));
				
				spot2golden.put(s.getMention(),wikiId);
				

			}

		}
		Context context = new Context(sml);
		List<SpotMatch> ambiguousSpots = context.getAmbiguousSpots();
		for (SpotMatch sm : ambiguousSpots){
			goldStandard.add(spot2golden.get(sm.getSpot().getMention()));
		}
		float priorProbabilityThreshold = context
				.getPriorProbabilityThreshold();
		// only use links
		// Context context = new Context(unambigLabels, rc, maxContextSize) ;

		// resolve senses
		Set<Integer> disambiguatedLinks = new HashSet<Integer>();
		EntityMatch best;

		// FIXME refactor using getDisambiguatetEntities
		for (SpotMatch sm : ambiguousSpots) {
			best = null;
			for (EntityMatch em : sm.getEntities()) {
				if (em.getPriorProbability() < priorProbabilityThreshold)
					continue;

				double prob = 0;
				try {
					prob = getProbabilityOfSense(em.getPriorProbability(),
							context.getRelatednessTo(em), context);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (prob < 0.5)
					continue;
				em.setScore(prob);
				if (best == null || best.getScore() < prob) {
					best = em;
				}

			}
			if (best != null) {
				disambiguatedLinks.add(best.getId());
			}

		}

		Result<Integer> result = new Result<Integer>(disambiguatedLinks,
				goldStandard);
		logger.info("-----------");
		logger.info("disambiguated {}", disambiguatedLinks);
		
		logger.info("goldStandard {}", goldStandard);
		logger.info("-----------");

		return result;
	}
	
	
	public EntityMatchList getDisambiguatedEntities(Context context){
		List<SpotMatch> ambiguousSpots = context.getAmbiguousSpots();
		EntityMatchList disambiguatedLinks = new EntityMatchList();
		EntityMatch best = null;
		for (SpotMatch sm : ambiguousSpots) {
			best = null;
			for (EntityMatch em : sm.getEntities()) {
				if (em.getPriorProbability() < priorProbabilityThreshold)
					continue;

				double prob = 0;
				try {
					prob = getProbabilityOfSense(em.getPriorProbability(),
							context.getRelatednessTo(em), context);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (prob < 0.5)
					continue;
				em.setScore(prob);
				if (best == null || best.getScore() < prob) {
					best = em;
				}

			}
			if (best != null) {
				disambiguatedLinks.add(best);
			}

		}
		return disambiguatedLinks;
	}
	
	public EntityMatchList getNonAmbiguousEntities(Context context){
		return  context.getNotAmbiguousEntities();
	}
	
	public EntityMatchList disambiguate(Context context){
		EntityMatchList eml = new EntityMatchList();
		eml.addAll(getDisambiguatedEntities(context));
		
		EntityMatchList nonAmbiguous = getNonAmbiguousEntities(context);
		eml.addAll(nonAmbiguous);
		for (EntityMatch em : eml){
			em.setScore((em.getSpotLinkProbability()*em.getPriorProbability()*context.getRelatednessTo(em)));
			
				
		}
		return eml;
		
	}

	/**
	 * returns the probability (between 0 and 1) of a sense with the given
	 * commonness and relatedness being valid given the available context.
	 * 
	 * @param commonness
	 *            the commonness of the sense (it's prior probability,
	 *            irrespective of context)
	 * @param relatedness
	 *            the relatedness of the sense to the given context (the result
	 *            of calling context.getRelatednessTo()
	 * @param context
	 *            the available context.
	 * @return the probability that the sense implied here is valid.
	 * @throws Exception
	 *             if we cannot classify this sense.
	 */
	public double getProbabilityOfSense(double commonness, double relatedness,
			Context context)  {

		Instance i = null;
		try {
			i = decider
					.getInstanceBuilder()
					.setAttribute(Attributes.COMMONNESS, commonness)
					.setAttribute(Attributes.RELATEDNESS, relatedness)
					.setAttribute(Attributes.CONTEXT_QUALITY,
							(double) context.getQuality()).build();
		} catch (ClassMissingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AttributeMissingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		sensesConsidered++;

		try {
			return decider.getDecisionDistribution(i).get(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		return -1;
	}

	public void buildDefaultClassifier() {
		Classifier classifier = new Bagging();
		try {
			classifier
					.setOptions(Utils
							.splitOptions("-P 10 -S 1 -I 10 -W weka.classifiers.trees.J48 -- -U -M 2"));

			decider.train(classifier, dataset);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
