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
package it.cnr.isti.hpc.io.reader;

import java.util.Scanner;

import com.google.gson.Gson;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Dec 19, 2012
 */
public class TsvRecordParser implements RecordParser<TsvTuple> {
	public static Gson gson = new Gson();
	private String[] fields;
	private String delimiter = "\t";
	StringBuilder sb = new StringBuilder();

	public TsvRecordParser(String... fields) {
		this.fields = fields;
	}

	
	public TsvTuple decode(String record) {
		Scanner scanner = new Scanner(record).useDelimiter(delimiter);
		TsvTuple tuple = new TsvTuple();
		for (String f : fields){
			tuple.put(f,scanner.next());
		}
		return tuple;
	}

	
	public String encode(TsvTuple tuple) {
		sb.setLength(0);
		for (String f : fields){
			sb.append(tuple.get(f)).append("\t");
		}
		sb.setLength(sb.length()-1);
		return sb.toString();
	}

	

}
