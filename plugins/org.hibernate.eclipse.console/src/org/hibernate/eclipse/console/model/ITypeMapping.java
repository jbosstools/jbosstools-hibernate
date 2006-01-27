package org.hibernate.eclipse.console.model;

public interface ITypeMapping {

	String getJDBCType();

	String getHibernateType();

	Integer getLength();

	Integer getPrecision();

	Integer getScale();

	void setJDBCType(String string);

	void setLength(Integer string);

	void setHibernateType(String string);

	void setPrecision(Integer string);

	void setScale(Integer integer);

	Boolean getNullable();
	
	void setNullable(Boolean value);

}
