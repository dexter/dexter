package it.cnr.isti.hpc.text;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utils to manage text.
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 02/nov/2011
 */
public class Text {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(Text.class);

	private String text;

	public boolean useStopwords = true;

	// private static Detector languageDetector;

	private static Set<String> stopEnglish = new HashSet<String>(
			Arrays.asList(new String[] { "about", "above", "above", "across",
					"after", "afterwards", "again", "against", "all", "almost",
					"alone", "along", "already", "also", "although", "always",
					"am", "among", "amongst", "amoungst", "amount", "an",
					"and", "another", "any", "anyhow", "anyone", "anything",
					"anyway", "anywhere", "are", "around", "as", "at", "back",
					"be", "became", "because", "become", "becomes", "becoming",
					"been", "before", "beforehand", "behind", "being", "below",
					"beside", "besides", "between", "beyond", "bill", "both",
					"bottom", "but", "by", "call", "can", "cannot", "cant",
					"co", "con", "could", "couldnt", "cry", "de", "describe",
					"detail", "do", "done", "down", "due", "during", "each",
					"eg", "eight", "either", "eleven", "else", "elsewhere",
					"empty", "enough", "etc", "even", "ever", "every",
					"everyone", "everything", "everywhere", "except", "few",
					"fifteen", "fify", "fill", "find", "fire", "first", "five",
					"for", "former", "formerly", "forty", "found", "four",
					"from", "front", "full", "further", "get", "give", "go",
					"had", "has", "hasnt", "have", "he", "hence", "her",
					"here", "hereafter", "hereby", "herein", "hereupon",
					"hers", "herself", "him", "himself", "his", "how",
					"however", "hundred", "ie", "if", "in", "inc", "indeed",
					"interest", "into", "is", "it", "its", "itself", "keep",
					"last", "latter", "latterly", "least", "less", "ltd",
					"made", "many", "may", "me", "meanwhile", "might", "mill",
					"mine", "more", "moreover", "most", "mostly", "move",
					"much", "must", "my", "myself", "name", "namely",
					"neither", "never", "nevertheless", "next", "nine", "no",
					"nobody", "none", "noone", "nor", "not", "nothing", "now",
					"nowhere", "of", "off", "often", "on", "once", "one",
					"only", "onto", "or", "other", "others", "otherwise",
					"our", "ours", "ourselves", "out", "over", "own", "part",
					"per", "perhaps", "please", "put", "rather", "re", "same",
					"see", "seem", "seemed", "seeming", "seems", "serious",
					"several", "she", "should", "show", "side", "since",
					"sincere", "six", "sixty", "so", "some", "somehow",
					"someone", "something", "sometime", "sometimes",
					"somewhere", "still", "such", "system", "take", "ten",
					"than", "that", "the", "their", "them", "themselves",
					"then", "thence", "there", "thereafter", "thereby",
					"therefore", "therein", "thereupon", "these", "they",
					"thickv", "thin", "third", "this", "those", "though",
					"three", "through", "throughout", "thru", "thus", "to",
					"together", "too", "top", "toward", "towards", "twelve",
					"twenty", "two", "un", "under", "until", "up", "upon",
					"us", "very", "via", "was", "we", "well", "were", "what",
					"whatever", "when", "whence", "whenever", "where",
					"whereafter", "whereas", "whereby", "wherein", "whereupon",
					"wherever", "whether", "which", "while", "whither", "who",
					"whoever", "whole", "whom", "whose", "why", "will", "with",
					"within", "without", "would", "yet", "you", "your",
					"yours", "yourself", "yourselves", "the" }));

	private List<String> stopItalian = Arrays.asList(new String[] { "adesso",
			"ai", "al", "alla", "allo", "allora", "altre", "altri", "altro",
			"anche", "ancora", "avere", "aveva", "avevano", "ben", "buono",
			"che", "chi", "cinque", "comprare", "con", "consecutivi",
			"consecutivo", "cosa", "cui", "da", "del", "della", "dello",
			"dentro", "deve", "devo", "di", "doppio", "due", "e", "ecco",
			"fare", "fine", "fino", "fra", "gente", "giu", "ha", "hai",
			"hanno", "ho", "il", "indietro", "invece", "io", "la", "lavoro",
			"le", "lei", "lo", "loro", "lui", "lungo", "ma", "me", "meglio",
			"molta", "molti", "molto", "nei", "nella", "no", "noi", "nome",
			"nostro", "nove", "nuovi", "nuovo", "o", "oltre", "ora", "otto",
			"peggio", "pero", "persone", "piu", "poco", "primo", "promesso",
			"qua", "quarto", "quasi", "quattro", "quello", "questo", "qui",
			"quindi", "quinto", "rispetto", "sara", "secondo", "sei", "sembra",
			"sembrava", "senza", "sette", "sia", "siamo", "siete", "solo",
			"sono", "sopra", "soprattutto", "sotto", "stati", "stato",
			"stesso", "su", "subito", "sul", "sulla", "tanto", "te", "tempo",
			"terzo", "tra", "tre", "triplo", "ultimo", "un", "una", "uno",
			"va", "vai", "voi", "volte", "vostro" });

