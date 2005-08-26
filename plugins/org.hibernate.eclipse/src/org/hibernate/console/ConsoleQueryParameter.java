package org.hibernate.console;

import org.hibernate.HibernateException;
import org.hibernate.type.NullableType;


public class ConsoleQueryParameter {

	static public final Object NULL_MARKER = new Object() { public String toString() { return "[null]"; } };
	
	String name;
	NullableType type;
	Object value;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public NullableType getType() {
		return type;
	}
	
	public void setType(NullableType type) {
		this.type = type;
	}
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		if(value == null) { throw new IllegalArgumentException("Value must not be set to null"); };
		this.value = value;
	}
	
	public String getValueAsString() {
		if(getValue()==NULL_MARKER) return "";
		return type.toString(getValue());
	}
	
	public void setValueFromString(String value) {
		try {
			Object object = type.fromStringValue(value);
			setValue(object);
		} catch(HibernateException he) {
			setValue(NULL_MARKER);
		}
	}
}
