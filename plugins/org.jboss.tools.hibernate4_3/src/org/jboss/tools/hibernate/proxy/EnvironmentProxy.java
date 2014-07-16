package org.jboss.tools.hibernate.proxy;

import org.hibernate.cfg.Environment;
import org.jboss.tools.hibernate.spi.IEnvironment;

public class EnvironmentProxy implements IEnvironment {

	@Override
	public String getTransactionManagerStrategy() {
		throw new RuntimeException("unsupported");
	}

	@Override
	public String getDriver() {
		return Environment.DRIVER;
	}

	@Override
	public String getHBM2DDLAuto() {
		return Environment.HBM2DDL_AUTO;
	}

	@Override
	public String getDialect() {
		return Environment.DIALECT;
	}

	@Override
	public String getDataSource() {
		return Environment.DATASOURCE;
	}

	@Override
	public String getConnectionProvider() {
		return Environment.CONNECTION_PROVIDER;
	}

	@Override
	public String getURL() {
		return Environment.URL;
	}

	@Override
	public String getUser() {
		return Environment.USER;
	}

	@Override
	public String getPass() {
		return Environment.PASS;
	}

	@Override
	public String getSessionFactoryName() {
		return Environment.SESSION_FACTORY_NAME;
	}

	@Override
	public String getDefaultCatalog() {
		return Environment.DEFAULT_CATALOG;
	}

	@Override
	public String getDefaultSchema() {
		return Environment.DEFAULT_SCHEMA;
	}

	@Override
	public Class<?> getWrappedClass() {
		return Environment.class;
	}

}
