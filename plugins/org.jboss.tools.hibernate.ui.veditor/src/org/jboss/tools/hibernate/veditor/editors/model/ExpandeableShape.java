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

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.RGB;
import org.jboss.tools.hibernate.veditor.editors.parts.ResourceManager;

/**
 * @author Konstantin Mishin
 *
 */
public class ExpandeableShape extends Shape {
	
	public static final String SHOW_REFERENCES = "show references";
	
	private boolean refHide = false;
	protected List<Shape> shapes = new ArrayList<Shape>();

	public List<Shape> getChildren() {
		return shapes;
	}

	public ExpandeableShape(Object ioe) {
		super(ioe);
	}

	public void refreshReferences(Object model) {
		refHide = !refHide;
		if (model instanceof OrmDiagram) {
			((OrmDiagram)model).processExpand(this);
		}
		firePropertyChange(SHOW_REFERENCES, null, new Boolean(refHide));
	}

	protected boolean getHide() {
		return refHide;
	}
}
