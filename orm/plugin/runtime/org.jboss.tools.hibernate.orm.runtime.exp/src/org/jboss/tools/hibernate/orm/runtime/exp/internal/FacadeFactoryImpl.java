package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.jboss.tools.hibernate.runtime.common.AbstractArtifactCollectorFacade;
import org.jboss.tools.hibernate.runtime.common.AbstractCfg2HbmToolFacade;
import org.jboss.tools.hibernate.runtime.common.AbstractFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.ICriteria;
import org.jboss.tools.hibernate.runtime.spi.IEntityMetamodel;
import org.jboss.tools.hibernate.runtime.spi.IEnvironment;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IForeignKey;
import org.jboss.tools.hibernate.runtime.spi.IGenericExporter;
import org.jboss.tools.hibernate.runtime.spi.IHQLQueryPlan;
import org.jboss.tools.hibernate.runtime.spi.IHbm2DDLExporter;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.IOverrideRepository;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.IQuery;
import org.jboss.tools.hibernate.runtime.spi.IQueryExporter;
import org.jboss.tools.hibernate.runtime.spi.IQueryTranslator;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;
import org.jboss.tools.hibernate.runtime.spi.ISession;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;

public class FacadeFactoryImpl  extends AbstractFacadeFactory {
	
	private WrapperFactory wrapperFactory = new WrapperFactory();
	
	@Override
	public IArtifactCollector createArtifactCollector(Object target) {
		return new AbstractArtifactCollectorFacade(this, wrapperFactory.createArtifactCollectorWrapper()) {};
	}
	
	@Override
	public ICfg2HbmTool createCfg2HbmTool(Object target) {
		return new AbstractCfg2HbmToolFacade(this, wrapperFactory.createCfg2HbmWrapper()) {};
	}
	
	@Override
	public ClassLoader getClassLoader() {
		return FacadeFactoryImpl.class.getClassLoader();
	}

	@Override
	public IReverseEngineeringStrategy createReverseEngineeringStrategy(Object target) {
		return new ReverseEngineeringStrategyFacadeImpl(this, target);
	}

	public IOverrideRepository createOverrideRepository(Object target) {
		return new OverrideRepositoryFacadeImpl(this, target);
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
		assert target instanceof EntityPersister;
		return new EntityMetamodelFacadeImpl(this, target);
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
	
	@Override
	public ITypeFactory createTypeFactory() {
		return new TypeFactoryFacadeImpl(this, null);
	}

	@Override
	public IType createType(Object target) {
		return new TypeFacadeImpl(this, target);
	}

}