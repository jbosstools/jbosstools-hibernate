package org.jboss.tools.hibernate.spi;

public interface IEnvironment {

	String getTransactionManagerStrategy();
	String getDriver();
	String getHBM2DDLAuto();
	String getDialect();
	String getDataSource();
	String getConnectionProvider();
	String getURL();
	String getUser();
	String getPass();
	String getSessionFactoryName();
	String getDefaultCatalog();
	String getDefaultSchema();
	Class<?> getWrappedClass();

}
