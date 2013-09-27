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
package it.cnr.isti.hpc.dexter.spot;

import it.cnr.isti.hpc.dexter.entity.Entity;
import it.cnr.isti.hpc.io.reader.RecordParser;
import it.cnr.isti.hpc.text.IntArrayString;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A spot consists of:
 * <ul>
 * <li>the mention (i.e. the common anchor used to link the entities)</li>
 * <li>the list of entities related to the current mention in the knowledge base
 * </li>
 * <li>the frequency of the mention: how many times the mention occurs in the
 * collection (as link or simple text)</li>
 * <li>the link frequency of the mention: how many times the mention occurs in
 * the collection as a link to an entity</li>
 * <li>the idf score of the mentions</li>
 * </ul>
 * 
 * The spot is not document related, but collection related.
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it
 */
public class Spot implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(Spot.class);
	private static IntArrayString ias = new IntArrayString(false);
	private static StringBuilder sb = new StringBuilder();

	// the text of the mention
	protected String mention;
	// how many times the mention occurs in the collection
	// (as anchor/spot or simple text). note that freq >= link
	protected int freq;
	// how many times the mention occurs in the collection
	// as a link to an entity
	protected int link;
	// the list of entities with the current label
	protected List<Entity> entities;

	// probability to be a link (i.e., link/freq)
	protected double linkProbability;
	// spot inverse document frequency in the collection
	// (i.e. freq / collectionSize)
	protected double idf;

	// the number of entities in the collection
	protected static int collectionSize;

	/**
	 * Builds a spot from a textual mention
	 * 
	 * @param spot
	 *            The string representing the textual mention
	 */
	public Spot(String mention) {
		this.mention = mention;
		this.entities = Collections.emptyList();
	}

	/**
	 * Creates the spot, and associates the entities that can be referred by the
	 * mention, how many times the mention occurs as an anchor-text in Wikipedia
	 * and how many articles that contain the mention as anchor or simple text
	 * (i.e. document frequency of the spot).
	 * 
	 * @param spot
	 *            The string representing the spot;
	 * @param entities
	 *            The list of entities that can be refered by the spot;
	 * @param link
	 *            How many times the spot occurs in wikipedia as anchor text;
	 * @param freq
	 *            How many articles that contain the text mention as anchor or
	 *            simple text (i.e. document frequency of the spot).
	 */
	public Spot(String spot, List<Entity> entities, int link, int freq) {
		this.mention = spot;
		this.freq = freq;
		this.link = link;
		this.entities = entities;

		updateLinkProbability();
		updateIdf();
		logger.debug("spot [{}] IDF = {}", spot, idf);
	}

	/**
	 * Returns the document frequency of this spot, i.e., how many wikipedia
	 * articles contain the spot as simple text or anchor text.
	 * 
	 * @return The document frequency of this spot;
	 */
	public int getFrequency() {
		return freq;
	}

	/**
	 * Set How many times the mention occurs in the collection. It will
	 * consequently update the IDF value.
	 * 
	 * @param freq
	 *            How many times the mention occurs in the collection
	 */
	public void setFrequency(int freq) {
		this.freq = freq;
		updateIdf();
		updateLinkProbability();
	}

	/**
	 * Returns the inverse document frequency of this mention;
	 * 
	 * @return The inverse document frequency of this mention;
	 */
	public double getIdf() {
		return idf;
	}

	/**
	 * Set the inverse document frequency of the mention in the collection.
	 * 
	 * @param idf
	 *            The inverse document frequency to set for this mention
	 */
	public void setIdf(double idf) {
		this.idf = idf;
	}

	/**
	 * Returns the text of this mention;
	 * 
	 * @return the text of the mention;
	 */
	public String getMention() {
		return mention;
	}

	/**
	 * Return how many times the mention occurs in the collection as a link to
	 * an entity
	 * 
	 * @return link Times the mention occurs in the collection as a link to an
	 *         entity
	 */
	public int getLink() {
		return link;
	}

	/**
	 * Set how many times the mention occurs in the collection as a link to an
	 * entity
	 * 
	 * @param link
	 *            Times the mention occurs in the collection as a link to an
	 *            entity
	 */
	public void setLink(int link) {
		this.link = link;
		updateLinkProbability();
	}

	/**
	 * Return the list of entities with the current mention
	 * 
	 * @return the list of entities with the current mention
	 */
	public List<Entity> getEntities() {
		if (entities == null)
			return Collections.emptyList();
		return entities;
	}

	/**
	 * Set the list of entities with the current mention
	 * 
	 * @param entities
	 *            The list of entities with the current mention
	 */
	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}

	/**
	 * Return the probability for the current mention to be a link
	 * 
	 * @return the probability for the current mention to be a link
	 */
	public double getLinkProbability() {
		return Math.min((double) link / (double) freq, 1);
	}

	/**
	 * Set the probability for this mention to be a link;
	 * 
	 * @param probability
	 *            The probability of this mention to be a link
	 */
	public void setLinkProbability(double probability) {
		this.linkProbability = probability;
	}

	/**
	 * Set the text of the mention
	 * 
	 * @param text
	 *            The text of the mention;
	 */
	public void setMention(String mention) {
		this.mention = mention;
	}

	/**
	 * Update the link probability using the link and freq fields
	 */
	protected void updateLinkProbability() {
		linkProbability = Math.min((double) link / (double) freq, 1);
	}

	/**
	 * Update the IDF using the freq and collectionSize fields
	 */
	protected void updateIdf() {
		idf = Math.log(collectionSize / (freq + 0.5));
	}

	/**
	 * Get the number of entities in the collection
	 * 
	 * @return
	 */
	public static int getCollectionSize() {
		return collectionSize;
	}

	/**
	 * Set the number of entities in the collection
	 * 
	 * @param collectionSize
	 *            The number of entities of the collection
	 */
	public static void setCollectionSize(int collectionSize) {
		Spot.collectionSize = collectionSize;
	}

	/**
	 * Computes the entity commonness for a given entity and this spot, i.e.,
	 * <code>P(e|s)</code>, the probability for an entity to be associated with
	 * this spot. It is computed dividing the number of times that the spot
	 * links to the entity by the frequency of spot as anchor.
	 * 
	 * @param e
	 *            The entity for which to compare the commmonness
	 * @return The
	 * 
	 */
	public double getEntityCommonness(Entity e) {
		return (double) e.getFrequency() / (double) link;

	}

	// /**
	// * Returns true if this spot and the given spots overlaps in the annotated
	// * text, e.g.,
	// * <code> "neruda pablo picasso" -> 'neruda pablo' 'pablo picasso'
	// </code>.
	// *
	// * @param s
	// * - The spot to check
	// * @return
	// */
	// public boolean overlaps(Spot s) {
	// boolean startOverlap = ((s.getStart() >= this.getStart()) && (s
	// .getStart() <= this.getEnd()));
	// if (startOverlap)
	// return true;
	// boolean endOverlap = ((s.getEnd() >= this.getStart()) && (s.getEnd() <=
	// this
	// .getEnd()));
	// return endOverlap;
	// }

	/**
	 * Encodes this spot in a array of byte, the encode consists in:
	 * <ul>
	 * <li>1 byte, containing the length of the mention (it is assumed that the
	 * length <code>n</code> of the mention is less than 256)</li>
	 * <li> <code>n</code> bytes, containing the mention encoded in ascii </code>
	 * </li>
	 * <li>4 bytes, containing the frequency of the mention as link</li>
	 * <li>4 bytes, containing the document frequency of the mention</li>
	 * <li> <code>2 x 4 x m </code> bytes, where <code> m </code> is the number
	 * of entities associated with the mention, containing for each entity its
	 * unique id and its frequency (number of anchors with this text that link
	 * to the entity);
	 * 
	 * @return the encoded byte array
	 */
	public byte[] toByteArray() {
		int size = entities.size();
		int len = mention.length();

		if (len > 255)
			len = 255;
		byte[] s = null;
		try {
			s = mention.getBytes("US-ASCII");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (s.length > len) {
			// it should never happen but, just in case..
			len = 1;
			s = new byte[] { '0' };
		}

		// length(spot string) + spot + link (int) + freq (int) + entities (id +
		// freq)
		ByteBuffer buffer = ByteBuffer.allocate(1 + (len) + (size * 2 + 2) * 4);
		buffer.put((byte) len);
		buffer.put(s);

		buffer.putInt(link);
		buffer.putInt(freq);
		for (Entity e : entities) {
			buffer.putInt(e.getId());
			buffer.putInt(e.getFrequency());
		}
		return buffer.array();
	}

	/**
	 * Decodes a Spot from a byte representation, if the given text match the
	 * spot text encoded in the byte array.
	 * 
	 * @see toByteArray
	 * 
	 * @param text
	 *            - the spot text to decode
	 * @param data
	 *            - the binary rep for the spot
	 * @return A spot if the given string <code> text </code> matches the text
	 *         of the spot encoded in data, otherwise null
	 */
	public static Spot fromByteArray(String text, byte[] data) {
		int len = (int) data[0];
		if (text.length() != len)
			return null;

		String s = null;
		try {
			s = new String(Arrays.copyOfRange(data, 1, 1 + len), "US-ASCII");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!s.equals(text))
			return null;

		IntBuffer intBuf = ByteBuffer.wrap(
				Arrays.copyOfRange(data, 1 + len, data.length)).asIntBuffer();
		int[] array = new int[intBuf.remaining()];
		intBuf.get(array);
		// List<Entity> entities = null;
		List<Entity> entities = new ArrayList<Entity>(array.length - 2);

		for (int i = 2; i < array.length; i += 2) {
			entities.add(new Entity(array[i], array[i + 1]));
		}
		Spot spot = new Spot(text, entities, array[0], array[1]);
		return spot;
	}

	/**
	 * Returns a tab separated version of the spot in a string. The string
	 * contains:
	 * <ul>
	 * <li>the entity ids of the possible entities for this spot, encoded in
	 * esadecimal</li>
	 * <li>the frequencies of the each entity for this spot, encoded in
	 * esadecimal</li>
	 * <li>the frequency of the spot as anchor text</li>
	 * <li>the document frequency of the spot in the collection</li>
	 * </ul>
	 * 
	 * @return the tab separated representation of this spot
	 */
	public String toTsv() {
		sb.setLength(0);
		List<Integer> entityId = new ArrayList<Integer>();
		List<Integer> freqs = new ArrayList<Integer>();
		for (Entity e : entities) {
			entityId.add(e.getId());
			freqs.add(e.getFrequency());
		}
		sb.append(ias.toString(entityId));
		sb.append("\t");
		sb.append(ias.toString(freqs));

		sb.append("\t");
		sb.append(link);
		sb.append("\t");
		sb.append(freq);
		return sb.toString();

	}

	/**
	 * Decodes a tab separated representation of a spot.
	 * 
	 * @see toTsv
	 * @param text
	 * @return the spot encoded in text.
	 */
	public static Spot fromTsvLine(String text) {
		Scanner scanner = new Scanner(text).useDelimiter("\t");
		String spot = scanner.next();
		List<Integer> entities = new IntArrayString(false).toArray(scanner
				.next());
		List<Integer> freqs = new IntArrayString(false).toArray(scanner.next());
		List<Entity> ent = new ArrayList<Entity>();
		for (int i = 0; i < entities.size(); i++) {
			Entity e = new Entity(entities.get(i), freqs.get(i));
			ent.add(e);
		}
		int link = scanner.nextInt();
		int freq = scanner.nextInt();
		Spot s = new Spot(spot, ent, link, freq);
		return s;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[" + mention + "] idf:"
				+ getIdf() + "\t probability:" + linkProbability + "\t link "
				+ link);
		sb.append("\n");
		int i = 1;

		for (Entity e : getEntities()) {
			System.out.println("e " + e.getId() + " freq: " + e.getFrequency());
			sb.append("\t entity").append(i++).append("\t").append(e.getId())
					.append("\t p(e|spot)=")
					.append(e.getFrequency() / (double) getLink()).append("\n");

		}
		return sb.toString();
	}

	/**
	 * Returns a copy of this object
	 */
	public Spot clone() {
		Spot copy = new Spot(mention);
		copy.setLinkProbability(linkProbability);
		copy.setEntities(entities);
		copy.setIdf(idf);
		copy.setLink(link);
		copy.setFrequency(freq);
		return copy;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Spot other = (Spot) obj;
		if (mention == null) {
			if (other.mention != null)
				return false;
		} else if (!mention.equals(other.mention))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mention == null) ? 0 : mention.hashCode());
		return result;
	}

	/**
	 * A record parser for tsv encoded spots.
	 * 
	 * @see RecordParser
	 * 
	 */
	public static class Parser implements RecordParser<Spot> {

		@Override
		public Spot decode(String record) {
			return fromTsvLine(record);
		}

		@Override
		public String encode(Spot spot) {
			return spot.toTsv();
		}
	}
}