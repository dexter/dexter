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
package it.cnr.isti.hpc.dexter.label.mapdb;

import it.cnr.isti.hpc.dexter.util.DexterParams;
import it.cnr.isti.hpc.mapdb.MapDB;

/**
 * An instance of a MapDB object
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 05/lug/2012
 */
public class MapDBInstance {

	private static MapDB db;

	private static DexterParams dexterParams = DexterParams.getInstance();

	public static MapDB getInstance() {
		if (db == null) {
			db = MapDB.getDb(dexterParams.getLabelDir().getAbsolutePath());
		}
		return db;

	}

	public static MapDB getInstance(boolean readonly) {
		if (db == null) {
			db = MapDB.getDb(dexterParams.getLabelDir().getAbsolutePath(),
					readonly);
		}
		return db;

	}

}
