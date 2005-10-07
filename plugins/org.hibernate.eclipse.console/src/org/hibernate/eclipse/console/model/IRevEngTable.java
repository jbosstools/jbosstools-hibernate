package org.hibernate.eclipse.console.model;

public interface IRevEngTable extends Notifiable {

	String getCatalog();
	String getSchema();
	String getName();
	
	IRevEngPrimaryKey getPrimaryKey();
	IRevEngColumn[] getColumns();
	void setName(String value);
	void setCatalog(String value);
	void setSchema(String value);
	void addColumn(IRevEngColumn revCol);
	void addPrimaryKey();
}
