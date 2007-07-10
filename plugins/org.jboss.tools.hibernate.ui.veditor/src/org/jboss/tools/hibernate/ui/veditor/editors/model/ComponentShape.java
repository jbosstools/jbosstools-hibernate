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

import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Property;

public class ComponentShape extends ExpandeableShape {
	public static final String SET_CHILDS_HIDEN = "set childs hiden";

	protected boolean childsHiden = true;
	
	private OrmShape reference=null;
	
	public void setReference(OrmShape reference){
		this.reference = reference;
	}
	
	public OrmShape getReference(){
		return reference;
	}

	public ComponentShape(Object ioe) {	
		super(ioe);
		Shape bodyOrmShape;
		if (ioe instanceof Property) {
			Collection collection = (Collection)((Property)ioe).getValue();
			bodyOrmShape = new Shape(collection.getKey());
			bodyOrmShape.setIndent(20);
			getChildren().add(bodyOrmShape);
			bodyOrmShape = new Shape(collection.getElement());
			bodyOrmShape.setIndent(20);
			getChildren().add(bodyOrmShape);
		}
	}
	
	protected void setChildsHiden(boolean hiden) {
		for (int i = 0; i < getChildren().size(); i++)
			((Shape)getChildren().get(i)).setHiden(hiden);
	}

	public void refreshChildsHiden(OrmDiagram ormDiagram) {
		childsHiden = !childsHiden;
		
		for (int i = 0; i < getChildren().size(); i++)
			((Shape)getChildren().get(i)).setHiden(childsHiden);
		
		if(!childsHiden)
			ormDiagram.refreshComponentReferences(this);
		else
			ormDiagram.hideReferences(this);
		
		firePropertyChange(SET_CHILDS_HIDEN, null, new Boolean(childsHiden));
	}
}