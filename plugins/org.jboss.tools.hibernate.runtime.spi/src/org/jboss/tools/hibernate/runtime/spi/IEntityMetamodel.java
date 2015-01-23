package org.jboss.tools.hibernate.runtime.spi;


public interface IEntityMetamodel {

	Integer getPropertyIndexOrNull(String id);
	Object getTuplizerPropertyValue(Object entity, int i);

}
