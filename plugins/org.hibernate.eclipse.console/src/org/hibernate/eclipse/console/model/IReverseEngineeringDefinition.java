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
package org.hibernate.eclipse.console.model;

import java.beans.PropertyChangeListener;

public interface IReverseEngineeringDefinition {

	public static final String TABLEFILTER_STRUCTURE = "tableFilterStructure"; //$NON-NLS-1$
	public static final String TYPEMAPPING_STRUCTURE = "typeMappingStructure"; //$NON-NLS-1$
	public static final String TABLES_STRUCTURE = "tablesStructure"; //$NON-NLS-1$
	

	void addPropertyChangeListener(PropertyChangeListener pcl);
	void addPropertyChangeListener(String property, PropertyChangeListener pcl);
	void removePropertyChangeListener(PropertyChangeListener pcl);
	void removePropertyChangeListener(String property, PropertyChangeListener pcl);
	
	ITableFilter createTableFilter();
	void addTableFilter(ITableFilter filter);
	
	ITableFilter[] getTableFilters();
	void removeAllTableFilters();
	void removeTableFilter(ITableFilter item);
	void moveTableFilterDown(ITableFilter item);
	void moveTableFilterUp(ITableFilter item);
    
	
	ITypeMapping[] getTypeMappings();
	ITypeMapping createTypeMapping();
	void removeAllTypeMappings();
	void removeTypeMapping(ITypeMapping item);
	void addTypeMapping(ITypeMapping typeMapping);
	void moveTypeMappingDown(ITypeMapping item);
	void moveTypeMappingUp(ITypeMapping item);
	
	IRevEngTable[] getTables();
	IRevEngTable createTable();
	void addTable(IRevEngTable retable);
	void removeTable(IRevEngTable retable);
	void removeColumn(IRevEngColumn recolumn);
	IRevEngColumn createColumn();
	IRevEngColumn createKeyColumn();
		
}
