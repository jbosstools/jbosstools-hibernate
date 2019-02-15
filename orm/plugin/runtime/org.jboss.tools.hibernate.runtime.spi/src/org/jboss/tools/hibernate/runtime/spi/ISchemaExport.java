package org.jboss.tools.hibernate.runtime.spi;

import java.util.List;

public interface ISchemaExport {

	void create();
	List<Throwable> getExceptions();

}
