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

import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Property;


public class ExtendedShape extends ExpandeableShape {
	protected List<Shape> shapes = new ArrayList<Shape>();

	protected ExtendedShape(Object  ioe) {
		super(ioe);
	}
	
	public List<Shape> getChildren() {
		return shapes;
	}
}
