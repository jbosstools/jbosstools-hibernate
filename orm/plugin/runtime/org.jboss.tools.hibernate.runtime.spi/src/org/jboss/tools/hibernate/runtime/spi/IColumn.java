package org.jboss.tools.hibernate.runtime.spi;

public interface IColumn {

	String getName();
	Integer getSqlTypeCode();
	String getSqlType();
	long getLength();
	int getDefaultLength();
	int getPrecision();
	int getDefaultPrecision();
	int getScale();
	int getDefaultScale();
	boolean isNullable();
	IValue getValue();
	boolean isUnique();
	String getSqlType(IConfiguration configuration);
	void setSqlType(String sqlType);

}
