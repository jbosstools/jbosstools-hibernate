package org.hibernate.eclipse.console.model.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.eclipse.console.model.IReverseEngineeringDefinition;
import org.hibernate.eclipse.console.model.ITableFilter;
import org.hibernate.eclipse.console.model.ITypeMapping;

public class ReverseEngineeringDefinitionImpl implements
		IReverseEngineeringDefinition {

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
		
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		pcs.addPropertyChangeListener(pcl);		
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		pcs.removePropertyChangeListener(pcl);		
	}
	
	public void addPropertyChangeListener(String property, PropertyChangeListener pcl) {
		pcs.addPropertyChangeListener(property, pcl);
	}

	public void removePropertyChangeListener(String property, PropertyChangeListener pcl) {
		pcs.removePropertyChangeListener(property, pcl);
	}

	public ITableFilter createTableFilter() {		
		return new TableFilterImpl();
	}

	List tableFilters = new ArrayList();
	private List typeMappings = new ArrayList();
	
	public void addTableFilter(ITableFilter filter) {
		tableFilters.add(filter);
		firePropertyChange( TABLEFILTER_STRUCTURE, null, filter );		
	}

	private void firePropertyChange(String property, Object old, Object newValue) {
		pcs.firePropertyChange(property, old, newValue);
	}

	public ITableFilter[] getTableFilters() {
		return (ITableFilter[]) tableFilters.toArray(new ITableFilter[tableFilters.size()]);
	}

	public void removeTableFilter(ITableFilter item) {
		tableFilters.remove(item);
		firePropertyChange(TABLEFILTER_STRUCTURE, item, null);
	}

	public void moveTableFilterDown(ITableFilter item) {
		move(item,1);		
	}

	public void moveTableFilterUp(ITableFilter item) {
		move(item,-1);		
	}

	private void move(Object tf, int shift) {
		List list = tableFilters;
		String prop = TABLEFILTER_STRUCTURE;
		move( tf, shift, list, prop );
	}

	private void move(Object tf, int shift, List list, String prop) {
		int i = list.indexOf(tf);
		
		if(i>=0) {
			if(i+shift<list.size() && i+shift>=0) { 
				list.remove(i);
				list.add(i+shift, tf);
			}
		}
		
		firePropertyChange(prop, null, null);
	}

	public ITypeMapping[] getTypeMappings() {
		return (ITypeMapping[]) typeMappings .toArray(new ITypeMapping[typeMappings.size()]);
	}

	public ITypeMapping createTypeMapping() {
		return new TypeMappingImpl();
	}

	public void addTypeMapping(ITypeMapping typeMapping) {
		typeMappings.add(typeMapping);
		firePropertyChange(TYPEMAPPING_STRUCTURE, null, typeMapping);
	}

	public void moveTypeMappingDown(ITypeMapping item) {
		move(item, 1, typeMappings, TYPEMAPPING_STRUCTURE);		
	}

	public void moveTypeMappingUp(ITypeMapping item) {
		move(item, -1, typeMappings, TYPEMAPPING_STRUCTURE);		
	}

	public void removeTypeMapping(ITypeMapping item) {
		typeMappings.remove(item);		
	}
}
