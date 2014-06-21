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

import org.jboss.tools.hibernate.spi.IValue;
import org.jboss.tools.hibernate.spi.IValueVisitor;

public class TypeNameValueVisitor implements IValueVisitor {

	/** if true then only return the classname, not the fully qualified classname */
	final boolean dequalify; 
	
	public TypeNameValueVisitor(boolean dequalify) {
		this.dequalify=dequalify;
	}
	
	private Object acceptBag(IValue bag) {
		return "Bag <" + bag.getCollectionElement().accept(this) + ">";  //$NON-NLS-1$//$NON-NLS-2$
	}

	private Object acceptIdBag(IValue bag) {
		return "IdBag <" + bag.getCollectionElement().accept(this) + ">";  //$NON-NLS-1$//$NON-NLS-2$
	}

	private Object acceptList(IValue list) {
		return "List <" + list.getCollectionElement().accept(this) + ">"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	private Object acceptPrimitiveArray(IValue primitiveArray) {
		return primitiveArray.getCollectionElement().accept(this) + "[]"; //$NON-NLS-1$
	}

	private Object acceptArray(IValue list) {
		return list.getCollectionElement().accept(this) + "[]"; //$NON-NLS-1$
	}

	private Object acceptMap(IValue map) {
		return "Map<" + map.getCollectionElement().accept(this) + ">"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	private Object acceptOneToMany(IValue many) {
		return dequalify(many.getReferencedEntityName());
	}

	private String dequalify(String referencedEntityName) {
		if(dequalify && referencedEntityName!=null && referencedEntityName.indexOf(".")>=0) {			 //$NON-NLS-1$
			return referencedEntityName.substring(referencedEntityName.lastIndexOf('.')+1);
		}
		return referencedEntityName;
	}

	private Object acceptSet(IValue set) {
		return "Set<" + set.getCollectionElement().accept(this) + ">"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	private Object acceptAny(IValue any) {
		return "Any"; //$NON-NLS-1$
	}

	private Object acceptSimpleValue(IValue value) {
		return dequalify(value.getTypeName());
	}

	private Object acceptDependantValue(IValue value) {
		return null;
	}

	private Object acceptComponent(IValue component) {
		return dequalify(component.getComponentClassName());
	}

	private Object acceptManyToOne(IValue mto) {
		return dequalify(mto.getReferencedEntityName());
	}

	private Object acceptOneToOne(IValue oto) {
		return dequalify(oto.getEntityName());
	}

	@Override
	public Object accept(IValue value) {
		if (value.isOneToOne()) {
			return acceptOneToOne(value);
		} else if (value.isManyToOne()) {
			return acceptManyToOne(value);
		} else if (value.isComponent()) {
			return acceptComponent(value);
		} else if (value.isDependantValue()) {
			return acceptDependantValue(value);
		} else if (value.isAny()) {
			return acceptAny(value);
		} else if (value.isSimpleValue()) {
			return acceptSimpleValue(value);
		} else if (value.isSet()) {
			return acceptSet(value);
		} else if (value.isOneToMany()) {
			return acceptOneToMany(value);
		} else if (value.isMap()) {
			return acceptMap(value);
		} else if (value.isPrimitiveArray()) {
			return acceptPrimitiveArray(value);
		} else if (value.isArray()) {
			return acceptArray(value);
		} else if (value.isList()) {
			return acceptList(value);
		} else if (value.isIdentifierBag()) {
			return acceptIdBag(value);
		} else if (value.isBag()) {
			return acceptBag(value);
		}
		return null;
	}
	
}
