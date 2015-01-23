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

import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.model.IConsoleConfigurationNameProvider;
import org.hibernate.eclipse.console.model.IRevEngColumn;
import org.hibernate.eclipse.console.model.IRevEngGenerator;
import org.hibernate.eclipse.console.model.IRevEngParameter;
import org.hibernate.eclipse.console.model.IRevEngPrimaryKey;
import org.hibernate.eclipse.console.model.IRevEngTable;
import org.hibernate.eclipse.console.model.IReverseEngineeringDefinition;
import org.hibernate.eclipse.console.model.ITableFilter;
import org.hibernate.eclipse.console.model.ITypeMapping;
import org.jboss.tools.hibernate.runtime.spi.IService;

public class ReverseEngineeringDefinitionImpl implements
		IReverseEngineeringDefinition {
	
	private IConsoleConfigurationNameProvider consoleConfigurationNameProvider;
	
	public ReverseEngineeringDefinitionImpl(IConsoleConfigurationNameProvider ccnp) {
		consoleConfigurationNameProvider = ccnp;
	}

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
	
	IService getService() {
		String consoleConfigurationName = consoleConfigurationNameProvider.getConsoleConfigurationName();
		if (consoleConfigurationName == null || "".equals(consoleConfigurationName)) return null; //$NON-NLS-1$
		ConsoleConfiguration cc = KnownConfigurations.getInstance().find(consoleConfigurationName);
		if (cc == null) return null;
		return cc.getHibernateExtension().getHibernateService();
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
		moveTableFilter(item,1);		
	}

	public void moveTableFilterUp(ITableFilter item) {
		moveTableFilter(item,-1);		
	}

	private void moveTableFilter(ITableFilter tf, int shift) {
		int i = tableFilters.indexOf(tf);		
		if(i>=0) {
			if(i+shift<tableFilters.size() && i+shift>=0) { 
				tableFilters.remove(i);
				tableFilters.add(i+shift, tf);
			}
		}		
		firePropertyChange(TABLEFILTER_STRUCTURE, null, null);
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
		moveTypeMapping(item, 1);		
	}

	public void moveTypeMappingUp(ITypeMapping item) {
		moveTypeMapping(item, -1);		
	}
	
	private void moveTypeMapping(ITypeMapping tm, int shift) {
		int i = typeMappings.indexOf(tm);		
		if(i>=0) {
			if(i+shift<typeMappings.size() && i+shift>=0) { 
				typeMappings.remove(i);
				typeMappings.add(i+shift, tm);
			}
		}		
		firePropertyChange(TYPEMAPPING_STRUCTURE, null, null);
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
	
	public void removeGenerator(IRevEngGenerator regenerator) {
		
	}

	public void removeParameter(IRevEngParameter reparam) {
		
	}

	public void removePrimaryKey(IRevEngPrimaryKey reprimaryKey) {
		
	}

	public IRevEngColumn createColumn() {
		return null;
	}
	
	public IRevEngColumn createKeyColumn() {
		return null;
	}
}
