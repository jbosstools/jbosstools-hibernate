package org.hibernate.eclipse.console.model.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.dialect.FirebirdDialect;
import org.hibernate.eclipse.console.model.IReverseEngineeringDefinition;
import org.hibernate.eclipse.console.model.ITableFilter;

public class ReverseEngineeringDefinitionImpl implements
		IReverseEngineeringDefinition {

	PropertyChangeSupport pcs = new PropertyChangeSupport(this);
		
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		pcs.addPropertyChangeListener(pcl);		
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		pcs.removePropertyChangeListener(pcl);		
	}

	public ITableFilter createTableFilter() {		
		return new TableFilterImpl();
	}

	List tableFilters = new ArrayList();
	
	public void addTableFilter(ITableFilter filter) {
		tableFilters.add(filter);
		pcs.firePropertyChange("tableFilters", null, filter);		
	}

	public ITableFilter[] getTableFilters() {
		return (ITableFilter[]) tableFilters.toArray(new ITableFilter[tableFilters.size()]);
	}

	public void removeTableFilter(ITableFilter item) {
		tableFilters.remove(item);
		pcs.firePropertyChange("tableFilters", item, null);
	}

	public void moveTableFilterDown(ITableFilter item) {
		move(item,1);		
	}

	public void moveTableFilterUp(ITableFilter item) {
		move(item,-1);		
	}

	private void move(Object tf, int shift) {
		int i = tableFilters.indexOf(tf);
		
		if(i>=0) {
			if(i+shift<tableFilters.size() && i+shift>=0) { 
				tableFilters.remove(i);
				tableFilters.add(i+shift, tf);
			}
		}
		pcs.firePropertyChange("tableFilters",null, null);
	}
}
