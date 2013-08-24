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
package it.cnr.isti.hpc.dexter.spot;

import it.cnr.isti.hpc.io.reader.RecordParser;

import java.util.Scanner;

/**
 * Contains the text of a spot and its document frequency in the collection.
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 * Created on Apr 29, 2013
 */
public class SpotFrequency {
	String spot;
	int freq;
	
	
	public SpotFrequency(){
		
	}
	
	public SpotFrequency(String spot, int freq) {
		super();
		this.spot = spot;
		this.freq = freq;
	}
	/**
	 * @return the spot
	 */
	public String getSpot() {
		return spot;
	}
	/**
	 * @param spot the spot to set
	 */
	public void setSpot(String spot) {
		this.spot = spot;
	}
	/**
	 * @return the freq
	 */
	public int getFreq() {
		return freq;
	}
	/**
	 * @param freq the freq to set
	 */
	public void setFreq(int freq) {
		this.freq = freq;
	} 
	
	/**
	 * Parse a line containing the encoded version of a SpotFrequency object.
	 * 
	 *  @see RecordParser
	 */
	public static class Parser implements RecordParser<SpotFrequency>{

		
		public SpotFrequency decode(String record) {
			SpotFrequency rec = new SpotFrequency();
			Scanner scanner = new Scanner(record).useDelimiter("\t");
			rec.setSpot(scanner.next());
			rec.setFreq(scanner.nextInt());
			return rec;
		}

		
		public String encode(SpotFrequency r) {
			return r.spot+"\t"+r.freq;
		}
	}
		

	
	
	
}


