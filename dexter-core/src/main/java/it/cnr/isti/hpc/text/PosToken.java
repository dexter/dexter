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
package it.cnr.isti.hpc.text;

import simplenlg.framework.NLGFactory;
import simplenlg.framework.WordElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.SPhraseSpec;

/**
 * A pos token
 * 
 * 
 * @see http://bulba.sdsu.edu/jeanette/thesis/PennTags.html
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 08/giu/2012
 */
public class PosToken implements Comparable<PosToken> {
	String token;
	String pos;
	static NLGFactory nlgFactory;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
		result = prime * result + ((token == null) ? 0 : token.hashCode());
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
		PosToken other = (PosToken) obj;
		if (pos == null) {
			if (other.pos != null)
				return false;
		} else if (!pos.equals(other.pos))
			return false;
		if (token == null) {
			if (other.token != null)
				return false;
		} else if (!token.equals(other.token))
			return false;
		return true;
	}

	static {
		Lexicon lexicon = Lexicon.getDefaultLexicon();
		nlgFactory = new NLGFactory(lexicon);
	}

	public PosToken(String token, String pos) {
		super();
		this.token = token;
		this.pos = pos;
	}

	public String getToken() {
		return token;
	}

	public String getPos() {
		return pos;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public boolean isVerb() {
		return pos.startsWith("VB");
	}
	
	public boolean isAdverb() {
		return pos.startsWith("RB");
	}
	
	public boolean isConjunction() {
		return pos.startsWith("CC");
	}
	
	public boolean isAbjective() {
		return pos.startsWith("JJ");
	}
	
	

	/**
	 * if the token is a verb, return a normalized form, otherwise an empty
	 * string
	 * 
	 * @return the normalized form of a verb, otherwise empty string
	 */
	public String normalizedVerb() {
		if (! isVerb()) return "";
		SPhraseSpec p = nlgFactory.createClause();
		p.setVerb(token);
		return ((WordElement) p.getVerb()).getBaseForm();
	}

	@Override
	public String toString() {
		return "PosToken [token=" + token + ", pos=" + pos + "]";
	}

	@Override
	public int compareTo(PosToken arg0) {
		return token.toLowerCase().compareToIgnoreCase(arg0.getToken());
	}

}
