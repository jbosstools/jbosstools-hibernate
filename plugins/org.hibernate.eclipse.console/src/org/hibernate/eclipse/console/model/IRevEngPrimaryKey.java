package org.hibernate.eclipse.console.model;

public interface IRevEngPrimaryKey extends Notifiable {

	IRevEngGenerator getGenerator();
	IRevEngColumn[] getColumns();
	void addGenerator();
	void addColumn();
	
}
