package it.cnr.isti.hpc.dexter.document;

import it.cnr.isti.hpc.io.IOUtils;

import java.io.File;
import java.util.Iterator;

/**
 * A FlatDocument is a Document with only one Field.
 * <br/> 
 * For simplicity the name of that field will be: "body".
 * 
 * @author Salvatore Trani, salvatore.trani@isti.cnr.it created on 18/sept/2013
 */	
public class FlatDocument extends MultifieldDocument {
	private static final long serialVersionUID = 1L;
	protected final String fieldName = "body";
	
	public FlatDocument() {
		super();
	}

	/**
	 * FlatDocument build with the "text" value as unique field  
	 * 
	 * @param text The content of the unique "body" field of the document
	 */
	public FlatDocument(String text) {
		super();
		Field body = new Field(fieldName, text);
		addField(body);
	}
	
	/**
	 * FlatDocument build with the content taken from a file
	 * @param file The file where the content has to be taken
	 */
	public FlatDocument(File file) {
		this(IOUtils.getFileAsUTF8String(file.getAbsolutePath()));
	}

	/**
	 * Set the content of the flat document (the body field)
	 * @param content The content of the document
	 */
	public void setContent(String content) {
		fields.get(fieldName).setValue(content);
	}
	
	public void addField(Field field) {
		if (field.getName().equals(fieldName))
			super.addField(field);
	}
	
	public void removeField(String name) {
		if (name.equals(fieldName))
			super.removeField(name);
	}
}
