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

import it.cnr.isti.hpc.wikipedia.article.Article;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.charfilter.HTMLStripCharFilter;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.pattern.PatternReplaceCharFilter;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.util.Version;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Aug 18, 2014
 */
public class DexterAnalyzer extends Analyzer {

	private boolean shingles = true;

	@Override
	protected TokenStreamComponents createComponents(String fieldName,
			Reader reader) {

		CharFilter cf = new PatternReplaceCharFilter(
				Pattern.compile("[*-!`{}~[]='<>:/;.&%|=+_]"), "", reader);

		cf = new HTMLStripCharFilter(cf);

		final StandardTokenizer analyzer = new StandardTokenizer(
				Version.LUCENE_41, cf);
		TokenStream tok = new StandardFilter(Version.LUCENE_41, analyzer);
		tok = new LowerCaseFilter(Version.LUCENE_41, tok);
		tok = new ASCIIFoldingFilter(tok);
		if (shingles) {
			tok = new ShingleFilter(tok, 5);
		}
		return new TokenStreamComponents(analyzer, tok) {
			@Override
			protected void setReader(final Reader reader) throws IOException {
				super.setReader(reader);
			}
		};
	}

	public void setShingles(boolean shingles) {
		this.shingles = shingles;
	}

	public static class ArticleIterator implements Iterator<String> {
		private boolean hasNext = true;
		CharTermAttribute termAtt = null;
		TokenStream ts = null;
		Analyzer analyzer = new DexterAnalyzer();

		public ArticleIterator() {

		}

		public ArticleIterator(Article a) throws IOException {

			setArticle(a);
		}

		public void setArticle(Article a) throws IOException {
			StringBuilder sb = new StringBuilder();
			sb.append(a.getTitle());
			for (String par : a.getCleanParagraphs()) {
				sb.append(' ');
				sb.append(par);
			}
			ts = analyzer.tokenStream("content",
					new StringReader(sb.toString()));

			termAtt = ts.addAttribute(CharTermAttribute.class);
			ts.reset();
			hasNext = ts.incrementToken();
		}

		@Override
		public boolean hasNext() {
			return hasNext;
		}

		@Override
		public String next() {
			String spot = termAtt.toString();
			try {
				hasNext = ts.incrementToken();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return spot;

		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub

		}

	}

	public static void main(String[] args) throws IOException {
		String str = "<body>perchééééééééé";
		Analyzer anal = new DexterAnalyzer();
		TokenStream ts = anal.tokenStream("content", new StringReader(str));

		OffsetAttribute offsetAtt = ts.addAttribute(OffsetAttribute.class);
		CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);
		ts.reset();
		while (ts.incrementToken()) {
			System.out.println(termAtt.toString()
					.substring(0, termAtt.length()));
			System.out
					.println("token start offset: " + offsetAtt.startOffset());
			System.out.println("  token end offset: " + offsetAtt.endOffset());
		}
	}
}
