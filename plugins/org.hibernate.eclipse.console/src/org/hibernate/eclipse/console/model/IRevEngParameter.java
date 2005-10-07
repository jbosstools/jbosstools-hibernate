package org.hibernate.eclipse.console.model;

public interface IRevEngParameter extends Notifiable {

	String getName();
	String getValue();
	void setName(String value);
	void setValue(String value);
}
