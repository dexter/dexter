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
package it.cnr.isti.hpc.dexter.hash;

import it.cnr.isti.hpc.dexter.hash.jdbm.JDBMIdToLabel;
import it.cnr.isti.hpc.dexter.hash.jdbm.JDBMLabelToId;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Oct 3, 2012
 */
public class IdHelperFactory {

	
	public static IdHelper stdIdHelper = null; 
	
	public static IdToLabelWriter stdIdToLabelWriter;
	public static LabelToIdWriter stdLabelToIdWriter;
	
	public enum Type {
		RAM, JDBM, HADOOP
	};
	public static final Type STD_TYPE = Type.JDBM;
	

	public IdHelperFactory() {

	}

	public static IdHelper getHashHelper(Type type) {
		if (type == Type.JDBM) {
			IdToLabel h2l = JDBMIdToLabel.getInstance();
			LabelToId l2h = JDBMLabelToId.getInstance();
			IdHelper helper = new IdHelper(h2l, l2h);
			return helper;
		}
//		if (type == Type.HADOOP){
//			IdToLabel h2l = new IdToLabelHadoopMapfile();
//			LabelToId l2h = new LabelToIdHadoopMapfile();
//			IdHelper helper = new IdHelper(h2l, l2h);
//			return helper;
//		}
		throw new UnsupportedOperationException("Helper is not supported for type "+type);
	}
	
	public static LabelToIdWriter getLabelToIdWriter(Type type) {
		if (type == Type.JDBM) {
			return JDBMLabelToId.getInstance();
		}
//		if (type == Type.HADOOP){
//			return new LabelToIdHadoopWriter();
//		}
		throw new UnsupportedOperationException("Helper is not supported for type "+type);
	}
	
	public static IdToLabelWriter getIdToLabelWriter(Type type) {
		if (type == Type.JDBM) {
			return JDBMIdToLabel.getInstance();
		}
//		if (type == Type.HADOOP){
//			return new IdToLabelHadoopWriter();
//		}
		throw new UnsupportedOperationException("Helper is not supported for type "+type);
	}
	
	public static LabelToIdWriter getStdLabelToIdWriter(){
		if (stdLabelToIdWriter == null){
			stdLabelToIdWriter = getLabelToIdWriter(STD_TYPE);
			
		}
		return stdLabelToIdWriter;
	}
	
	public static IdToLabelWriter getStdIdToLabelWriter(){
		if (stdIdToLabelWriter == null){
			stdIdToLabelWriter = getIdToLabelWriter(STD_TYPE);
			
		}
		return stdIdToLabelWriter;
	}
	
	
	
	
	public static IdHelper getStdIdHelper(){
		if (stdIdHelper == null){
			stdIdHelper = getHashHelper(STD_TYPE);
			
		}
		return stdIdHelper;
	}

}
