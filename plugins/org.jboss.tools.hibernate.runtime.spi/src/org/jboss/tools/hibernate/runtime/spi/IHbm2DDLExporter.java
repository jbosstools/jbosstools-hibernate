package org.jboss.tools.hibernate.runtime.spi;

import java.util.Hashtable;

public interface IHbm2DDLExporter {

	void setExport(boolean parseBoolean);
	Hashtable<Object, Object> getProperties();

}
