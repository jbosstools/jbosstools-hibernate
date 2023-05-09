package org.jboss.tools.hibernate.runtime.spi;

import java.util.Map;

public interface IExportPOJODelegate {

	void exportPOJO(Map<Object, Object> map, IPOJOClass pojoClass);
	
	default void exportPojo(Map<Object, Object> map, Object pojoClass) {
		exportPOJO(map, (IPOJOClass)pojoClass);
	}
	
	default void exportPojo(Map<Object, Object> map, Object pojoClass, String fullyQualifiedName) {
		exportPojo(map, pojoClass);
	}

}
