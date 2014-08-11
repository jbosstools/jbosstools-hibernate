package org.jboss.tools.hibernate.spi;


public interface IEntityMetamodel {

	Integer getPropertyIndexOrNull(String id);
	Object getTuplizerPropertyValue(Object entity, int i);

}
