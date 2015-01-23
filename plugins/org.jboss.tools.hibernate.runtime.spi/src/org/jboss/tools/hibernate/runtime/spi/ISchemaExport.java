package org.jboss.tools.hibernate.runtime.spi;

import java.util.List;

public interface ISchemaExport {

	void create(boolean b, boolean c);
	List<Throwable> getExceptions();

}
