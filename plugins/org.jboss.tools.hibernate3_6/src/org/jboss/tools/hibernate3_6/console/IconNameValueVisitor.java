/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate3_6.console;

import org.hibernate.console.ImageConstants;
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

/**
 * 
 * @author dgeraskov
 *
 */
final class IconNameValueVisitor implements ValueVisitor {
	
	public Object accept(OneToOne oto) {
		return ImageConstants.ONETOONE;
	}

	public Object accept(ManyToOne mto) {
		return ImageConstants.MANYTOONE;
	}

	public Object accept(Component component) {
		return ImageConstants.COMPONENT;
	}

	public Object accept(DependantValue value) {
		return ImageConstants.UNKNOWNPROPERTY;
	}

	public Object accept(SimpleValue value) {
		return ImageConstants.PROPERTY;
	}

	public Object accept(Any any) {
		return ImageConstants.PROPERTY;
	}

	public Object accept(Set set) {
		return ImageConstants.MANYTOONE;
	}	

	public Object accept(OneToMany many) {
		return ImageConstants.ONETOMANY;
	}

	public Object accept(Map map) {
		return ImageConstants.MANYTOONE;
	}

	public Object accept(Array list) {
		return ImageConstants.MANYTOONE;
	}

	public Object accept(PrimitiveArray primitiveArray) {
		return ImageConstants.MANYTOONE;			
	}

	public Object accept(List list) {
		return ImageConstants.MANYTOONE;
	}

	public Object accept(IdentifierBag bag) {
		return ImageConstants.MANYTOONE;
	}

	public Object accept(Bag bag) {
		return ImageConstants.MANYTOONE;
	}
	
}