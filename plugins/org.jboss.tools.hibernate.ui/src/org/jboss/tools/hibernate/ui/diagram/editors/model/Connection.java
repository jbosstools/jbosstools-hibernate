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

import org.hibernate.mapping.Column;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Table;

/**
 * Directed connection between 2 shapes, from source to target. 
 *
 * @author ?
 * @author Vitali Yemialyanchyk
 */
public class Connection extends BaseElement {
	
	protected Shape source;
	protected Shape target;
	
	/**
	 * supported connection types 
	 */
	public enum ConnectionType {
		ClassMapping,
		PropertyMapping,
		Association,
		ForeignKeyConstraint,
	};

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
	 * Detect connection type from connected source and target.
	 * 
	 * @return
	 */
	public ConnectionType getConnectionType() {
		if (source instanceof OrmShape && target instanceof OrmShape) {
			if ((source.getOrmElement() instanceof Table) && (target.getOrmElement() instanceof Table)) {
				return ConnectionType.ForeignKeyConstraint;
			}
			boolean bClassMapping = true;
			if (!(source.getOrmElement() instanceof RootClass || source.getOrmElement() instanceof Table)) {
				bClassMapping = false;
			}
			if (!(target.getOrmElement() instanceof RootClass || target.getOrmElement() instanceof Table)) {
				bClassMapping = false;
			}
			if (bClassMapping) {
				return ConnectionType.ClassMapping;
			}
		}
		if ((source.getOrmElement() instanceof Table && target.getOrmElement() instanceof Table) ||
			(source.getOrmElement() instanceof Table && target.getOrmElement() instanceof Column) ||
			(source.getOrmElement() instanceof Column && target.getOrmElement() instanceof Table) ||
			(source.getOrmElement() instanceof Column && target.getOrmElement() instanceof Column)) {
			return ConnectionType.ForeignKeyConstraint;
		}
		if (((source instanceof OrmShape) ^ (target instanceof OrmShape))) {
			boolean bAssociation = true;
			if (!(!(source instanceof OrmShape) && source.getOrmElement() instanceof Property) &&
				!(!(target instanceof OrmShape) && target.getOrmElement() instanceof Property)) {
				bAssociation = false;
			}
			if (bAssociation) {
				return ConnectionType.Association;
			}
		}
		return ConnectionType.PropertyMapping;
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