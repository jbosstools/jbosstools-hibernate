package org.hibernate.eclipse.console.model;

public interface IRevEngGenerator extends Notifiable {

	String getGeneratorClassName();
	
	IRevEngParameter[] getParameters();

	void setGeneratorClassName(String value);
}
