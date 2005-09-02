package org.hibernate.eclipse.console.model;

import java.beans.PropertyChangeListener;

public interface IReverseEngineeringDefinition {

	ITableFilter createTableFilter();
	
	void addTableFilter(ITableFilter filter);
	
	void addPropertyChangeListener(PropertyChangeListener pcl);
	void removePropertyChangeListener(PropertyChangeListener pcl);

	ITableFilter[] getTableFilters();

	void removeTableFilter(ITableFilter item);

	void moveTableFilterDown(ITableFilter item);

	void moveTableFilterUp(ITableFilter item);

	
		
}
