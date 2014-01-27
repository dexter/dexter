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
package it.cnr.isti.hpc.dexter.plugin;

import it.cnr.isti.hpc.dexter.Tagger;
import it.cnr.isti.hpc.dexter.disambiguation.Disambiguator;
import it.cnr.isti.hpc.dexter.relatedness.Relatedness;
import it.cnr.isti.hpc.dexter.spotter.Spotter;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import jodd.util.ClassLoaderUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The PluginLoader allows to include in the framework new implementations of
 * components linking project.
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Sep 30, 2013
 */
public class PluginLoader {

	// PClassLoader loader;
	// ClasspathResourceLoader luceneLoader = new ClasspathResourceLoader();

	private static final Logger logger = LoggerFactory
			.getLogger(PluginLoader.class);

	public PluginLoader(File libDir) {

		// URLClassLoader l = (URLClassLoader)
		// ClassLoader.getSystemClassLoader();

		// loader = new PClassLoader(l.getURLs());

		if (!libDir.exists() || !libDir.isDirectory()) {
			logger.warn("cannot find lib: {} ", libDir.getAbsolutePath());
			return;
		}
		for (File file : libDir.listFiles()) {
			if (file.isFile() && file.getName().endsWith(".jar")) {
				ClassLoaderUtil.addFileToClassPath(file);
				// try {
				// loader.addURL(file.toURL());
				// } catch (MalformedURLException e) {
				// logger.error("loading the library {} ", file.getName());
				// continue;
				// }
				logger.info("{} loaded ", file.getName());
			}

		}

	}

	public Spotter getSpotter(String spotClass) {
		Spotter spotter = null;
		// try {
		// spotter = (Spotter) ClasspathUtils.newInstance(spotClass, loader);
		// } catch (Exception e) {
		// logger.error("generating the spotter {}:", spotClass);
		// e.printStackTrace();
		Class c = null;
		try {
			c = Class.forName(spotClass);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// spotter = luceneLoader.newInstance(spotClass, Spotter.class);
		try {
			spotter = (Spotter) c.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// }
		return spotter;
	}

	public Disambiguator getDisambiguator(String disambiguatorClass) {
		Disambiguator disambiguator = null;
		Class c = null;
		try {
			c = Class.forName(disambiguatorClass);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// spotter = luceneLoader.newInstance(spotClass, Spotter.class);
		try {
			disambiguator = (Disambiguator) c.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// }

		return disambiguator;
	}

	public Relatedness getRelatedness(String relatednessClass) {
		Relatedness relatedness = null;
		Class c = null;
		try {
			c = Class.forName(relatednessClass);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// spotter = luceneLoader.newInstance(spotClass, Spotter.class);
		try {
			relatedness = (Relatedness) c.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// }

		return relatedness;
	}

	public Tagger getTagger(String taggerClass) {
		Tagger tagger = null;
		Class c = null;
		try {
			c = Class.forName(taggerClass);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// spotter = luceneLoader.newInstance(spotClass, Spotter.class);
		try {
			tagger = (Tagger) c.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// }

		return tagger;
	}

	private class PClassLoader extends URLClassLoader {

		/**
		 * @param urls
		 *            , to carryforward the existing classpath.
		 */
		public PClassLoader(URL[] urls) {
			super(urls);
		}

		@Override
		/** 
		 * add ckasspath to the loader. 
		 */
		public void addURL(URL url) {
			super.addURL(url);
		}
	}

}
