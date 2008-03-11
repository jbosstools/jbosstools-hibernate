/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.veditor.editors.model;

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.hibernate.core.IOrmElement;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.hibernate.ICollectionMapping;


public class ExtendedShape extends Shape {

	protected List<Shape> shapes = new ArrayList<Shape>();

	protected ExtendedShape(IOrmElement  ioe) {
		super(ioe);
		Shape bodyOrmShape;
		if (ioe instanceof IPersistentField && ((IPersistentField)ioe).getMapping().getPersistentValueMapping() instanceof ICollectionMapping) {
			ICollectionMapping collectionMapping = (ICollectionMapping)((IPersistentField)ioe).getMapping().getPersistentValueMapping();
			bodyOrmShape = new Shape(collectionMapping.getKey());
			bodyOrmShape.setIndent(20);
			shapes.add(bodyOrmShape);
			bodyOrmShape = new Shape(collectionMapping.getElement());
			bodyOrmShape.setIndent(20);
			shapes.add(bodyOrmShape);
		}
			
	}
	
	public List<Shape> getChildren() {
		return shapes;
	}
	
	protected void setHiden(boolean hiden) {
		super.setHiden(hiden);
		for (int i = 0; i < shapes.size(); i++)
			((Shape)shapes.get(i)).setHiden(hiden);
	}
}
