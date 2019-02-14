package org.jboss.tools.hibernate.jpt.core.internal.context;

import org.eclipse.jpt.jpa.core.context.Converter;


public interface TypeConverter extends Converter {
	
	String getHibernateType();
	void setHibernateType(String type);
		String TYPE_PROPERTY = "type"; //$NON-NLS-1$


}
