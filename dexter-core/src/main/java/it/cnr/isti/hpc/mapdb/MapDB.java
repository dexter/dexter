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
package it.cnr.isti.hpc.mapdb;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MapDB abstracts the mapdb framework, allowing to create big maps that are
 * stored on the disk and to access them with high performance.
 * 
 * @see <a href="http://www.mapdb.org"> MapDB</a>
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 05/lug/2012
 */
public class MapDB {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(MapDB.class);

	private static final String DEFAULT_DB_NAME = "mapdb.db";
	// wrapped db instance
	private DB db;

	private boolean readonly = true;

	private MapDB(String dbFolder) {
		File folderPath = new File(dbFolder);
		// if (dbFolder.endsWith(".zip"))
		// openZipDb(folderPath);

		openDb(folderPath, DEFAULT_DB_NAME);

	}

	private MapDB(String dbFolder, boolean readonly) {
		this.readonly = readonly;
		File folderPath = new File(dbFolder);
		// if (dbFolder.endsWith(".zip"))
		// openZipDb(folderPath);

		openDb(folderPath, DEFAULT_DB_NAME);

	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	// private void openZipDb(File dbPath) {
	// logger.info("open zip db in {} ", dbPath.getAbsolutePath());
	// db = DBMaker.openZip(dbPath.getAbsolutePath()).make();
	//
	// }

	public MapDB(String path, String dbFolder) {
		File folderPath = new File(path, dbFolder);
		// if (dbFolder.endsWith(".zip"))
		// openZipDb(folderPath);
		openDb(folderPath, DEFAULT_DB_NAME);
	}

	public MapDB(String path, String dbFolder, boolean readonly) {
		this.readonly = readonly;
		File folderPath = new File(path, dbFolder);
		// if (dbFolder.endsWith(".zip"))
		// openZipDb(folderPath);
		openDb(folderPath, DEFAULT_DB_NAME);
	}

	public MapDB(File dbPath, boolean readonly) {
		this.readonly = readonly;
		// NOTE: i don't need transactions, disabling should improve speed
		if (readonly) {
			db = DBMaker.newFileDB(dbPath).transactionDisable()
					.closeOnJvmShutdown().readOnly().make();
		} else {
			db = DBMaker.newFileDB(dbPath).transactionDisable()
					.closeOnJvmShutdown().make();
		}
	}

	public MapDB(File dbPath) {
		logger.info("open db in {} ", dbPath.getAbsolutePath());

		// NOTE: i don't need transactions, disabling should improve speed
		if (readonly) {
			db = DBMaker.newFileDB(dbPath).transactionDisable()
					.closeOnJvmShutdown().readOnly().make();
		} else {
			db = DBMaker.newFileDB(dbPath).transactionDisable()
					.closeOnJvmShutdown().make();
		}
	}

	private void openDb(File dbFolder, String dbName) {
		if (!dbFolder.exists())
			dbFolder.mkdir();
		if (!dbFolder.isDirectory())
			throw new MapDBException("file " + dbFolder
					+ " exists but it is not a directory");
		File dbPath = new File(dbFolder, dbName);
		logger.info("open db in {} ", dbPath.getAbsolutePath());
		if (readonly) {
			// NOTE: i don't need transactions, disabling should improve speed
			db = DBMaker.newFileDB(dbPath).transactionDisable()
					.closeOnJvmShutdown().readOnly().make();
		} else {
			db = DBMaker.newFileDB(dbPath).transactionDisable()
					.closeOnJvmShutdown().make();
		}

	}

	public static MapDB getDb(String dbName) {
		return new MapDB(dbName);
	}

	public static MapDB getDb(String path, String dbName) {
		return new MapDB(path, dbName);
	}

	public static MapDB getDb(String dbName, boolean readonly) {
		return new MapDB(dbName, readonly);
	}

	public static MapDB getDb(String path, String dbName, boolean readonly) {
		return new MapDB(path, dbName, readonly);
	}

	public boolean hasCollection(String collection) {
		return db.getAll().containsKey(collection);
	}

	public void rmCollection(String collection) {
		db.delete(collection);
		db.commit();
	}

	public Set<String> getCollections() {
		return db.getAll().keySet();
	}

	public Map getCollection(String collection) {
		// if (hasCollection(collection)) {
		logger.info("get collection {} ", collection);
		return db.getTreeMap(collection);
		// } else {
		// logger.info("create collection {} ", collection);
		// return db.create(collection);
		// }

	}

	public void commit() {
		db.commit();
	}

	public void close() {
		db.close();
	}

}
