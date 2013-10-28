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
package it.cnr.isti.hpc.dexter.rest.domain;

import it.cnr.isti.hpc.dexter.entity.EntityMatch;
import it.cnr.isti.hpc.dexter.entity.EntityMatchList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an annotated document
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Sep 30, 2013
 */
public class AnnotatedDocument {

	private String text;
	private String annotatedText;
	private List<AnnotatedSpot> spots;

	public AnnotatedDocument(String text) {
		this.text = text;
		spots = new ArrayList<AnnotatedSpot>();
	}

	public void annotate(EntityMatchList eml) {
		annotate(eml, eml.size());
	}

	public void annotate(EntityMatchList eml, int nEntities) {
		eml.sort();
		EntityMatchList emlSub = new EntityMatchList();
		int size = Math.min(nEntities, eml.size());
		spots.clear();
		for (int i = 0; i < size; i++) {
			emlSub.add(eml.get(i));
			EntityMatch em = eml.get(i);
			AnnotatedSpot spot = new AnnotatedSpot(em.getMention(),
					em.getSpotLinkProbability(), em.getStart(), em.getEnd(), em
							.getSpot().getLinkFrequency(), em.getSpot()
							.getFrequency(), em.getId(), em.getFrequency(),
					em.getCommonness(), em.getScore());

			spots.add(spot);
		}
		annotatedText = getAnnotatedText(emlSub);
	}

	private String getAnnotatedText(EntityMatchList eml) {
		Collections.sort(eml, new EntityMatch.SortByPosition());
		StringBuffer sb = new StringBuffer();
		int pos = 0;
		for (EntityMatch em : eml) {
			assert em.getStart() >= 0;
			assert em.getEnd() >= 0;

			sb.append(text.substring(pos, em.getStart()));
			// the spot has been normalized, i want to retrieve the real one
			String realSpot = text.substring(em.getStart(), em.getEnd());
			sb.append(
					"<a href=\"#\" onmouseover='manage(" + em.getId() + ")' >")
					.append(realSpot).append("</a>");
			pos = em.getEnd();
		}
		if (pos < text.length()) {
			sb.append(text.substring(pos));
		}

		return sb.toString();
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getAnnotatedText() {
		return annotatedText;
	}

	public void setAnnotatedText(String annotatedText) {
		this.annotatedText = annotatedText;
	}

	public List<AnnotatedSpot> getSpots() {
		return spots;
	}

	public void setSpots(List<AnnotatedSpot> spots) {
		this.spots = spots;
	}
	

}
