package org.jboss.tools.hibernate.spi;

import org.hibernate.mapping.PersistentClass;

public interface ICfg2HbmTool {

	String getTag(PersistentClass persistentClass);
	String getTag(IProperty property);

}
