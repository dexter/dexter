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
package it.cnr.isti.hpc.mapdb;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.Test;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 * Created on Aug 25, 2013
 */
public class MapDBTest {

	@Test
	public void test() throws IOException {
		File test = File.createTempFile("mapdbtest-", ".db");
		MapDB mapdb = new MapDB(test);
		Map collection = mapdb.getCollection("test");
		collection.put("diego", 1);
		collection.put("salvo",2);
		collection.put("dexter", 3);
		mapdb.commit();
		mapdb.close();
		mapdb = new MapDB(test);
		collection = mapdb.getCollection("test");
		assertEquals(1,collection.get("diego"));
		assertEquals(2,collection.get("salvo"));
		assertEquals(3,collection.get("dexter"));
		
		
		
	}

}
