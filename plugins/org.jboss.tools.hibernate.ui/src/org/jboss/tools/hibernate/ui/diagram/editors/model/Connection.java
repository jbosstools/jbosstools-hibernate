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

/**
 * Directed connection between 2 shapes, from source to target. 
 *
 * @author some modifications from Vitali
 */
public class Connection extends BaseElement {
	
	protected Shape source;
	protected Shape target;

	/**
	 * flag to prevent cycle call of updateVisibleValue()
	 */
	protected boolean inUpdateVisibleValue = false;
		
	public Connection(Shape s, Shape newTarget) {
		if (s == null || newTarget == null || s == newTarget) {
			throw new IllegalArgumentException();
		}
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

	/**
	 * It has no children, so not possible to add.
	 */
	public boolean addChild(Shape item) {
		return false;
	}
	
	@Override
	public void setSelected(boolean selected) {
		source.setSelected(selected);
		target.setSelected(selected);
		super.setSelected(selected);
	}
	
	@Override
	public void updateVisibleValue(boolean initState) {
		if (inUpdateVisibleValue) {
			return;
		}
		inUpdateVisibleValue = true;
		boolean visible = initState;
		visible = visible && source.isVisible();
		visible = visible && target.isVisible();
		setVisible(visible);
		super.updateVisibleValue(this.visible);
		inUpdateVisibleValue = false;
	}

	/**
	 * It has no parent
	 */
	@Override
	public BaseElement getParent() {
		return null;
	}
	
	@Override
	public void refresh() {
		updateVisibleValue(isVisible());
		super.refresh();
	}

	@Override
	public String getKey() {
		return null;
	}
}