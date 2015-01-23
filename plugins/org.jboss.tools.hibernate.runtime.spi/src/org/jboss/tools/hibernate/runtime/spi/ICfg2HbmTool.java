package org.jboss.tools.hibernate.runtime.spi;


public interface ICfg2HbmTool {

	String getTag(IPersistentClass persistentClass);
	String getTag(IProperty property);

}
