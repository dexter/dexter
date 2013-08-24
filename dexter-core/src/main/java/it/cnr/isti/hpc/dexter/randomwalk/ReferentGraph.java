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
package it.cnr.isti.hpc.dexter.randomwalk;

import it.cnr.isti.hpc.dexter.entity.Entity;
import it.cnr.isti.hpc.dexter.entity.EntityMatch;
import it.cnr.isti.hpc.dexter.entity.EntityMatchList;
import it.cnr.isti.hpc.dexter.relatedness.RelatednessFactory;
import it.cnr.isti.hpc.dexter.spot.Spot;
import it.cnr.isti.hpc.dexter.spot.SpotMatch;
import it.cnr.isti.hpc.dexter.spot.SpotMatchList;
import it.cnr.isti.hpc.io.IOUtils;
import it.cnr.isti.hpc.property.ProjectProperties;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Jama.Matrix;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Referent Graph models the matrix to perform the random walk how described in
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 07/ago/2012
 */
public class ReferentGraph {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(ReferentGraph.class);

	SpotMatchList sml;
	EntityMatchList eml;
	Set<EntityMatch> em;
	int size;
	int nSpot;
	int nEntities;
	double[][] matrix;
	double[][] evidenceVector;
	double lambda = 0.1;

	double[][] result;

	// for each entity position, contain the positions of the other entities
	// that co-occur in the same spot;
	Map<Integer, Set<Integer>> entityToSameSpotEntities = new HashMap<Integer, Set<Integer>>();

	static ProjectProperties properties = new ProjectProperties(
			ReferentGraph.class);
	String relatenessType = properties.get("relatedness");
	boolean debugMatrix = false;

	RelatednessFactory relatedness;
	// static {
	// RelatednessFactory.register(new MWRelatedness());
	// }

	BiMap<SpotMatch, Integer> spot2pos = HashBiMap.create();
	BiMap<EntityMatch, Integer> entity2pos = HashBiMap.create();

	private void addCooccorrence(int ePos, int cPos) {
		if (!entityToSameSpotEntities.containsKey(ePos)) {
			entityToSameSpotEntities.put(ePos, new HashSet<Integer>());
		}
		entityToSameSpotEntities.get(ePos).add(cPos);
	}

	public ReferentGraph(SpotMatchList sml, EntityMatchList eml) {
		this.sml = sml;
		this.eml = eml;
		em = new HashSet<EntityMatch>(eml);
		nSpot = sml.size();
		nEntities = em.size();
		size = nSpot + nEntities;

		matrix = new double[size][size];
		evidenceVector = new double[size][1];
		logger.info("matrix size {}", size);

		logger.info("loading <{}> relatedness", relatenessType);
		relatedness = new RelatednessFactory(relatenessType);
		String debug = properties.get("debug.matrix");
		debugMatrix = (debug != null) && (debug.equals("true"));

		logger.info("generate ids");
		generateIds();
		logger.info("generate matrix");
		generateMatrix();
		logger.info("generate vector");
		generateVector();

		if (debugMatrix) {
			if (!new File("/tmp/mat").exists()) {
				new File("/tmp/mat").mkdir();
			}
			Matrix original = new Matrix(matrix);
			dumpMatrix("/tmp/mat/original", original);
		}

		logger.info("normalize");
		normalizeMatrix();
		normalizeVector();
	}

