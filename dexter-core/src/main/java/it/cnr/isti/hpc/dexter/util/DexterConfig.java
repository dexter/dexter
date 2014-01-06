/**
 *  Copyright 2014 Diego Ceccarelli
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
 *  Copyright 2014 Diego Ceccarelli
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
package it.cnr.isti.hpc.dexter.util;

import it.cnr.isti.hpc.io.IOUtils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Jan 2, 2014
 */
public class DexterConfig {
	private static final Logger logger = LoggerFactory
			.getLogger(DexterConfig.class);

	Models models;
	Labels labels;
	Index index;

	public DexterConfig() {

	}

	@Override
	public String toString() {
		return "DexterConfig [ models=" + models + "]";
	}

	private static class Models {
		List<Model> models = new ArrayList<Model>();

		@Override
		public String toString() {
			return "Models [models=" + models + "]";
		}

	}

	public static class Model {

		String name;
		String path;

		@Override
		public String toString() {
			return "Model [name=" + name + ", path=" + path + "]";
		}

	}

	public static class Labels {
		String dir;
	}

	public static class Index {
		String dir;
		String wikiIdMap;
	}

	public static void main(String[] args) {
		XStream xstream = new XStream(new StaxDriver());
		xstream.alias("config", DexterConfig.class);
		xstream.alias("model", Model.class);
		xstream.alias("labels", Labels.class);
		xstream.alias("index", Index.class);

		xstream.addImplicitCollection(Models.class, "models");
		String xml = IOUtils.getFileAsString("dexter-conf.xml");
		DexterConfig config = (DexterConfig) xstream.fromXML(xml);
		Gson gson = new Gson();
		System.out.println(gson.toJson(config));
	}
}
