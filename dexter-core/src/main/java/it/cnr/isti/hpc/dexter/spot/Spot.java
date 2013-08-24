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
 * a Spot consists of
 * <ul>
 * <li>the spot text</li>
 * <li>a list of entities that have that label</li>
 * <li>frequency of the spot (how many times the spot text occurs in the collection (as anchor/spot or simple text) </li>
 * <li>link frequency of the spot how many times the spot text occurs in the collection as a link to an entity </li>
 * <li>an idf score</li>
 * </ul>
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 01/ago/2012
 */
public class Spot implements Serializable {
	

	
	private static final long serialVersionUID = 1L;
	/**
	 * Logger for this class
	 */
	static final Logger logger = LoggerFactory.getLogger(Spot.class);
	// text of the spot
	String spot;
	// how many times the spot text occurs in the collection
	// (as anchor/spot or simple text). note that freq >= link
	int freq;
	// how many times the spot text occurs in the collection
	// as a link to an entity
	int link;
	// the list of entities
	List<Entity> entities = Collections.emptyList();
	
	
	// probability to be a link (i.e., link/freq)
	double linkProbability;
	// spot inverse document frequency in the collection (i.e., freq /
	// collectionSize)
	double idf;
	
	// where the spot begins / ends in the original text
	int start,end;

	// the number of entities in the collection
	static int collectionSize = 4000000;

//	static {
//		ProjectProperties properties = new ProjectProperties(
//				IndexWikipediaOnLuceneCLI.class);
//		
//		collectionSize = properties.getInt("w");
//		
//	}

	/**
	 * Builds a spot from a string
	 * @param spot - the string representing the spot
	 */
	public Spot(String spot) {
		this.spot = spot;
	}

	/**
	 * Creates the spot, and associates the entities that can be refered by the spot,
	 * how many times the spot occurs as a anchor-text in Wikipedia and how many articles
	 * that contain the spot as anchor or simple text (i.e. document frequency 
	 * of the spot).
	 * 
	 * @param spot - the string representing the spot;
	 * @param entities - the list of entities that can be refered by the spot;
	 * @param link - how many times the spot occurs in wikipedia as anchor text;
	 * @param freq - how many articles that contain the spot as anchor or simple text (i.e. document frequency 
	 * of the spot).
	 */
	public Spot(String spot, List<Entity> entities,int link, int freq) {
		this.spot = spot;
		this.freq = freq;
		this.link = link;
		this.entities = entities;
		//Collections.sort(entities);
		// FIXME since some labels are produced artificially from other labels
		// (e.g., john F. kennydy -> john kennedy),
		// sometimes happens that link > freq
		linkProbability = Math.min((double) link / (double) freq, 1);
		this.idf = Math.log(collectionSize / (freq + 0.5));
		logger.debug("spot [{}] IDF = {}",spot,idf );
	}
	
	
	/** 
	 * Computes the entity commonness for a given entity and this spot, 
	 * i.e., <code>p(e|s)</code>, the probability for an entity to be associated with 
	 * this spot. It is computed dividing the number of times that the spot 
	 * links to the entity by the frequency of spot as anchor.  
	 * @param e - the entity for which to compare the commmonness
	 * @return
	 */
	public double getEntityCommonness(Entity e){
		return (double)e.getFrequency()/(double)link;
		
	}
	
	/** 
	 * Returns a copy of this object
	 */
	public Spot clone(){
		Spot copy = new Spot(spot);
		copy.setLinkProbability(linkProbability);
		copy.setEntities(entities);
		copy.setIdf(idf);
		copy.setLink(link);
		copy.setFreq(freq);
		return copy;
	}

	/**
	 * Set the probability for this spot to be a link;
	 * @param probability - the probability of this spot to be a link;
	 */
	public void setLinkProbability(double probability) {
		this.linkProbability = probability;
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
		if (spot == null) {
			if (other.spot != null)
				return false;
		} else if (!spot.equals(other.spot))
			return false;
		return true;
	}

	/**
	 * Returns the document frequency of this spot, i.e., how many wikipedia articles contain 
	 * the spot as simple text or anchor text. 
	 * @return - The document frequency of this spot;
	 */
	public int getFrequency() {
		return freq;
	}

	
	/** 
	 * Returns the inverse document frequency of this spot;
	 * @return The inverse document frequency of this spot;
	 */ 
	public double getIdf() {
		return idf;
	}	

	/** 
	 * Returns the text of this spot;
	 * @return the text of the spot;
	 */
	public String getText() {
		return spot;
	}
	
	

	

	/**
	 * @param freq the freq to set
	 */
	public void setFreq(int freq) {
		this.freq = freq;
	}

	/**
	 * @return the link
	 */
	public int getLink() {
		return link;
	}

	/**
	 * @param link the link to set
	 */
	public void setLink(int link) {
		this.link = link;
	}

	/**
	 * @return the entities
	 */
	public List<Entity> getEntities() {
		if (entities == null) return Collections.emptyList();
		return entities;
	}

	/**
	 * @param entities the entities to set
	 */
	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}

	/**
	 * @return the probability
	 */
	public double getProbability() {
		return linkProbability;
	}

	/**
	 * Set the inverse document frequency of the spot in the collection.
	 * @param idf - The inverse document frequency to set
	 */
	public void setIdf(double idf) {
		this.idf = idf;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((spot == null) ? 0 : spot.hashCode());
		return result;
	}

	/**
	 * Set the text of the spot;
	 * @param text - The text of the spot;
	 */
	public void setText(String text) {
		this.spot = text;
	}
	
	
	
	/**
	 * Set start position of the spot in the annotated text
	 * @return the start position of the spot in the annotated text
	 */
	public int getStart() {
		return start;
	}

	/**
	 * Set the start position of the spot in the annotated text
	 * @param start  the start position of the spot in the annotated text
	 */
	public void setStart(int start) {
		this.start = start;
	}
	
	
	/**
	 * Returns true if this spot and the given spots overlaps in the annotated text, 
	 * e.g., <code> "neruda pablo picasso" -> 'neruda pablo' 'pablo picasso' </code>.
	 * @param s - The spot to check
	 * @return 
	 */
	public boolean overlaps(Spot s){
		boolean startOverlap = ( (   s.getStart() >= this.getStart()  ) && (s.getStart() <= this.getEnd() )) ;
		if (startOverlap) return true;
		boolean endOverlap = ( (   s.getEnd() >= this.getStart()  ) && (s.getEnd() <= this.getEnd() )) ;
		return endOverlap;
	}

	/**
	 * Gets the end position of the spot in the annotated text
	 * @return end - the end position of the spot
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * Sets the end position of the spot in the annotated text
	 * @param end - the end position of the spot
	 */
	public void setEnd(int end) {
		this.end = end;
	}
	
	
	/**
	 * Encodes this spot in a array of byte, the encode consists in:
	 * <ul>
	 * <li> 1 byte, containing the length of the text of the spot (it is assumed that the length <code>n</code>
	 * of the spot is less than 256)</li>
	 * <li> <code>n</code> bytes, containing the spot encoded in ascii </code> </li>
	 * <li> 4 bytes, containing the frequency of the spot as link </li>
	 * <li> 4 bytes, containing the document frequency of the spot </li>
	 * <li> <code>2 x 4 x m </code> bytes, where <code> m </code> is the number of entities associated with 
	 * the spot, containing for each entity its unique id and its frequency (number of anchors with this 
	 * text that link to the entity); 
	 * @return the encoded byte array
	 */
	public byte[] toByteArray(){
		int size = entities.size();
		
		
		int len = spot.length();
		
		if (len > 255) len = 255;
		byte[] s = null;
		try {
			s = spot.getBytes("US-ASCII");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (s.length > len){ 
			// it should never happen but, just in case..
			len = 1;
			s = new byte[] {'0'};
		}
		
		//length(spot string) + spot + link (int) + freq (int) +   entities (id + freq)
		ByteBuffer buffer = ByteBuffer.allocate(1+(len)+(size*2+2)*4);
		buffer.put((byte)len);
		buffer.put(s);
		
		buffer.putInt(link);	
		buffer.putInt(freq);
		for (Entity e : entities){
			buffer.putInt(e.id());
			buffer.putInt(e.getFrequency());
		}
		return buffer.array();
	}
	
	/**
	 * Decodes a Spot from a byte representation, if the given 
	 * text match the spot text encoded in the byte array. 
	 * 
	 * @see toByteArray
	 * 
	 * @param text - the spot text to decode
	 * @param data - the binary rep for the spot
	 * @return A spot if the given string <code> text </code> matches the text of the spot 
	 * encoded in data, otherwise null
	 */
	public static Spot fromByteArray(String text,byte[] data){
		int len = (int) data[0];
		if (text.length() != len) return null;
		
		String s = null;
		try {
			s = new String(Arrays.copyOfRange(data, 1, 1+len), "US-ASCII");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (! s.equals(text)) return null;
		
		 IntBuffer intBuf =
				   ByteBuffer.wrap(Arrays.copyOfRange(data, 1+len,data.length))
				     .asIntBuffer();
		 int[] array = new int[intBuf.remaining()];
		 intBuf.get(array);
		 // List<Entity> entities = null;
		 List<Entity> entities = new ArrayList<Entity>(array.length-2);
		 
		 for (int i = 2; i < array.length ; i+=2){
			 entities.add(new Entity(array[i],array[i+1]));
		 }
		 Spot spot = new Spot(text, entities, array[0], array[1]);
		 return spot;
	}



	static IntArrayString ias = new IntArrayString(false);
	static StringBuilder sb = new StringBuilder();
	
	/** 
	 * Returns a tab separated version of the spot in a string. The string contains 
	 * <ul>
	 * <li> the entity ids of the possible entities for this spot, encoded in esadecimal </li>
	 * <li> the frequencies of the each entity for this spot, encoded in esadecimal </li>
	 * <li> the frequency of the spot as anchor text </li>
	 * <li> the document frequency of the spot in the collection </li>
	 * </ul>
	 * 
	 * @return the tab separated representation of this spot
	 */
	public String toTsv(){
		sb.setLength(0);
		List<Integer> entityId = new ArrayList<Integer>();
		List<Integer> freqs = new ArrayList<Integer>();
		for (Entity e : entities){
			entityId.add(e.id());
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
	public static Spot fromTsvLine(String text){
		Scanner scanner = new Scanner(text).useDelimiter("\t");
		String spot = scanner.next();
		List<Integer> entities = new IntArrayString(false).toArray(scanner.next());
		List<Integer> freqs = new IntArrayString(false).toArray(scanner.next());
		List<Entity> ent = new ArrayList<Entity>();
		for (int i = 0; i < entities.size(); i++){ 
			Entity e = new Entity(entities.get(i), freqs.get(i));
			ent.add(e);
		}
		int link = scanner.nextInt();
		int freq = scanner.nextInt();
		Spot s =  new Spot(spot,ent,link, freq);
		return s;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[" + spot + "] idf:" + getIdf()
				+ "\t probability:" + linkProbability +"\t link "+link);
		sb.append("\n");
		int i = 1;
		
		for (Entity e : getEntities()){
			System.out.println("e "+e.id()+" freq: "+e.getFrequency());
			sb.append("\t entity").append(i++).append("\t").append(e.id()).append("\t p(e|spot)=").append(e.getFrequency()/(double)getLink()).append("\n");
			
		}
		return sb.toString();
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
