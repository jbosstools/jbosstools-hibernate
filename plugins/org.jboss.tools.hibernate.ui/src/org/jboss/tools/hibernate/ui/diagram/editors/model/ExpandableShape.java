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

import java.util.Properties;

import org.eclipse.ui.IMemento;

/**
 * Shape with two intrinsic states: expand and collapse.
 * 
 * @author some modifications from Vitali
 */
public class ExpandableShape extends Shape {
	
	public static final String EXPANDED = "expanded"; //$NON-NLS-1$

	protected boolean expanded = true;
	
	public ExpandableShape(Object ioe) {
		super(ioe);
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		if (expanded) {
			expand();
		} else {
			collapse();
		}
	}

	public boolean expand() {
		if (!isVisible()) {
			// prohibit expand and collapse in invisible mode
			return false;
		}
		boolean expandedOld = this.expanded;
		expanded = true;
		setVisibleChildren(true);
		firePropertyChange(EXPANDED, Boolean.valueOf(expandedOld), Boolean.valueOf(expanded));
		return true;
	}

	public boolean collapse() {
		if (!isVisible()) {
			// prohibit expand and collapse in invisible mode
			return false;
		}
		boolean expandedOld = this.expanded;
		expanded = false;
		setVisibleChildren(false);
		firePropertyChange(EXPANDED, Boolean.valueOf(expandedOld), Boolean.valueOf(expanded));
		return true;
	}
	
	@Override
	public void setVisible(boolean visible) {
		boolean visibleOld = this.visible;
		this.visible = visible;
		if (expanded) {
			// set children visible only if in expanded state
			setVisibleChildren(visible);
		}
		firePropertyChange(VISIBLE, Boolean.valueOf(visibleOld), Boolean.valueOf(visible));
		// update connections visibility state
		updateVisibleValue(this.visible);
	}
	
	@Override
	public void loadState(IMemento memento) {
		super.loadState(memento);
		boolean expanded = getPrValue(memento, EXPANDED, true);
		setExpanded(expanded);
	}
	
	@Override
	protected void loadFromProperties(Properties properties) {
		super.loadFromProperties(properties);
		boolean expanded = getPrValue(properties, EXPANDED, true);
		setExpanded(expanded);
	}

	@Override
	public void saveState(IMemento memento) {
		boolean expanded = isExpanded();
		setPrValue(memento, EXPANDED, expanded);
		super.saveState(memento);
	}

	@Override
	protected void saveInProperties(Properties properties) {
		boolean expanded = isExpanded();
		setPrValue(properties, EXPANDED, expanded);
		super.saveInProperties(properties);
	}
}
