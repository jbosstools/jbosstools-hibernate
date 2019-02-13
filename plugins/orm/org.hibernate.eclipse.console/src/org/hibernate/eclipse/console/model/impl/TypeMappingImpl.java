/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
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
