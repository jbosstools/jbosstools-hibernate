package org.jboss.tools.hibernate.runtime.v_5_3.internal;

import org.jboss.tools.hibernate.runtime.common.AbstractFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IEnvironment;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;
import org.jboss.tools.hibernate.runtime.spi.ISession;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;

public class FacadeFactoryImpl extends AbstractFacadeFactory {

	@Override
	public ClassLoader getClassLoader() {
		return FacadeFactoryImpl.class.getClassLoader();
	}

	@Override
	public IColumn createColumn(Object target) {
		return new ColumnFacadeImpl(this, target);
	}
	
	@Override
	public IConfiguration createConfiguration(Object target) {
		return new ConfigurationFacadeImpl(this, target);
	}
	
	@Override
	public IEnvironment createEnvironment() {
		return new EnvironmentFacadeImpl(this);
	}
	
	@Override
	public IExporter createExporter(Object target) {
		return new ExporterFacadeImpl(this, target);
	}
	
	@Override
	public ISchemaExport createSchemaExport(Object target) {
		return new SchemaExportFacadeImpl(this, target);
	}
	
	@Override
	public ISessionFactory createSessionFactory(Object target) {
		return new SessionFactoryFacadeImpl(this, target);
	}
	
	@Override
	public ISession createSession(Object target) {
		return new SessionFacadeImpl(this, target);
	}
	
	@Override
	public IPersistentClass createSpecialRootClass(IProperty property) {
		return new SpecialRootClassFacadeImpl(this, property);
	}

}
