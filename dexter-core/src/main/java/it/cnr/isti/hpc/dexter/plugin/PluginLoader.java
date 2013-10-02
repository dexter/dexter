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

import it.cnr.isti.hpc.dexter.Spotter;
import it.cnr.isti.hpc.dexter.Tagger;
import it.cnr.isti.hpc.dexter.disambiguation.Disambiguator;
import it.cnr.isti.hpc.dexter.relatedness.Relatedness;
import it.cnr.isti.hpc.property.ProjectProperties;

import org.apache.lucene.analysis.util.ClasspathResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoader;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Sep 30, 2013
 */
public class PluginLoader {

	ResourceLoader loader;
	ProjectProperties properties = new ProjectProperties(PluginLoader.class);

	public PluginLoader() {
		loader = new ClasspathResourceLoader();
	}

	public Spotter getSpotter(String spotClass) {
		Spotter spotter = loader.newInstance(spotClass, Spotter.class);
		return spotter;
	}

	public Disambiguator getDisambiguator(String disambiguatorClass) {
		Disambiguator disambiguator = loader.newInstance(disambiguatorClass,
				Disambiguator.class);
		return disambiguator;
	}

	public Relatedness getRelatedness(String relatednessClass) {
		Relatedness relatedness = loader.newInstance(relatednessClass,
				Relatedness.class);
		return relatedness;
	}

	public Tagger getTagger(String taggerClass) {
		Tagger tagger = loader.newInstance(taggerClass, Tagger.class);
		return tagger;
	}

}