	private static TokenStream tokenStream = new StandardTokenizer(
			Version.LUCENE_41, new StringReader(""));
	
	private static Analyzer analyzer = new StandardAnalyzer(
			Version.LUCENE_41, CharArraySet.EMPTY_SET);

	public Text(String text) {
		this.text = text;
		
	}

	public Text disableStopwords() {
		useStopwords = false;
		return this;
	}

	// public static String getLanguage(String text) {
	// if (languageDetector == null) {
	// try {
	// String p = Terms.class.getResource("/languages/profiles/")
	// .getPath();
	// logger.info("loading language profiles in {}", p);
	// DetectorFactory.loadProfile(p);
	// } catch (LangDetectException e) {
	// logger.error("detecting the language ({}) ", e.toString());
	// return "";
	// }
	// try {
	// languageDetector = DetectorFactory.create();
	// } catch (LangDetectException e) {
	// logger.error("detecting the language ({}) ", e.toString());
	// return "";
	// }
	// }
	// languageDetector.append(text);
	// try {
	// String language = languageDetector.detect();
	// // clean the language detector (i cannot find a reset() method)
	// languageDetector = DetectorFactory.create();
	// return language;
	// } catch (LangDetectException e) {
	// logger.error("detecting the language ({}) ", e.toString());
	// return "";
	// }
	// }
	//
	// public String getLanguage() {
	// return getLanguage(text);
	// }

	// private int wordMatches(String s, List<String> lang) throws IOException {
	// int i = 0;
	// Text swords = new Text(s);
	// // String[] swords = s.trim().split(" ");
	// for (String word : swords.getTerms()) {
	// if (lang.contains(word)) {
	// i += 1;
	// }
	// }
	// return i;
	// }
	//
	// public static boolean isEnglish(String s) {
	// return getLanguage(s).equals("en");
	// }
	//
	// public static boolean isItalian(String s) {
	// return getLanguage(s).equals("it");
	// }

	public List<String> getTerms() {
		List<String> terms = new ArrayList<String>();
		TokenStream tokenStream = null;
		try {
			tokenStream = analyzer.tokenStream(null, new StringReader(text));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
		
		
		try {
			tokenStream.reset();
			while (tokenStream.incrementToken()) {
			   
			    String term = charTermAttribute.toString();
			    terms.add(term);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return terms;
//		List<String> terms = new ArrayList<String>();
//		tokenStream.reset(new StringReader(text));
//		OffsetAttribute offsetAttribute = tokenStream
//				.addAttribute(OffsetAttribute.class);
//		CharTermAttribute charTermAttribute = tokenStream
//				.addAttribute(CharTermAttribute.class);
//
//		try {
//			while (tokenStream.incrementToken()) {
//
//				String term = charTermAttribute.toString();
//				terms.add(term);
//			}
//		} catch (IOException e) {
//			logger.error("Error tokening the terms in string ({})", text);
//			return Collections.emptyList();
//		}
//		return terms;
		// try {
		// ((StandardTokenizer) tokenStream).reset(new StringReader(text
		// .toLowerCase()));
		// TokenStream ts = (TokenStream) tokenStream;
		//
		// TermAttribute termAttribute = ts.getAttribute(TermAttribute.class);
		// // add a triple for each of the tokens
		//
		// while (tokenStream.incrementToken()) {
		// String term = termAttribute.term();
		// if (useStopwords && stopEnglish.contains(term))
		// continue;
		// terms.add(term);
		//
		// }
		// } catch (IOException e) {
		// logger.error("Error tokening the terms in string ({})", text);
		// return Collections.emptyList();
		// }
		// return terms;
	}

	public static Set<String> getIntersection(String l, String r) {
		Text lt = new Text(l);
		Text rt = new Text(r);
		Set<String> intersection = new HashSet<String>();
		for (String term : lt.getTermsSet()) {
			if (rt.getTermsSet().contains(term))
				intersection.add(term);
		}
		return intersection;
	}

	public static int sharedTerms(String l, String r) {
		return getIntersection(l, r).size();
	}

	public Set<String> getTermsSet() {
		Set<String> termsSet = new HashSet<String>(getTerms());
		return termsSet;
	}

}
