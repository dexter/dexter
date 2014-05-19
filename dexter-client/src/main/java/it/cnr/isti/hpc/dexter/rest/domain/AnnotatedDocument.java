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

import java.util.ArrayList;
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
	private Tagmeta meta;

	public AnnotatedDocument(String text) {
		this.text = text;
		spots = new ArrayList<AnnotatedSpot>();

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

	@Override
	public String toString() {
		return "AnnotatedDocument [text=" + text + ", annotatedText="
				+ annotatedText + ", spots=" + spots + "]";
	}

	public Tagmeta getMeta() {
		return meta;
	}

	public void setMeta(Tagmeta meta) {
		this.meta = meta;
	}

}
