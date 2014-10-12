/**
 *  Copyright 2014 Diego Ceccarelli
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
package it.cnr.isti.hpc.dexter.analysis;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.charfilter.HTMLStripCharFilter;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.pattern.PatternReplaceCharFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Aug 18, 2014
 */
public class SpotAnalyzer extends Analyzer {

	boolean lowercase = true;

	public SpotAnalyzer lowercase(boolean lowercase) {
		this.lowercase = lowercase;
		return this;
	}

	@Override
	protected Reader initReader(String fieldName, Reader reader) {
		CharFilter cf = new PatternReplaceCharFilter(
				Pattern.compile("^[ ]*the +(.*)"), "$1", reader);

		cf = new PatternReplaceCharFilter(
				Pattern.compile("[*!`{}~='<>:/%|=+_]"), " ", cf);

		cf = new PatternReplaceCharFilter(Pattern.compile("^[ ]*a +(.*)"),
				"$1", cf);
		cf = new PatternReplaceCharFilter(Pattern.compile("^(.*) \\(.*\\)$"),
				"$1", cf);

		cf = new PatternReplaceCharFilter(Pattern.compile("^(.*)#.*$"), "$1",
				cf);

		cf = new PatternReplaceCharFilter(
				Pattern.compile("[, ]*[sjSJ][rR][.]?"), "", cf);

		cf = new PatternReplaceCharFilter(Pattern.compile(" ([A-Z][.] ?)+"),
				" ", cf);
		cf = new PatternReplaceCharFilter(Pattern.compile("^([A-Z][.] ?)+ "),
				" ", cf);
		cf = new PatternReplaceCharFilter(Pattern.compile(" [A-Z][.]$"), " ",
				cf);

		cf = new HTMLStripCharFilter(reader);
		return cf;

	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName,
			Reader reader) {

		final StandardTokenizer analyzer = new StandardTokenizer(
				Version.LUCENE_41, reader);
		TokenStream tok = new StandardFilter(Version.LUCENE_41, analyzer);
		if (lowercase)
			tok = new LowerCaseFilter(Version.LUCENE_41, tok);
		tok = new ASCIIFoldingFilter(tok);

		return new TokenStreamComponents(analyzer, tok) {
			@Override
			protected void setReader(final Reader reader) throws IOException {
				super.setReader(reader);
			}
		};
	}
}
