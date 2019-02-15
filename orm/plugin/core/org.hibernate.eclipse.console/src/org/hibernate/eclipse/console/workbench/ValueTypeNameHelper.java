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
package org.hibernate.eclipse.console.workbench;

import org.jboss.tools.hibernate.runtime.spi.IValue;

public class ValueTypeNameHelper {

	/** if true then only return the classname, not the fully qualified classname */
	final boolean dequalify; 
	
	public ValueTypeNameHelper(boolean dequalify) {
		this.dequalify=dequalify;
	}
	
	private Object getTypeNameForBag(IValue bag) {
		return "Bag <" + getTypeName(bag.getCollectionElement()) + ">";  //$NON-NLS-1$//$NON-NLS-2$
	}

	private Object getTypeNameForIdBag(IValue bag) {
		return "IdBag <" + getTypeName(bag.getCollectionElement()) + ">";  //$NON-NLS-1$//$NON-NLS-2$
	}

	private Object getTypeNameForList(IValue list) {
		return "List <" + getTypeName(list.getCollectionElement()) + ">"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	private Object getTypeNameForPrimitiveArray(IValue primitiveArray) {
		return getTypeName(primitiveArray.getCollectionElement()) + "[]"; //$NON-NLS-1$
	}

	private Object getTypeNameForArray(IValue list) {
		return getTypeName(list.getCollectionElement()) + "[]"; //$NON-NLS-1$
	}

	private Object getTypeNameForMap(IValue map) {
		return "Map<" + getTypeName(map.getCollectionElement()) + ">"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	private Object getTypeNameForOneToMany(IValue many) {
		return dequalify(many.getReferencedEntityName());
	}

	private String dequalify(String referencedEntityName) {
		if(dequalify && referencedEntityName!=null && referencedEntityName.indexOf(".")>=0) {			 //$NON-NLS-1$
			return referencedEntityName.substring(referencedEntityName.lastIndexOf('.')+1);
		}
		return referencedEntityName;
	}

	private Object getTypeNameForSet(IValue set) {
		return "Set<" + getTypeName(set.getCollectionElement()) + ">"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	private Object getTypeNameForAny(IValue any) {
		return "Any"; //$NON-NLS-1$
	}

	private Object getTypeNameForSimpleValue(IValue value) {
		return dequalify(value.getTypeName());
	}

	private Object getTypeNameForDependantValue(IValue value) {
		return null;
	}

	private Object getTypeNameForComponent(IValue component) {
		return dequalify(component.getComponentClassName());
	}

	private Object getTypeNameForManyToOne(IValue mto) {
		return dequalify(mto.getReferencedEntityName());
	}

	private Object getTypeNameForOneToOne(IValue oto) {
		return dequalify(oto.getEntityName());
	}

	public Object getTypeName(IValue value) {
		if (value.isOneToOne()) {
			return getTypeNameForOneToOne(value);
		} else if (value.isManyToOne()) {
			return getTypeNameForManyToOne(value);
		} else if (value.isComponent()) {
			return getTypeNameForComponent(value);
		} else if (value.isDependantValue()) {
			return getTypeNameForDependantValue(value);
		} else if (value.isAny()) {
			return getTypeNameForAny(value);
		} else if (value.isSimpleValue()) {
			return getTypeNameForSimpleValue(value);
		} else if (value.isSet()) {
			return getTypeNameForSet(value);
		} else if (value.isOneToMany()) {
			return getTypeNameForOneToMany(value);
		} else if (value.isMap()) {
			return getTypeNameForMap(value);
		} else if (value.isPrimitiveArray()) {
			return getTypeNameForPrimitiveArray(value);
		} else if (value.isArray()) {
			return getTypeNameForArray(value);
		} else if (value.isList()) {
			return getTypeNameForList(value);
		} else if (value.isIdentifierBag()) {
			return getTypeNameForIdBag(value);
		} else if (value.isBag()) {
			return getTypeNameForBag(value);
		}
		return null;
	}
	
}
