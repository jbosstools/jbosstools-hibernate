package org.hibernate.eclipse.console.model;

import java.beans.PropertyChangeListener;

public interface Notifiable {

	public void addPropertyChangeListener(PropertyChangeListener pcl);
	public void removePropertyChangeListener(PropertyChangeListener pcl);
	public void addPropertyChangeListener(String property, PropertyChangeListener pcl);
	public void removePropertyChangeListener(String property, PropertyChangeListener pcl);

}
