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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * This is basis model class for diagram items.  
 */
public abstract class ModelElement implements IPropertySource {

	/** An empty property descriptor. */
	private static final IPropertyDescriptor[] EMPTY_ARRAY = new IPropertyDescriptor[0];

	private transient PropertyChangeSupport pcsDelegate = new PropertyChangeSupport(this);

	public synchronized void addPropertyChangeListener(PropertyChangeListener l) {
		if (l == null) {
			throw new IllegalArgumentException();
		}
		pcsDelegate.addPropertyChangeListener(l);
	}
	
	protected void firePropertyChange(String property, Object oldValue, Object newValue) {
		if (pcsDelegate.hasListeners(property)) {
			pcsDelegate.firePropertyChange(property, oldValue, newValue);
		}
	}
	
	public synchronized void removePropertyChangeListener(PropertyChangeListener l) {
		if (l != null) {
			pcsDelegate.removePropertyChangeListener(l);
		}
	}

	/**
	 * The result is parent or null if the object has no parent
	 * @return ModelElement
	 */
	abstract public ModelElement getParent();
	
	/**
	 * The children are items which type is Shape!
	 * In general ModelElement is not a child.
	 */
	private ArrayList<Shape> children = new ArrayList<Shape>();
	
	public Iterator<Shape> getChildrenIterator() {
		return children.iterator();
	}
	
	/**
	 * Return copy of children list (to prevent modification of internal array)
	 * @return
	 */
	public List<Shape> getChildrenList() {
		ArrayList<Shape> copy = new ArrayList<Shape>();
		Iterator<Shape> it = getChildrenIterator();
		while (it.hasNext()) {
			copy.add(it.next());
		}
		return copy;
	}
	
	/**
	 * Number of children
	 * @return
	 */
	public int getChildrenNumber() {
		return children.size();
	}
	
	/**
	 * Standard way to add child
	 * @param item
	 * @return
	 */
	public boolean addChild(Shape item) {
		item.setParent(this);
		return children.add(item);
	}
	
	/**
	 * Standard way to remove child
	 * @param item
	 * @return
	 */
	public boolean removeChild(Shape item) {
		item.setParent(null);
		return children.remove(item);
	}
	
	/**
	 * Clear all children
	 */
	public void deleteChildren() {
		Iterator<Shape> it = getChildrenIterator();
		while (it.hasNext()) {
			Shape me = it.next();
			me.setParent(null);
		}
		children.clear();
	}
	
	public Object getEditableValue() {
		return this;
	}

	/**
	 * Children should override this. The default implementation returns an empty array.
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return EMPTY_ARRAY;
	}

	/**
	 * Children should override this. The default implementation returns null.
	 */
	public Object getPropertyValue(Object id) {
		return null;
	}

	/**
	 * Children should override this. The default implementation returns false.
	 */
	public boolean isPropertySet(Object id) {
		return false;
	}

	/**
	 * Children should override this. The default implementation does nothing.
	 */
	public void resetPropertyValue(Object id) {
		// do nothing
	}

	/**
	 * Children should override this. The default implementation does nothing.
	 */
	public void setPropertyValue(Object id, Object value) {
		// do nothing
	}
}
