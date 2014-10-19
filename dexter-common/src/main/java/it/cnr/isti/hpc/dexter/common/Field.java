package it.cnr.isti.hpc.dexter.common;

import java.io.Serializable;

/**
 * A field is the basic unit being part of a Document object. Each field is
 * described by a name and a value (both textual).
 * 
 * @author Salvatore Trani, salvatore.trani@isti.cnr.it
 */
public class Field implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String value;

	public Field(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Field other = (Field) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}