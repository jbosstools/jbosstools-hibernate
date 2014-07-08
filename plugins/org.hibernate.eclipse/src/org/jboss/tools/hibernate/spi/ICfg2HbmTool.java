package org.jboss.tools.hibernate.spi;


public interface ICfg2HbmTool {

	String getTag(IPersistentClass persistentClass);
	String getTag(IProperty property);

}
