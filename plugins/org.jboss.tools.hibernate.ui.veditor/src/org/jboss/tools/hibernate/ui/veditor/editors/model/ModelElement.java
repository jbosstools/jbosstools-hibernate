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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

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
	
	private List<Shape> children = new OList<Shape>();
	private ModelElement parent;
	
	public List<Shape> getChildren(){
		return children;
	}
	
	public ModelElement getParent(){
		return parent;
	}
	
	public void setParent(ModelElement element){
		parent = element;
	}
	
	public OrmDiagram getOrmDiagram(){
		ModelElement element = this;
		while(true){
			if(element instanceof OrmDiagram) return (OrmDiagram)element;
			if(element.getParent() == null)break;
			element = element.getParent();
		}
		return null;
	}
	
	public ExpandeableShape getExtendeableShape(){
		ModelElement element = this;
		while(true){
			if(element instanceof ExpandeableShape) return (ExpandeableShape)element;
			if(element.getParent() == null)break;
			if(element.getParent() instanceof ExpandeableShape) return (ExpandeableShape)element.getParent();
			element = element.getParent();
		}
		return null;
	}
	
	public OrmShape getOrmShape(){
		ModelElement element = this;
		while(true){
			if(element instanceof OrmShape) return (OrmShape)element;
			if(element.getParent() == null)break;
			element = element.getParent();
		}
		return null;
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

	class OList<E> extends ArrayList<E>{
		public OList(){
			
		}
		
		public boolean add(E item){
			if(item instanceof ModelElement)((ModelElement)item).setParent(ModelElement.this);
			return super.add(item);
		}
		
		public boolean remove(Object item){
			if(item instanceof ModelElement)((ModelElement)item).setParent(null);
			return super.remove(item);
		}
	}

}
