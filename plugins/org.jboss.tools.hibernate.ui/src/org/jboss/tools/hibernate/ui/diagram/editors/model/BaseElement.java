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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * This is basis model class for diagram items.
 * Any BaseElement could be a parent, but only Shape could be a child.
 * 
 * @author Vitali
 */
public abstract class BaseElement implements IPropertySource, Comparable<BaseElement> {

	public static final String SELECTED = "selected"; //$NON-NLS-1$
	public static final String VISIBLE = "visible"; //$NON-NLS-1$
	public static final String VISIBLE_CHILDREN = "visibileChildren"; //$NON-NLS-1$
	public static final String REFRESH = "refresh"; //$NON-NLS-1$
	
	/** An empty property descriptor. */
	private static final IPropertyDescriptor[] EMPTY_ARRAY = new IPropertyDescriptor[0];

	private transient PropertyChangeSupport pcsDelegate = new PropertyChangeSupport(this);
	
	protected boolean selected = false;
	protected boolean visible = true;
	protected boolean visibleChildren = true;

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
	 * @return BaseElement
	 */
	abstract public BaseElement getParent();
	
	/**
	 * The children are items which type is Shape!
	 * In general BaseElement is not a child.
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
		if (item == null || (this == item.getParent() && children.contains(item))) {
			return false;
		}
		item.setParent(this);
		return children.add(item);
	}
	
	/**
	 * Standard way to remove child
	 * @param item
	 * @return
	 */
	public boolean removeChild(Shape item) {
		if (item == null) {
			return false;
		}
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
	
	public void sortChildren(boolean deepInto) {
		Collections.sort(children);
		if (deepInto) {
			Iterator<Shape> it = getChildrenIterator();
			while (it.hasNext()) {
				Shape me = it.next();
				me.sortChildren(deepInto);
			}
		}
	}

	public int compareTo(BaseElement be) {
		return toString().compareToIgnoreCase(be.toString());
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
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean selected) {
		boolean selectedOld = this.selected;
		//if (!visible && selected) {
		//	// invisible item can't be selected
		//	selected = false;
		//}
		this.selected = selected;
		firePropertyChange(SELECTED, Boolean.valueOf(selectedOld), Boolean.valueOf(selected));
	}
	
	public boolean isVisible() {
		//boolean visible = this.visible;
		//if (visible && getParent() != null) {
		//	visible = getParent().isVisible();
		//}
		return visible;
	}
	
	public void setVisible(boolean visible) {
		boolean visibleOld = this.visible;
		this.visible = visible;
		setVisibleChildren(visible);
		firePropertyChange(VISIBLE, Boolean.valueOf(visibleOld), Boolean.valueOf(visible));
		updateVisibleValue(this.visible);
	}

	/**
	 * updates visible value for model elements which are dependent
	 * of visible state of the current model element and update
	 * other properties which are dependent of visible state
	 */
	public void updateVisibleValue(boolean initState) {
		if (!visible) {
			setSelected(false);
		}
	}
	
	public boolean isVisibleChildren() {
		return visibleChildren;
	}
	
	public void setVisibleChildren(boolean visibleChildren) {
		boolean visibleChildrenOld = this.visibleChildren;
		this.visibleChildren = visibleChildren;
		if (visibleChildren && !visible) {
			setVisible(true);
		}
		Iterator<Shape> it = getChildrenIterator();
		while (it.hasNext()) {
			it.next().setVisible(visibleChildren);
		}
		firePropertyChange(VISIBLE_CHILDREN, Boolean.valueOf(visibleChildrenOld), Boolean.valueOf(visibleChildren));
	}
	
	public void refreshBasic() {
		firePropertyChange(REFRESH, null, null);
	}
	
	public void refresh() {
		Iterator<Shape> it = getChildrenIterator();
		while (it.hasNext()) {
			Shape shape = it.next();
			shape.refresh();
		}
		refreshBasic();
	}

	/**
	 * @return key value for object of this class
	 */
	public abstract String getKey();

	protected void setPropertyValue(Properties properties, String key, boolean value) {
		if (properties.containsKey(key)) {
			properties.remove(key);
		}
		properties.put(key, Boolean.valueOf(value).toString());
	}
	
	protected boolean getPropertyValue(Properties properties, String key) {
		String str = properties.getProperty(key, "true"); //$NON-NLS-1$
		return Boolean.valueOf(str).booleanValue();
	}
	
	public void setPrValue(Properties properties, boolean value, String propertyName) {
		setPropertyValue(properties, getKey() + "." + propertyName, value); //$NON-NLS-1$
	}
	
	public boolean getPrValue(Properties properties, String propertyName) {
		return getPropertyValue(properties, getKey() + "." + propertyName); //$NON-NLS-1$
	}
	
	protected void loadFromProperties(Properties properties) {
		boolean visibleChildren = getPrValue(properties, VISIBLE_CHILDREN);
		setVisibleChildren(visibleChildren);
		boolean visible = getPrValue(properties, VISIBLE);
		setVisible(visible);
		boolean selected = getPrValue(properties, SELECTED);
		setSelected(selected);
		Iterator<Shape> it = getChildrenIterator();
		while (it.hasNext()) {
			it.next().loadFromProperties(properties);
		}
	}

	protected void saveInProperties(Properties properties) {
		boolean visibleChildren = isVisibleChildren();
		setPrValue(properties, visibleChildren, VISIBLE_CHILDREN);
		boolean visible = isVisible();
		setPrValue(properties, visible, VISIBLE);
		boolean selected = isSelected();
		setPrValue(properties, selected, SELECTED);
		Iterator<Shape> it = getChildrenIterator();
		while (it.hasNext()) {
			it.next().saveInProperties(properties);
		}
	}
}
