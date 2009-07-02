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
package org.jboss.tools.hibernate.ui.diagram.editors.model;

import java.util.Iterator;

import org.hibernate.MappingException;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.execution.ExecutionContext.Command;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.type.Type;

public class SpecialOrmShape extends OrmShape {
	private Shape parentShape;

	public SpecialOrmShape(SpecialRootClass ioe) {
		super(ioe);
//		generate();
	}

	@SuppressWarnings("unchecked")
	protected void generate() {
		Shape bodyOrmShape;
		RootClass rootClass = (RootClass)getOrmElement();
		Property identifierProperty = rootClass.getIdentifierProperty();
		if (identifierProperty != null) {
			addChild(new Shape(identifierProperty));
		}

		SpecialRootClass src = (SpecialRootClass)getOrmElement();
		if (src.getParentProperty() != null) {
			bodyOrmShape = new Shape(src.getParentProperty());
			addChild(bodyOrmShape);
			parentShape = bodyOrmShape;
		}
		
		Iterator<Property> iterator = rootClass.getPropertyIterator();
		while (iterator.hasNext()) {
			Property field = iterator.next();
			Type type = null;
			if (getOrmDiagram() != null){
				ConsoleConfiguration cfg = getOrmDiagram().getConsoleConfig();
				final Property fField = field;
				type = (Type) cfg.execute(new Command(){
					public Object execute() {
						return fField.getValue().getType();
					}});								
			} else {
				try{
					type = field.getValue().getType();
				} catch (MappingException e){
					//type is not accessible
					HibernateConsolePlugin.getDefault().logErrorMessage("MappingException: ", e); //$NON-NLS-1$
				}
			}
			if (type != null && type.isEntityType()) {
				bodyOrmShape = new ExpandeableShape(field);
			} else if (type != null && type.isCollectionType()) {
				bodyOrmShape = new ComponentShape(field);
			} else {
				bodyOrmShape = new Shape(field);
			}
			addChild(bodyOrmShape);
		}
	}

	protected Shape getParentShape() {
		return parentShape;
	}

}
