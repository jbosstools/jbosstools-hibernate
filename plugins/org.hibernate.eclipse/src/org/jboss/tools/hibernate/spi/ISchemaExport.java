package org.jboss.tools.hibernate.spi;

import java.util.List;

public interface ISchemaExport {

	void create(boolean b, boolean c);
	List<Throwable> getExceptions();

}
