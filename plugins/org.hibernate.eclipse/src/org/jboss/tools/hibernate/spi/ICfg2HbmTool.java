package org.jboss.tools.hibernate.spi;

import org.hibernate.mapping.Property;


public interface ICfg2HbmTool {

	String getTag(IPersistentClass persistentClass);
	String getTag(Property property);

}
