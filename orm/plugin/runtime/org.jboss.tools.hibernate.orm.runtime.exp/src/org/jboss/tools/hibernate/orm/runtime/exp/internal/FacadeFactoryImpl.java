package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import org.jboss.tools.hibernate.runtime.common.AbstractFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.ICriteria;
import org.jboss.tools.hibernate.runtime.spi.IEnvironment;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IForeignKey;
import org.jboss.tools.hibernate.runtime.spi.IGenericExporter;
import org.jboss.tools.hibernate.runtime.spi.IHQLQueryPlan;
import org.jboss.tools.hibernate.runtime.spi.IHbm2DDLExporter;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IOverrideRepository;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.IQuery;
import org.jboss.tools.hibernate.runtime.spi.IQueryExporter;
import org.jboss.tools.hibernate.runtime.spi.IQueryTranslator;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;
import org.jboss.tools.hibernate.runtime.spi.ISession;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;

public class FacadeFactoryImpl  extends AbstractFacadeFactory {
	
	@Override
	public IArtifactCollector createArtifactCollector(Object target) {
		throw new RuntimeException("Should use class 'NewFacadeFactory'");
	}
	
	@Override
	public ICfg2HbmTool createCfg2HbmTool(Object target) {
		throw new RuntimeException("Should use class 'NewFacadeFactory'");
	}
	
	@Override
	public INamingStrategy createNamingStrategy(Object target) {
		throw new RuntimeException("Should use class 'NewFacadeFactory'");
	}
	
	@Override
	public IOverrideRepository createOverrideRepository(Object target) {
		throw new RuntimeException("Should use class 'NewFacadeFactory'");
	}
	
	@Override
	public IReverseEngineeringStrategy createReverseEngineeringStrategy(Object target) {
		throw new RuntimeException("Should use class 'NewFacadeFactory'");
	}

	@Override
	public IReverseEngineeringSettings createReverseEngineeringSettings(Object target) {
		throw new RuntimeException("Should use class 'NewFacadeFactory'");
	}

	@Override
	public IConfiguration createConfiguration(Object target) {
		throw new RuntimeException("Should use class 'NewFacadeFactory'");
	}

	@Override
	public IColumn createColumn(Object target) {
		throw new RuntimeException("Should use class 'NewFacadeFactory'");
	}

	@Override
	public ISessionFactory createSessionFactory(Object target) {
		throw new RuntimeException("Should use class 'NewFacadeFactory'");
	}
	
	@Override
	public IClassMetadata createClassMetadata(Object target) {
		throw new RuntimeException("Should use class 'NewFacadeFactory'");
	}
	
	@Override
	public ClassLoader getClassLoader() {
		return FacadeFactoryImpl.class.getClassLoader();
	}

	@Override
	public ISchemaExport createSchemaExport(Object target) {
		return new SchemaExportFacadeImpl(this, target);
	}
	
	@Override
	public IGenericExporter createGenericExporter(Object target) {
		return new GenericExporterFacadeImpl(this, target);
	}

	@Override
	public IHbm2DDLExporter createHbm2DDLExporter(Object target) {
		return new Hbm2DDLExporterFacadeImpl(this, target);
	}

	@Override
	public IQueryExporter createQueryExporter(Object target) {
		return new QueryExporterFacadeImpl(this, target);
	}

	@Override
	public IExporter createExporter(Object target) {
		return new ExporterFacadeImpl(this, target);
	}
	
	@Override
	public ICriteria createCriteria(Object target) {
		return new CriteriaFacadeImpl(this, target);
	}

	@Override
	public IEnvironment createEnvironment() {
		return new EnvironmentFacadeImpl(this);
	}

	@Override
	public IForeignKey createForeignKey(Object target) {
		return new ForeignKeyFacadeImpl(this, target);
	}

	@Override
	public IHibernateMappingExporter createHibernateMappingExporter(Object target) {
		return new HibernateMappingExporterFacadeImpl(this, target);
	}

	@Override
	public IHQLQueryPlan createHQLQueryPlan(Object target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPersistentClass createPersistentClass(Object target) {
		return new PersistentClassFacadeImpl(this, target);
	}
	
	@Override
	public IQuery createQuery(Object target) {
		return new QueryFacadeImpl(this, target);
	}
	
	@Override
	public IQueryTranslator createQueryTranslator(Object target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISession createSession(Object target) {
		return new SessionFacadeImpl(this, target);
	}

	@Override
	public IPersistentClass createSpecialRootClass(IProperty property) {
		return new SpecialRootClassFacadeImpl(this, property);
	}
	
	@Override
	public ITypeFactory createTypeFactory() {
		return new TypeFactoryFacadeImpl(this, null);
	}

	@Override
	public IType createType(Object target) {
		return new TypeFacadeImpl(this, target);
	}

}