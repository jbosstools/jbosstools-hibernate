package org.jboss.tools.hibernate.spi;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;

public interface ICfg2HbmTool {

	String getTag(PersistentClass persistentClass);
	String getTag(Property property);

}
