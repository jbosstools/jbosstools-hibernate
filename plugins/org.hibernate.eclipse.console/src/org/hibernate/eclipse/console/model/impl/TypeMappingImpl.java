package org.hibernate.eclipse.console.model.impl;

import org.hibernate.eclipse.console.model.ITypeMapping;

public class TypeMappingImpl implements ITypeMapping {

	String JDBCType;
	String hibernateType;
	Integer length;
	Integer precision;
	Integer scale;
	private Boolean nullable;
	
	public String getHibernateType() {
		return hibernateType;
	}
	public void setHibernateType(String hibernateType) {
		this.hibernateType = hibernateType;
	}
	public String getJDBCType() {
		return JDBCType;
	}
	public void setJDBCType(String JDBCType) {
		this.JDBCType = JDBCType;
	}
	public Integer getLength() {
		return length;
	}
	public void setLength(Integer length) {
		this.length = length;
	}
	public Integer getPrecision() {
		return precision;
	}
	public void setPrecision(Integer precision) {
		this.precision = precision;
	}
	public Integer getScale() {
		return scale;
	}
	public void setScale(Integer scale) {
		this.scale = scale;
	}
	public Boolean getNullable() {
		return nullable;
	}
	public void setNullable(Boolean value) {
		this.nullable = value;		
	}
	
	
}
