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

import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.jboss.tools.hibernate.runtime.spi.IValue;

/**
 * 
 * @author some modifications from Vitali
 */
public class SpecialOrmShape extends OrmShape {
	private Shape parentShape;

	public SpecialOrmShape(IPersistentClass ioe, String consoleConfigName) {
		super(ioe, consoleConfigName);
	}

	/**
	 * creates children of the shape, 
	 */
	@Override
	protected void initModel() {
		IPersistentClass rootClass = (IPersistentClass)getOrmElement();
		IProperty identifierProperty = rootClass.getIdentifierProperty();
		if (identifierProperty != null) {
			addChild(new Shape(identifierProperty, getConsoleConfigName()));
		}

		IPersistentClass src = (IPersistentClass)getOrmElement();
		if (src.getParentProperty() != null) {
			Shape bodyOrmShape = new Shape(src.getParentProperty(), getConsoleConfigName());
			addChild(bodyOrmShape);
			parentShape = bodyOrmShape;
		}
		
		Iterator<IProperty> iterator = rootClass.getPropertyIterator();
		while (iterator.hasNext()) {
			IProperty field = iterator.next();
			IValue v = field.getValue();
			IType type = getTypeUsingExecContext(v);
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
