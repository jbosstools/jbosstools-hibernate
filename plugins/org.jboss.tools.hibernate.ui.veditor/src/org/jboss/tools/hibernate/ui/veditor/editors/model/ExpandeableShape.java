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

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.RGB;
import org.jboss.tools.hibernate.ui.veditor.editors.parts.ResourceManager;

public class ExpandeableShape extends Shape {
	
	public static final String SHOW_REFERENCES = "show references";
	
	protected boolean refHide = false;
	protected boolean first=true;
	
	private OrmShape reference=null;
	
	public void setReference(OrmShape reference){
		this.reference = reference;
	}
	
	public OrmShape getReference(){
		return reference;
	}
	
	public boolean isReferenceVisible(){
		return refHide;
	}
	
	public ExpandeableShape(Object ioe) {
		super(ioe);
	}

	public void refreshReferences(Object model) {
		refHide = !refHide;
		if (model instanceof OrmDiagram) {
			if(refHide){
				if(first){
					((OrmDiagram)model).processExpand(this);
					first = false;
				}
			}
		}
		firePropertyChange(SHOW_REFERENCES, null, new Boolean(refHide));
	}

	protected boolean getHide() {
		return refHide;
	}
}
