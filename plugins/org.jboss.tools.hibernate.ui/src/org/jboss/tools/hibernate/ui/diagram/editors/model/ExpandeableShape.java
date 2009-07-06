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

public class ExpandeableShape extends Shape {
	
	public static final String SHOW_REFERENCES = "show references"; //$NON-NLS-1$
	
	public boolean refHide = true;
	protected boolean first = false;
	
	private OrmShape reference = null;
	
	public ExpandeableShape(Object ioe) {
		super(ioe);
	}
	
	public void setReference(OrmShape reference) {
		this.reference = reference;
	}
	
	public OrmShape getReference() {
		return reference;
	}
	
	public boolean isReferenceVisible() {
		return refHide;
	}

	protected boolean getHide() {
		return refHide;
	}

	public void refreshReferences(Object model) {
		refHide = !refHide;
		if (model instanceof OrmDiagram) {
			if (refHide) {
				if (first) {
					((OrmDiagram)model).processExpand(this);
					first = false;
				}
			}
		}
		firePropertyChange(SHOW_REFERENCES, null, Boolean.valueOf(refHide));
	}
}
