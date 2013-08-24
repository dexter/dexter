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
package it.cnr.isti.hpc.dexter.spot.clean;

import org.apache.commons.lang3.StringUtils;

/**
 * StripCleaner.java
 *
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it
 * created on 21/lug/2012
 */
public class StripCleaner implements Cleaner {
	
	private static final String DEFAULT_TRIM_CHARS = ",#*-!`{}~[]='<>:%/";
	private String trimChars;
	private boolean pre = true;
	private boolean post = true;
	public StripCleaner(){
		this(DEFAULT_TRIM_CHARS);
	}
			
	public StripCleaner(String trimChars){
		this.trimChars = trimChars;
	}
	
	public StripCleaner(String trimChars, boolean pre, boolean post){
		this(trimChars);
		this.pre = pre;
		this.post = post;
	}

	
	
	
	public String clean(String spot) {
		spot = trim(spot);
		spot = spot.replaceAll(" +"," ");
		spot = spot.trim();
		return spot;
	}

	
	public boolean post() {
		return post;
	}

	
	public boolean pre() {
		return pre;
	}
	
	private String trim(String str){
		//return str.trim();
		return StringUtils.strip(str, trimChars);
	}
	

}
