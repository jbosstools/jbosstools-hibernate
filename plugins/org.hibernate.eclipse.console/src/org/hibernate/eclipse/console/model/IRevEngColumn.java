package org.hibernate.eclipse.console.model;

public interface IRevEngColumn extends Notifiable {

	String getJDBCType();
	String getType();
	String getPropertyName();
	boolean getExclude();
	String getName();
	void setName(String value);
	void setPropertyName(String value);
	void setJDBCType(String value);
	void setType(String value);
	void setExcluded(boolean selection);
		
}
