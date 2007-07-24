/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.veditor.editors.model;

import java.util.Iterator;

import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;

public class SpecialOrmShape extends OrmShape {
	private Shape parentShape;

	public SpecialOrmShape(SpecialRootClass ioe) {
		super(ioe);
//		generate();
	}

	protected void generate() {
		Shape bodyOrmShape;
		RootClass rootClass = (RootClass)getOrmElement();
		Property identifierProperty = rootClass.getIdentifierProperty();
		if (identifierProperty != null) getChildren().add(new Shape(identifierProperty));

		SpecialRootClass src = (SpecialRootClass)getOrmElement();
		if (src.getParentProperty() != null) {
			bodyOrmShape = new Shape(src.getParentProperty());
			getChildren().add(bodyOrmShape);
			parentShape = bodyOrmShape;
		}
		
		Iterator iterator = rootClass.getPropertyIterator();
		while (iterator.hasNext()) {
			Property field = (Property)iterator.next();
			if (field.getValue().getType().isEntityType()) {
				bodyOrmShape = new ExpandeableShape(field);
			} else if (field.getValue().getType().isCollectionType()) {
				bodyOrmShape = new ComponentShape(field);
			} else {
				bodyOrmShape = new Shape(field);
			}
			getChildren().add(bodyOrmShape);
		}
	}

	protected Shape getParentShape() {
		return parentShape;
	}

}
