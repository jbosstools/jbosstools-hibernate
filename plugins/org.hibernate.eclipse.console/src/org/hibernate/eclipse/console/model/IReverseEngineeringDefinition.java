package org.hibernate.eclipse.console.model;

import java.beans.PropertyChangeListener;

public interface IReverseEngineeringDefinition {

	public static final String TABLEFILTER_STRUCTURE = "tableFilterStructure";
	public static final String TYPEMAPPING_STRUCTURE = "typeMappingStructure";
	

	void addPropertyChangeListener(PropertyChangeListener pcl);
	void addPropertyChangeListener(String property, PropertyChangeListener pcl);
	void removePropertyChangeListener(PropertyChangeListener pcl);
	void removePropertyChangeListener(String property, PropertyChangeListener pcl);
	
	ITableFilter createTableFilter();
	void addTableFilter(ITableFilter filter);
	ITableFilter[] getTableFilters();
	void removeTableFilter(ITableFilter item);
	void moveTableFilterDown(ITableFilter item);
	void moveTableFilterUp(ITableFilter item);

	ITypeMapping[] getTypeMappings();
	ITypeMapping createTypeMapping();
	void addTypeMapping(ITypeMapping typeMapping);

	

	
		
}
