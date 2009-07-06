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

public class Connection extends ModelElement {
	
	public static final String HIDE_SELECTION = "hide selection"; //$NON-NLS-1$
	public static final String SHOW_SELECTION = "show selection"; //$NON-NLS-1$
	public static final String SET_HIDEN = "set hiden"; //$NON-NLS-1$
	
	private Shape source;
	private Shape target;
	
	private int needHide;
		
	public Connection(Shape s, Shape newTarget) {
		if (s == null || newTarget == null || s == newTarget) {
			throw new IllegalArgumentException();
		}
		needHide = 2;
		this.source = s;
		this.target = newTarget;
		source.addConnection(this);
		target.addConnection(this);
	}			
	
	public Shape getSource() {
		return source;
	}
	
	public Shape getTarget() {
		return target;
	}
			
	public void hideSelection() {
		firePropertyChange(HIDE_SELECTION, null, null);
		source.firePropertyChange(Shape.HIDE_SELECTION, null, null);
		target.firePropertyChange(Shape.HIDE_SELECTION, null, null);
	}

	public void showSelection() {
		firePropertyChange(SHOW_SELECTION, null, null);
		source.firePropertyChange(Shape.SHOW_SELECTION, null, null);
		target.firePropertyChange(Shape.SHOW_SELECTION, null, null);
	}
	
	public void setHidden(boolean hiden) {
		if (hiden) {
			needHide--;
			if (needHide == 0) {
				return;
			}
		} else {
			needHide++;
			if (needHide == 1) {
				return;
			}
		}
		firePropertyChange(SET_HIDEN, null, Boolean.valueOf(hiden));
	}

	public boolean isHiden() {
		return needHide != 2;
	}

	/**
	 * It has no parent
	 */
	@Override
	public ModelElement getParent() {
		return null;
	}
}