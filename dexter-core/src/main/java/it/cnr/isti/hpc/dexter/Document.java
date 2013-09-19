/**
 *  Copyright 2013 Salvatore Trani
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
package it.cnr.isti.hpc.dexter;

import it.cnr.isti.hpc.dexter.entity.EntityMatch;
import it.cnr.isti.hpc.dexter.entity.EntityMatchList;
import it.cnr.isti.hpc.io.IOUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Document is a set of {@link Field fields}. Each field has a name and a
 * textual value. Thus each document should typically contain one or more fields
 * which uniquely identify it. 
 * <br/>
 * A document can contains only one field with a fixed name (duplicate fields
 * are not supported).
 * 
 * @author Salvatore Trani, salvatore.trani@isti.cnr.it
 */
public class Document implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory
			.getLogger(Document.class);

	// protected List<Field> fields = new ArrayList<Field>();
	protected Map<String, Field> fields;

	/**
	 * Constructs a new document with no fields.
	 */
	public Document() {
		fields = new HashMap<String, Field>();
	}

	/**
	 * Returns the field with the given name if exists in this document, 
	 * or null.
	 * @param name The name of the field to retrieve
	 * @return The field relative to the given name, if exist, null otherwise.
	 */
	public Field getField(String name) {
		return fields.get(name);
	}

	/**
	 * Return an iterator over the fields being part of this document.
	 * 
	 * @return Iterator over the fields of the document
	 */
	public Iterator<Field> getFields() {
		return fields.values().iterator();
	}

	/**
	 * Adds a field to a document. Only one field may be added with the same
	 * name. In this case, if the fields are already in the document, it will be
	 * overwrited.
	 * 
	 * @param field
	 *            The field to add to the document
	 */
	public void addField(Field field) {
		fields.put(field.getName(), field);
	}

	/**
	 * Removes field with the specified name from the document. If there is no
	 * field with the specified name, the document remains unchanged.
	 * @param name The name of the field to remove from the document's fields
	 */
	public void removeField(String name) {
		fields.remove(name);
	}
	
	/**
	 * Get the content of the document, appending the content
	 * of the different fields belonging to it.
	 * @return The content of the document
	 */
	public String getContent() {
		StringBuffer content = new StringBuffer();
		for (Field field: fields.values()) {
			content.append(field.getValue());
		}
		return content.toString();
	}

	public String getAnnotatedText(EntityMatchList eml) {
		Collections.sort(eml, new EntityMatch.SortByPosition());
		StringBuffer sb = new StringBuffer();
		int pos = 0;
		for (EntityMatch em : eml) {
			assert em.getStart() >= 0;
			assert em.getEnd() >= 0;
			logger.info("<{},{}>", em.getStart(), em.getEnd());

			sb.append(text.substring(pos, em.getStart()));
			// the spot has been normalized, i want to retrieve the real one
			String realSpot = text.substring(em.getStart(), em.getEnd());
			sb.append(
					"<a href=\"#\" onmouseover='manage(" + em.getId() + ")' >")
					.append(realSpot).append("</a>");
			pos = em.getEnd();
		}
		if (pos < text.length()) {
			sb.append(text.substring(pos));
		}

		return sb.toString();
	}

	public String getAnnotatedText(EntityMatchList eml, int nEntities) {
		eml.sort();
		EntityMatchList emlSub = new EntityMatchList();
		int size = Math.min(nEntities, eml.size());
		for (int i = 0; i < size; i++) {
			emlSub.add(eml.get(i));
		}
		return getAnnotatedText(emlSub);
	}
}
