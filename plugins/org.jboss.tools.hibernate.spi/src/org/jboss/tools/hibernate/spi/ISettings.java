package org.jboss.tools.hibernate.spi;

import org.hibernate.connection.ConnectionProvider;

public interface ISettings {

	ConnectionProvider getConnectionProvider();
	String getDefaultCatalogName();
	String getDefaultSchemaName();

}
