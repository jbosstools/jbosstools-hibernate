package org.hibernate.eclipse.console.model;

public interface IRevEngPrimaryKey {

	IRevEngGenerator getGenerator();
	IRevEngColumn[] getColumns();
	
}
