package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import org.jboss.tools.hibernate.runtime.common.AbstractFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.ICriteria;
import org.jboss.tools.hibernate.runtime.spi.IEntityMetamodel;
import org.jboss.tools.hibernate.runtime.spi.IEnvironment;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IHbm2DDLExporter;
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
	public IClassMetadata createClassMetadata(Object target) {
		return new ClassMetadataFacadeImpl(this, target);
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
	public ICriteria createCriteria(Object target) {
		return new CriteriaFacadeImpl(this, target);
	}
	
	@Override
	public IEntityMetamodel createEntityMetamodel(Object target) {
		return new EntityMetamodelFacadeImpl(this, target);
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
	public IHbm2DDLExporter createHbm2DDLExporter(Object target) {
		return new Hbm2DDLExporterFacadeImpl(this, target);
	}
	
	@Override
	public IPersistentClass createPersistentClass(Object target) {
		return new PersistentClassFacadeImpl(this, target);
	}
	
	@Override
	public ISchemaExport createSchemaExport(Object target) {
		return new SchemaExportFacadeImpl(this, target);
	}
	
	@Override
	public ISession createSession(Object target) {
		return new SessionFacadeImpl(this, target);
	}
	
	@Override
	public ISessionFactory createSessionFactory(Object target) {
		return new SessionFactoryFacadeImpl(this, target);
	}
	
	@Override
	public IPersistentClass createSpecialRootClass(IProperty property) {
		return new SpecialRootClassFacadeImpl(this, property);
	}

}
