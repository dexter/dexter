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

import com.google.gson.Gson;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 * Created on Dec 19, 2012
 */
public class JsonRecordParser<E> implements RecordParser<E>{
	public static Gson gson = new Gson();
	public Class<E> clazz;
	
	public JsonRecordParser(Class<E> clazz){
		this.clazz = clazz;
	}
	
	
	public E decode(String record){
		return gson.fromJson(record, clazz);
	}
	public String encode(E obj){
		return gson.toJson(obj);
	}
}
