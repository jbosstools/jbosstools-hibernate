/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.diagram.editors.model;

import java.util.Iterator;

import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.type.Type;

/**
 * 
 * @author some modifications from Vitali
 */
public class SpecialOrmShape extends OrmShape {
	private Shape parentShape;

	public SpecialOrmShape(SpecialRootClass ioe, String consoleConfigName) {
		super(ioe, consoleConfigName);
	}

	/**
	 * creates children of the shape, 
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void initModel() {
		RootClass rootClass = (RootClass)getOrmElement();
		Property identifierProperty = rootClass.getIdentifierProperty();
		if (identifierProperty != null) {
			addChild(new Shape(identifierProperty, getConsoleConfigName()));
		}

		SpecialRootClass src = (SpecialRootClass)getOrmElement();
		if (src.getParentProperty() != null) {
			Shape bodyOrmShape = new Shape(src.getParentProperty(), getConsoleConfigName());
			addChild(bodyOrmShape);
			parentShape = bodyOrmShape;
		}
		
		Iterator<Property> iterator = rootClass.getPropertyIterator();
		while (iterator.hasNext()) {
			Property field = iterator.next();
			Type type = getTypeUsingExecContext(field.getValue());
			Shape bodyOrmShape = null;
			if (type != null && type.isEntityType()) {
				bodyOrmShape = new ExpandableShape(field, getConsoleConfigName());
			} else if (type != null && type.isCollectionType()) {
				bodyOrmShape = new ComponentShape(field, getConsoleConfigName());
			} else {
				bodyOrmShape = new Shape(field, getConsoleConfigName());
			}
			addChild(bodyOrmShape);
		}
	}

	protected Shape getParentShape() {
		return parentShape;
	}

}
