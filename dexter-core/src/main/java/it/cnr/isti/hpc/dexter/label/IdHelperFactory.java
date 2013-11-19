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
package it.cnr.isti.hpc.dexter.label;

import it.cnr.isti.hpc.dexter.label.mapdb.MapDBIdToLabel;
import it.cnr.isti.hpc.dexter.label.mapdb.MapDBLabelToId;

/**
 * Generates an IdHelper that takes care to convert the entity labels (i.e., the
 * titles of the Wikipedia pages containing the description of the entity) in
 * integer identifiers used for internal processing.
 * 
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

	public static IdHelper getIdHelper(Type type, boolean readonly) {
		if (type == Type.JDBM) {
			IdToLabel h2l = MapDBIdToLabel.getInstance(readonly);
			LabelToId l2h = MapDBLabelToId.getInstance(readonly);
			IdHelper helper = new IdHelper(h2l, l2h);
			return helper;
		}
		// if (type == Type.HADOOP){
		// IdToLabel h2l = new IdToLabelHadoopMapfile();
		// LabelToId l2h = new LabelToIdHadoopMapfile();
		// IdHelper helper = new IdHelper(h2l, l2h);
		// return helper;
		// }
		throw new UnsupportedOperationException(
				"Helper is not supported for type " + type);
	}

	public static LabelToIdWriter getLabelToIdWriter(Type type) {
		if (type == Type.JDBM) {
			return MapDBLabelToId.getInstance(false);
		}
		// if (type == Type.HADOOP){
		// return new LabelToIdHadoopWriter();
		// }
		throw new UnsupportedOperationException(
				"Helper is not supported for type " + type);
	}

	public static IdToLabelWriter getIdToLabelWriter(Type type) {
		if (type == Type.JDBM) {
			return MapDBIdToLabel.getInstance(false);
		}
		// if (type == Type.HADOOP){
		// return new IdToLabelHadoopWriter();
		// }
		throw new UnsupportedOperationException(
				"Helper is not supported for type " + type);
	}

	public static LabelToIdWriter getStdLabelToIdWriter() {
		if (stdLabelToIdWriter == null) {
			stdLabelToIdWriter = getLabelToIdWriter(STD_TYPE);

		}
		return stdLabelToIdWriter;
	}

	public static IdToLabelWriter getStdIdToLabelWriter() {
		if (stdIdToLabelWriter == null) {
			stdIdToLabelWriter = getIdToLabelWriter(STD_TYPE);

		}
		return stdIdToLabelWriter;
	}

	/**
	 * returns the standard id helper (read only mode), you should probably want
	 * to use this.
	 */
	public static IdHelper getStdIdHelper() {
		if (stdIdHelper == null) {
			// read only mode
			stdIdHelper = getIdHelper(STD_TYPE, true);

		}
		return stdIdHelper;
	}

}
