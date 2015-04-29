package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IEnvironment;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public abstract class AbstractEnvironmentFacade 
extends AbstractFacade 
implements IEnvironment {

	public AbstractEnvironmentFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	public String getTransactionManagerStrategy() {
		return (String) Util.getFieldValue(
				getEnvironmentClass(), 
				"TRANSACTION_MANAGER_STRATEGY", 
				null);
	}
	
	@Override
	public String getDriver() {
		return (String) Util.getFieldValue(
				getEnvironmentClass(), 
				"DRIVER", 
				null);
	}

	@Override
	public String getHBM2DDLAuto() {
		return (String) Util.getFieldValue(
				getEnvironmentClass(), 
				"HBM2DDL_AUTO", 
				null);
	}

	@Override
	public String getDialect() {
		return (String) Util.getFieldValue(
				getEnvironmentClass(), 
				"DIALECT", 
				null);
	}

	@Override
	public String getDataSource() {
		return (String) Util.getFieldValue(
				getEnvironmentClass(), 
				"DATASOURCE", 
				null);
	}

	@Override
	public String getConnectionProvider() {
		return (String) Util.getFieldValue(
				getEnvironmentClass(), 
				"CONNECTION_PROVIDER", 
				null);
	}

	@Override
	public String getURL() {
		return (String) Util.getFieldValue(
				getEnvironmentClass(), 
				"URL", 
				null);
	}

	@Override
	public String getUser() {
		return (String) Util.getFieldValue(
				getEnvironmentClass(), 
				"USER", 
				null);
	}

	@Override
	public String getPass() {
		return (String) Util.getFieldValue(
				getEnvironmentClass(), 
				"PASS", 
				null);
	}

	@Override
	public String getSessionFactoryName() {
		return (String) Util.getFieldValue(
				getEnvironmentClass(), 
				"SESSION_FACTORY_NAME", 
				null);
	}

	@Override
	public String getDefaultCatalog() {
		return (String) Util.getFieldValue(
				getEnvironmentClass(), 
				"DEFAULT_CATALOG", 
				null);
	}

	@Override
	public String getDefaultSchema() {
		return (String) Util.getFieldValue(
				getEnvironmentClass(), 
				"DEFAULT_SCHEMA", 
				null);
	}

	@Override
	public Class<?> getWrappedClass() {
		return getEnvironmentClass();
	}

	protected Class<?> getEnvironmentClass() {
		return Util.getClass(getEnvironmentClassName(), getFacadeFactoryClassLoader());
	}

	protected String getEnvironmentClassName() {
		return "org.hibernate.cfg.Environment";
	}
	
}
