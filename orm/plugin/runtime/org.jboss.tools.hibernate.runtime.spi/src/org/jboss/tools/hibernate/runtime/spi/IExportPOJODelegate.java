package org.jboss.tools.hibernate.runtime.spi;

import java.util.Map;

public interface IExportPOJODelegate {

	void exportPOJO(Map<Object, Object> map, IPOJOClass pojoClass);

}
