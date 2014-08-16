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
package org.hibernate.console;

import org.jboss.tools.hibernate.spi.IService;
import org.jboss.tools.hibernate.spi.IType;
import org.jboss.tools.hibernate.spi.ITypeFactory;


public class ConsoleQueryParameter {

	static private final Object NULL_MARKER = null; //new Object() { public String toString() { return "[null]"; } };
	
	private ITypeFactory typeFactory;
	
	String name;
	IType type;
	Object value;
	
	public ConsoleQueryParameter(IService service, ConsoleQueryParameter cqp) {
		this(service);
		name = cqp.name;
		type = cqp.type;
		value = cqp.value;
	}

	public ConsoleQueryParameter(IService service) {
		this.typeFactory = service.newTypeFactory();
	}

	public ConsoleQueryParameter(IService service, String name, IType type, Object value) {
		this(service);
		this.name = name;
		this.type = type;
		this.value = value;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getTypeName() {
		return type.getName();
	}
	
	public void setType(IType type) {
		this.type = type;
	}
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		//if(value == null) { throw new IllegalArgumentException("Value must not be set to null"); }
		this.value = value;
	}

	public String[] getStringValues() {
		if(value == null) return new String[]{""}; //$NON-NLS-1$
		if (value.getClass().isArray()){
			Object[] arr = (Object[])value;
			String[] values = new String[arr.length];
			for (int i = 0; i < arr.length; i++) {
				values[i] = type.toString(arr[i]);
			}
			return values;
		} else {
			return new String[]{type.toString(value)};
		}
	}
	
	public Object convertStringToValue(String value){
		try {
			return type.fromStringValue(value);
		} catch(Exception he) {
			return NULL_MARKER;
		}
	}

	
	public String getDefaultFormat() {
		if(type!=null) {
			Object object = typeFactory.getTypeFormats().get(type);
			if(object!=null) {
				return object.toString();
			}
		}
		return "<unknown>";				 //$NON-NLS-1$
	}

	public void setNull() {
		setValue( NULL_MARKER );
	}
	
	public boolean isNull() {
		return getValue()==NULL_MARKER;
	}

	public Object getValueForQuery() {
		if(isNull()) {
			return null;
		} else {
			return getValue();
		}	
	}
}
