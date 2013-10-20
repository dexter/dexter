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
package it.cnr.isti.hpc.dexter.document;

import java.io.Serializable;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Document interface defines as a set of {@link Field fields}. Each
 * field has a name and a textual value. Thus each document should typically
 * contain one or more fields which uniquely identify it. <br/>
 * A document can contain only one field with a fixed name (duplicate fields
 * are not supported).
 * 
 * @author Salvatore Trani, salvatore.trani@isti.cnr.it
 */
public abstract class Document implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(Document.class);
	
	/**
	 * Returns the field with the given name if exists in this document, or
	 * null.
	 * 
	 * @param name
	 *            The name of the field to retrieve
	 * @return The field relative to the given name, if exist, null otherwise.
	 */
	public abstract Field getField(String name);
	
	/**
	 * Returns an iterator over the fields being part of this document.
	 * 
	 * @return Iterator over the fields of the document
	 */
	public abstract Iterator<Field> getFields();
	
	/**
	 * Adds a field to a document. Only one field may be added with the same
	 * name. In this case, if the fields are already in the document, it will be
	 * overwrited.
	 * 
	 * @param field
	 *            The field to add to the document
	 */
	public abstract void addField(Field field);
	
	/**
	 * Removes field with the specified name from the document. If there is no
	 * field with the specified name, the document remains unchanged.
	 * 
	 * @param name
	 *            The name of the field to remove from the document's fields
	 */
	public abstract void removeField(String name);
	
	/**
	 * Gets the content of the document, appending the content of the different
	 * fields belonging to it.
	 * 
	 * @return The content of the document
	 */
	public abstract String getContent();

}
