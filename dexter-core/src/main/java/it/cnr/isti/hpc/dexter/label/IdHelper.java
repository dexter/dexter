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
package it.cnr.isti.hpc.dexter.label;

import it.cnr.isti.hpc.wikipedia.article.Article;
import it.cnr.isti.hpc.wikipedia.article.Link;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An IdHelper provides the conversion between an entity label (e.g.,
 * Pablo_Picasso) and an integer representing the entity (e.g., 24176).
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 05/lug/2012
 */
public class IdHelper implements IdToLabel, LabelToId {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(IdHelper.class);

	private IdToLabel idToLabel;
	private LabelToId labelToId;

	public static final int NOID = 0;

	public IdHelper(IdToLabel idToLabel, LabelToId labelToHash) {
		this.idToLabel = idToLabel;
		this.labelToId = labelToHash;
	}

	public Integer getId(String label) {
		return labelToId.getId(label);
	}

	public String getLabel(Integer key) {
		return idToLabel.getLabel(key);
	}

	public List<String> getLabels(List<Integer> keys) {
		List<String> s = new ArrayList<String>(keys.size());
		for (Integer key : keys) {
			s.add(getLabel(key));
		}
		return s;
	}

	public List<Integer> getOutcomingIds(Article a) {
		List<Integer> ids = new ArrayList<Integer>();
		for (Link l : a.getLinks()) {
			Integer i = getId(l.getCleanId());
			if (i == 0) {
				logger.error("no id for label {}", l.getId());
				continue;
			}
			if (ids.contains(i))
				continue;
			ids.add(i);
		}
		return ids;
	}

	public boolean isDisambiguation(Integer id) {
		return id < 0;
	}

	public boolean hasLabel(String label) {
		return labelToId.getId(label) != NOID;
	}
}