	private void dumpMatrix(String path, Matrix m) {
		try {
			PrintWriter writer = new PrintWriter(new File(path));
			m.print(writer, 5, 5);
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void generateIds() {
		int pos = 0;
		for (SpotMatch m : sml) {
			spot2pos.put(m, pos++);
		}
		for (EntityMatch e : em) {
			entity2pos.put(e, pos++);
		}
	}

	private void generateMatrix() {
		logger.debug("generate matrix");

		for (SpotMatch match : sml) {
			// set values from spot to related entities
			for (EntityMatch entity : match.getEntities()) {

				setValueInMatrix(match, entity, entity.getScore());
			}
		}
		// can be improved

		long startMatch = System.currentTimeMillis();
		long endMatch = System.currentTimeMillis();
		long entityTime = 0;
		// for each spot
		for (int i = 0; i < sml.size(); i++) {
			endMatch = System.currentTimeMillis();
			if (i > 0) {
				logger.info("spot {} managed in {} millis", i, endMatch
						- startMatch);
				logger.debug("relatedness time: {}", entityTime);
				entityTime = 0;
			}
			// get the current spot;
			SpotMatch current = sml.get(i);

			for (EntityMatch entity : current.getEntities()) {
				// don't want to accumulate score for hubs
				for (EntityMatch e : current.getEntities()) {
					addCooccorrence(getId(entity), getId(e));
				}

				setValueInMatrix(entity, 0);
				for (int j = i + 1; j < sml.size(); j++) {
					SpotMatch candidateSpot = sml.get(j);
					addEntityRelatedness(entity, current, candidateSpot);

				}
			}
		}
	}

	private void addEntityRelatedness(EntityMatch currentEntity,
			SpotMatch currentSpot, SpotMatch candidateSpot) {
		// don't want to add relatedness if the two spots overlaps
		if (candidateSpot.getSpot().overlaps(currentSpot.getSpot())) {
			return;
		}

		Entity je = currentEntity.getEntity();
		int jeID = je.id();
		for (EntityMatch candidateEntity : candidateSpot.getEntities()) {
			int oeID = candidateEntity.getId();
			if (jeID == oeID) {
				continue;
			} else {
				double rel = relatedness.getScore(jeID, oeID);
				double invRel = relatedness.getScore(oeID, jeID);
				setValueInMatrix(currentEntity, candidateEntity, rel, invRel);
			}

			// logger.info("set value in {} millis  ",end-start);
		}

	}

	/**
	 * 
	 */
	private void generateVector() {
		for (SpotMatch m : sml) {
			evidenceVector[getId(m)][0] = m.getSpot().getIdf();
		}
	}

	private EntityMatch getEntity(Integer id) {
		return entity2pos.inverse().get(id);
	}

	private Integer getId(Object el) {
		if (el instanceof SpotMatch) {
			return spot2pos.get(el);
		}
		if (el instanceof EntityMatch) {
			return entity2pos.get(el);
		}
		throw new UnsupportedOperationException();
	}

	private SpotMatch getSpot(Integer id) {
		return spot2pos.inverse().get(id);
	}

	/**
	 * normalizes the negative values and returns the maximum value
	 */
	private void normalize() {
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		int smlSize = sml.size();
		// List<Double> column = new ArrayList<Double>(size);
		// Map<Integer,Double> medians = new HashMap<Integer,Double>(size);

		for (int i = smlSize; i < size; i++) {
			// column.clear();
			for (int j = smlSize; j < size; j++) {
				if (matrix[j][i] == 0)
					continue;
				min = Math.min(min, matrix[j][i]);
				max = Math.max(max, matrix[j][i]);
				// column.add(matrix[j][i]);
			}
			// Collections.sort(column);
			// medians.put(i, column.get((column.size()/2)));
		}
		double delta = 0 - min;

		max = max + delta;

		for (int i = smlSize; i < size; i++) {
			// System.out.println("normilizing column " + i);
			for (int j = smlSize; j < size; j++) {

				double before = matrix[j][i];
				// FIXME relatedness is always != from 0, if it is 0 it is 0
				if (j == i || before == 0) {
					matrix[j][i] = 0;
				} else {

					matrix[j][i] = (matrix[j][i] + delta) / max;

				}
				// System.out.println("normalizing : " + before + "->"
				// + matrix[j][i]);
			}

		}

		return;
	}

	/**
	 * Normalize by the columns
	 */
	private void normalizeMatrix() {
		if (relatedness.hasNegativeScores()) {
			normalize();
		}

		for (int i = 0; i < size; i++) {
			double sum = 0;
			for (int j = 0; j < size; j++) {
				sum += matrix[j][i];
			}
			assert sum >= 0;
			// if (sum == 0 ){
			// continue;
			// }
			if (sum == 0) {
				if (i >= nSpot) {
					// column is == 0
					// normalizing the column as discussed with claudio
					normalizeColumn(i);
					continue;
				} else {
					logger.warn("Spot column == 0");
					for (int j = 0; j < nSpot; j++) {
						matrix[j][i] = (double) 1 / (double) nSpot;
					}
				}
				continue;
			}
			// Normalizing
			for (int j = 0; j < size; j++) {
				matrix[j][i] = matrix[j][i] / sum;
			}
		}
	}

	private void normalizeColumn(int i) {
		Set<Integer> entitiesToIgnore = entityToSameSpotEntities.get(i);
		int entriesToFill = nEntities - entitiesToIgnore.size();
		if (entriesToFill == 0) {
			// FIXME BIOPARK
			logger.warn("entriesToFill = 0");
			return;
		}
		for (int j = nSpot; j < size; j++) {
			if (!entitiesToIgnore.contains(j)) {
				matrix[j][i] = (double) 1 / (double) entriesToFill;
			}
		}
	}

	/**
	 * 
	 */
	private void normalizeVector() {
		double sum = 0;
		for (int i = 0; i < size; i++) {
			sum += evidenceVector[i][0];
		}
		for (int i = 0; i < size; i++) {
			evidenceVector[i][0] = evidenceVector[i][0] / sum;
		}
	}

	public void performRandomwalk() {
		logger.info("performing random walk");
		double c = (1 - lambda);
		Matrix T = new Matrix(matrix);
		if (debugMatrix) {

			if (!new File("/tmp/mat").exists()) {
				new File("/tmp/mat").mkdir();
			}
			dumpMatrix("/tmp/mat/T", T);
		}
		Matrix s = new Matrix(evidenceVector);
		Matrix I = Matrix.identity(size, size);
		Matrix cT = T.times(c);
		logger.info("computing the inverse matrix");
		Matrix inv = I.minus(cT).inverse();
		logger.info("done");

		logger.debug("lambda = {} ", lambda);

		Matrix invLambda = inv.times(lambda);
		Matrix r = invLambda.times(s);

		result = r.getArray();
		if (debugMatrix) {

			dumpMatrix("/tmp/mat/invLambda", invLambda);
			dumpMatrix("/tmp/mat/inv", inv);
			dumpMatrix("/tmp/mat/r", r);

			dumpMatrix("/tmp/mat/cT", cT);
			dumpMatrix("/tmp/mat/s", s);
			dumpLineMeanings();
		}

	}

	private void dumpLineMeanings() {
		Writer w = IOUtils.getPlainOrCompressedUTF8Writer("/tmp/mat/labels");
		try {
			int pos = 0;
			for (int i = 0; i < sml.size(); i++) {
				w.write("SPOT ");
				w.write(getSpot(pos).getSpot().getText()); // GhGh!
				pos++;
				w.write("\n");
			}
			for (int i = 0; i < em.size(); i++) {
				w.write("ENTITY ");
				// w.write(getEntity(pos).getEntity().getName());
				pos++;
				w.write("\n");
			}
			w.close();
		} catch (IOException e) {
			logger.error("dumping the meanings of the lines {}", e.toString());
		}

	}

	public EntityMatchList rankEntities() {
		EntityMatchList results = new EntityMatchList();
		for (Map.Entry<EntityMatch, Integer> e : entity2pos.entrySet()) {
			Spot spot = e.getKey().getSpot();
			Integer id = e.getKey().getId();
			logger.debug("spot   = {}", spot);
			logger.debug("entity = {}", e.getKey());
			logger.debug("id-vect= {}", e.getValue());
			int arrayId = e.getValue();
			double evidence = result[arrayId][0];
			if (Double.isNaN(evidence)) {

				logger.error("evidence is NaN ");
				evidence = 0;
			}
			if (spot == null)
				continue;
			double importance = spot.getIdf() / sml.getTotalIdf();
			if (Double.isNaN(importance)) {
				logger.error("importance is NaN ");
				importance = 0;
			}
			double score = importance * evidence;

			logger.debug("entity     {} \t spot {}", id, spot.getText());
			logger.debug("importance {} \t evidence {} ", importance, evidence);

			EntityMatch match = new EntityMatch(id, score, spot);
			results.add(match);
		}
		results.sort();
		return results;
	}

	private void setValueInMatrix(EntityMatch e, EntityMatch c, double rel,
			double invRel) {
		int epos = getId(e);
		int cpos = getId(c);
		logger.debug("ENTITY {} -> ENTITY {}", epos, cpos);
		matrix[epos][cpos] += invRel;
		matrix[cpos][epos] += rel;
	}

	private void setValueInMatrix(EntityMatch e1, float value) {
		int e1pos = getId(e1);
		matrix[e1pos][e1pos] = value;
	}

	private void setValueInMatrix(SpotMatch spot, EntityMatch entity,
			double value) {
		int spotId = getId(spot);
		int entityId = getId(entity);
		logger.debug("SPOT   {} -> ENTITY {}", spotId, entityId);
		// matrix[getId(spot)][getId(entity)] = value;
		matrix[entityId][spotId] = value;
	}

}
