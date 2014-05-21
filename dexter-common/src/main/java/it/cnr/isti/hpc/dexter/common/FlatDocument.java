package it.cnr.isti.hpc.dexter.common;

import it.cnr.isti.hpc.io.IOUtils;

import java.io.File;

/**
 * A FlatDocument is a Document with only one Field. <br/>
 * By default the name of that field will be: "body".
 * 
 * @author Salvatore Trani, salvatore.trani@isti.cnr.it created on 18/sept/2013
 */
public class FlatDocument extends MultifieldDocument {
	private static final long serialVersionUID = 1L;
	private static String fieldName = "body";

	public FlatDocument() {
		super();
	}

	/**
	 * FlatDocument build with the "text" value as unique field
	 * 
	 * @param text
	 *            The content of the unique "body" field of the document
	 */
	public FlatDocument(String text) {
		super();
		Field body = new Field(fieldName, text);
		fields.put(fieldName, body);
	}

	/**
	 * FlatDocument build with the content taken from a file
	 * 
	 * @param file
	 *            The file where the content has to be taken
	 */
	public FlatDocument(File file) {
		this(IOUtils.getFileAsUTF8String(file.getAbsolutePath()));
	}

	/**
	 * Set the content of the flat document (the body field)
	 * 
	 * @param content
	 *            The content of the document
	 */
	public void setContent(String content) {
		fields.get(fieldName).setValue(content);
	}

	@Override
	public void addField(Field field) {
		throw new UnsupportedOperationException(
				"flat document must have only one field");
	}

	@Override
	public void removeField(String name) {
		throw new UnsupportedOperationException(
				"flat document must have only one field");
	}

	public String getFieldName() {
		return fieldName;
	}

	/**
	 * Set the (unique) field name of the flat document
	 */
	public static void setFieldName(String fieldName) {
		FlatDocument.fieldName = fieldName;
	}

}
