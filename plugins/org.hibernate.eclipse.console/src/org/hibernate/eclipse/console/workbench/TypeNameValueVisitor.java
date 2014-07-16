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

import org.hibernate.mapping.Any;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.IdentifierBag;
import org.hibernate.mapping.List;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PrimitiveArray;
import org.hibernate.mapping.Set;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.ValueVisitor;

public class TypeNameValueVisitor implements ValueVisitor {

	/** if true then only return the classname, not the fully qualified classname */
	final boolean dequalify; 
	
	public TypeNameValueVisitor(boolean dequalify) {
		this.dequalify=dequalify;
	}
	
	public Object accept(Bag bag) {
		return "Bag <" + bag.getElement().accept(this) + ">";  //$NON-NLS-1$//$NON-NLS-2$
	}

	public Object accept(IdentifierBag bag) {
		return "IdBag <" + bag.getElement().accept(this) + ">";  //$NON-NLS-1$//$NON-NLS-2$
	}

	public Object accept(List list) {
		return "List <" + list.getElement().accept(this) + ">"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public Object accept(PrimitiveArray primitiveArray) {
		return primitiveArray.getElement().accept(this) + "[]"; //$NON-NLS-1$
	}

	public Object accept(Array list) {
		return list.getElement().accept(this) + "[]"; //$NON-NLS-1$
	}

	public Object accept(Map map) {
		return "Map<" + map.getElement().accept(this) + ">"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public Object accept(OneToMany many) {
		return dequalify(many.getReferencedEntityName());
	}

	private String dequalify(String referencedEntityName) {
		if(dequalify && referencedEntityName!=null && referencedEntityName.indexOf(".")>=0) {			 //$NON-NLS-1$
			return referencedEntityName.substring(referencedEntityName.lastIndexOf('.')+1);
		}
		return referencedEntityName;
	}

	public Object accept(Set set) {
		return "Set<" + set.getElement().accept(this) + ">"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public Object accept(Any any) {
		return "Any"; //$NON-NLS-1$
	}

	public Object accept(SimpleValue value) {
		return dequalify(value.getTypeName());
	}

	public Object accept(DependantValue value) {
		return null;
	}

	public Object accept(Component component) {
		return dequalify(component.getComponentClassName());
	}

	public Object accept(ManyToOne mto) {
		return dequalify(mto.getReferencedEntityName());
	}

	public Object accept(OneToOne oto) {
		return dequalify(oto.getEntityName());
	}

}
