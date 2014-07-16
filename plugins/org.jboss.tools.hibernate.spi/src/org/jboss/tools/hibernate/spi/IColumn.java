package org.jboss.tools.hibernate.spi;

public interface IColumn {

	String getName();
	Integer getSqlTypeCode();
	String getSqlType();
	int getLength();
	int getDefaultLength();
	int getPrecision();
	int getDefaultPrecision();
	int getScale();
	int getDefaultScale();
	boolean isNullable();
	IValue getValue();
	boolean isUnique();
	String getSqlType(IDialect dialect, IMapping mapping);
	void setSqlType(String sqlType);

}
