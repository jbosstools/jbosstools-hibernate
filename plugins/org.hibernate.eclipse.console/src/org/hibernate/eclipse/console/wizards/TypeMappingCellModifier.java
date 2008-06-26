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
package org.hibernate.eclipse.console.wizards;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;
import org.hibernate.cfg.reveng.SQLTypeMapping;
import org.hibernate.eclipse.console.model.ITypeMapping;

final public class TypeMappingCellModifier implements ICellModifier {
	private final TableViewer tv;

	public TypeMappingCellModifier(TableViewer tv) {
		this.tv = tv;
	}

	public void modify(Object element, String property, Object value) {
		ITypeMapping tf = (ITypeMapping) ((TableItem)element).getData();
		if("jdbctype".equals(property)) {
			if(!safeEquals( value, tf.getJDBCType() )) {
				tf.setJDBCType((String) value);
			}
		}
		if("hibernatetype".equals(property)) {
			if(!safeEquals( value, tf.getHibernateType())) {
				tf.setHibernateType((String) value);
			}
		}
		if("length".equals(property)) {
			if(!safeEquals(value, tf.getLength())) {
				tf.setLength((Integer) value);
			}
		}
		if("precision".equals(property)) {
			if(!safeEquals(value, tf)) {
				tf.setPrecision((Integer) value);
			}
		}
		if("scale".equals(property)) {
			if(!safeEquals(value,tf.getScale())) {
				tf.setScale((Integer) value);
			}
		}
		if("not-null".equals(property)) {
			Boolean integerToBoolean = notnullToNullable((Integer) value);
			if(!safeEquals(integerToBoolean,tf.getNullable())) {
				tf.setNullable(integerToBoolean);
			}
		}
		tv.update(new Object[] { tf }, new String[] { property });
	}

	private Boolean notnullToNullable(Integer value) {
		if(value.intValue()==1) return Boolean.FALSE;
		if(value.intValue()==0) return Boolean.TRUE;
		if(value.intValue()==2) return SQLTypeMapping.UNKNOWN_NULLABLE;
		return SQLTypeMapping.UNKNOWN_NULLABLE;
	}

	private boolean safeEquals(Object value, Object tf) {
		if(value==tf) return true;
		if(value==null) return false;
		return value.equals(tf);
	}

	public Object getValue(Object element, String property) {
		ITypeMapping tf = (ITypeMapping) element;
		if("precision".equals(property)) {
			return tf.getPrecision();
		}
		if("jdbctype".equals(property)) {
			return tf.getJDBCType();
		}
		if("hibernatetype".equals(property)) {
			return tf.getHibernateType();
		}
		if("scale".equals(property)) {
			return tf.getScale();
		}		

		if("length".equals(property)) {
			return tf.getLength();
		}
		
		if("not-null".equals(property)) {
			if(tf.getNullable()==null) {
				return new Integer(2);
			}
			if(tf.getNullable().booleanValue()) {
				return new Integer(0);
			} else {
				return new Integer(1);
			}
		}		
		
		return null;
	}

	public boolean canModify(Object element, String property) {
		return true;
	}
}