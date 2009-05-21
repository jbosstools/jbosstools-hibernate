/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.console.model.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.eclipse.console.model.IRevEngColumn;
import org.hibernate.eclipse.console.model.IRevEngTable;
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
		return new TableFilterImpl(this);
	}

	List<ITableFilter> tableFilters = new ArrayList<ITableFilter>();
	private List<ITypeMapping> typeMappings = new ArrayList<ITypeMapping>();
	
	public void addTableFilter(ITableFilter filter) {
		tableFilters.add(filter);
		firePropertyChange( TABLEFILTER_STRUCTURE, null, filter );		
	}

	private void firePropertyChange(String property, Object old, Object newValue) {
		pcs.firePropertyChange(property, old, newValue);
	}

	protected void updateTableFilter(ITableFilter updated) {
		firePropertyChange("tableFilter", null, updated);		 //$NON-NLS-1$
	}
	
	public ITableFilter[] getTableFilters() {
		return tableFilters.toArray(new ITableFilter[tableFilters.size()]);
	}

	public void removeAllTableFilters() {
		tableFilters.clear();
		firePropertyChange(TABLEFILTER_STRUCTURE, null, null);
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
		List<ITableFilter> list = tableFilters;
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
		return typeMappings .toArray(new ITypeMapping[typeMappings.size()]);
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
	
	public void removeAllTypeMappings() {
		typeMappings.clear();
		firePropertyChange(TYPEMAPPING_STRUCTURE, null, null);
	}

	public void removeTypeMapping(ITypeMapping item) {
		typeMappings.remove(item);	
		firePropertyChange(TYPEMAPPING_STRUCTURE, item, null);		
	}

	public IRevEngTable[] getTables() {
		return new IRevEngTable[0];
	}

	public IRevEngTable createTable() {
		return null;
	}

	public void addTable(IRevEngTable retable) {
		
	}

	public void removeTable(IRevEngTable retable) {

	}

	public void removeColumn(IRevEngColumn recolumn) {

	}

	public IRevEngColumn createColumn() {
		return null;
	}
	
	public IRevEngColumn createKeyColumn() {
		return null;
	}
}
