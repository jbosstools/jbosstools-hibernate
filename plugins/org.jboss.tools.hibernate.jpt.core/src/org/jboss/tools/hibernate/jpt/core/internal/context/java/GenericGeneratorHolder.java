package org.jboss.tools.hibernate.jpt.core.internal.context.java;


public interface GenericGeneratorHolder {
	
	String GENERIC_GENERATOR_PROPERTY = "genericGenerator"; //$NON-NLS-1$	
	
	JavaGenericGenerator getGenericGenerator();

	JavaGenericGenerator addGenericGenerator();
	
	void removeGenericGenerator();

}
