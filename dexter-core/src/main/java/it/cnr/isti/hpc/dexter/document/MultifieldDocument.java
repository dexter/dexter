package it.cnr.isti.hpc.dexter.document;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A document with an unordered set of fields.
 * 
 * @author Salvatore Trani, salvatore.trani@isti.cnr.it
 */
public class MultifieldDocument extends Document {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory
			.getLogger(MultifieldDocument.class);

	// protected List<Field> fields = new ArrayList<Field>();
	protected Map<String, Field> fields;

	/**
	 * Constructs a new document with no fields.
	 */
	public MultifieldDocument() {
		fields = new HashMap<String, Field>();
	}

	/**
	 * Returns the field with the given name if exists in this document, or
	 * null.
	 * 
	 * @param name
	 *            The name of the field to retrieve
	 * @return The field relative to the given name, if exist, null otherwise.
	 */
	public Field getField(String name) {
		return fields.get(name);
	}

	/**
	 * Returns an iterator over the fields being part of this document.
	 * 
	 * @return Iterator over the fields of the document
	 */
	public Iterator<Field> getFields() {
		return fields.values().iterator();
	}

	/**
	 * Adds a field to a document. Only one field may be added with the same
	 * name. In this case, if the fields are already in the document, it will be
	 * overwritten.
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
	 * 
	 * @param name
	 *            The name of the field to remove from the document's fields
	 */
	public void removeField(String name) {
		fields.remove(name);
	}

	/**
	 * Gets the content of the document, appending the content of the different
	 * fields belonging to it.
	 * 
	 * @return The content of the document
	 */
	public String getContent() {
		StringBuffer content = new StringBuffer();
		for (Field field : fields.values()) {
			content.append(field.getValue());
		}
		return content.toString();
	}
}

