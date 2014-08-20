package it.cnr.isti.hpc.text;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class SentenceSegmenterTest {

	@Test
	public void testSplitPos() throws Exception {
		SentenceSegmenter segmenter = new SentenceSegmenter();
		String text = "Diego Armando Maradona Franco is a former Argentine footballer. He has served as a manager and coach at other clubs as well as for the national team of Argentina. ";
		List<Sentence> sentences = segmenter.splitPos(text);
		System.out.println("text: ");
		System.out.println(text);
		System.out.println("sentences: ");
		int i = 0;
		for (Sentence s : sentences) {
			System.out.println(i + " )  " + s);
			i++;
		}
		int posSecondSentence = text.indexOf("He has");
		System.out.println("pos: " + posSecondSentence);
		assertEquals(posSecondSentence, sentences.get(1).getStart());
	}
}
