package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.ICollectionMetadata;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.ICriteria;
import org.jboss.tools.hibernate.runtime.spi.IDatabaseCollector;
import org.jboss.tools.hibernate.runtime.spi.IEntityMetamodel;
import org.jboss.tools.hibernate.runtime.spi.IEnvironment;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IForeignKey;
import org.jboss.tools.hibernate.runtime.spi.IGenericExporter;
import org.jboss.tools.hibernate.runtime.spi.IHQLCodeAssist;
import org.jboss.tools.hibernate.runtime.spi.IHQLCompletionProposal;
import org.jboss.tools.hibernate.runtime.spi.IHQLQueryPlan;
import org.jboss.tools.hibernate.runtime.spi.IHbm2DDLExporter;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.IJDBCReader;
import org.jboss.tools.hibernate.runtime.spi.IJoin;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IOverrideRepository;
import org.jboss.tools.hibernate.runtime.spi.IPOJOClass;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IPrimaryKey;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.IQuery;
import org.jboss.tools.hibernate.runtime.spi.IQueryExporter;
import org.jboss.tools.hibernate.runtime.spi.IQueryTranslator;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;
import org.jboss.tools.hibernate.runtime.spi.ISession;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.ITableFilter;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;
import org.jboss.tools.hibernate.runtime.spi.IValue;

public abstract class AbstractFacadeFactory implements IFacadeFactory {

	public IArtifactCollector createArtifactCollector(Object target) {
		return new AbstractArtifactCollectorFacade(this,target) {};
	}
	
	public ICfg2HbmTool createCfg2HbmTool(Object target) {
		return new AbstractCfg2HbmToolFacade(this, target) {};
	}
	
	public INamingStrategy createNamingStrategy(Object target) {
		return new AbstractNamingStrategyFacade(this, target) {};
	}
	
	public IReverseEngineeringSettings createReverseEngineeringSettings(Object target) {
		return new AbstractReverseEngineeringSettingsFacade(this, target) {};
	}
	
	public IReverseEngineeringStrategy createReverseEngineeringStrategy(Object target) {
		return new AbstractReverseEngineeringStrategyFacade(this, target) {};
	}
	
	public IOverrideRepository createOverrideRepository(Object target) {
		return new AbstractOverrideRepositoryFacade(this, target) {};
	}
	
	public ISchemaExport createSchemaExport(Object target) {
		return new AbstractSchemaExportFacade(this, target) {};
	}
	
	public IGenericExporter createGenericExporter(Object target) {
		return new AbstractGenericExporterFacade(this, target) {};
	}
	
	public IHbm2DDLExporter createHbm2DDLExporter(Object target) {
		return new AbstractHbm2DDLExporterFacade(this, target) {};
	}
	
	public IQueryExporter createQueryExporter(Object target) {
		return new AbstractQueryExporterFacade(this, target) {};
	}
	
	public ITableFilter createTableFilter(Object target) {
		return new AbstractTableFilterFacade(this, target) {};
	}
	
	public IExporter createExporter(Object target) {
		return new AbstractExporterFacade(this, target) {};
	}
	
	@Override
	public IClassMetadata createClassMetadata(Object target) {
		return new AbstractClassMetadataFacade(this, target) {};
	}
	
	@Override
	public ICollectionMetadata createCollectionMetadata(Object target) {
		return new AbstractCollectionMetadataFacade(this, target) {};
	}

	@Override
	public IColumn createColumn(Object target) {
		return new AbstractColumnFacade(this, target) {};
	}
	
	@Override
	public IConfiguration createConfiguration(Object target) {
		return new AbstractConfigurationFacade(this, target) {};
	}

	@Override
	public ICriteria createCriteria(Object target) {
		return new AbstractCriteriaFacade(this, target) {};
	}

	@Override
	public IDatabaseCollector createDatabaseCollector(Object target) {
		return new AbstractDatabaseCollectorFacade(this, target) {};
	}

	@Override
	public IEntityMetamodel createEntityMetamodel(Object target) {
		return new AbstractEntityMetamodelFacade(this, target) {};
	}
	
	@Override
	public IEnvironment createEnvironment() {
		return new AbstractEnvironmentFacade(this, null) {};
	}

	@Override
	public IForeignKey createForeignKey(Object target) {
		return new AbstractForeignKeyFacade(this, target) {};
	}
	
	@Override
	public IValue createValue(Object target) {
		return new AbstractValueFacade(this, target) {};
	}
	
	@Override
	public IJoin createJoin(Object target) {
		return new AbstractJoinFacade(this, target) {};
	}
	
	@Override
	public IType createType(Object target) {
		return new AbstractTypeFacade(this, target) {};
	}
	
	@Override
	public IQuery createQuery(Object target) {
		return new AbstractQueryFacade(this, target) {};
	}
	
	@Override
	public IPOJOClass createPOJOClass(Object target) {
		return new AbstractPOJOClassFacade(this, target) {};
	}

	@Override
	public IQueryTranslator createQueryTranslator(Object target) {
		return new AbstractQueryTranslatorFacade(this, target) {};
	}

	@Override
	public IPersistentClass createPersistentClass(Object target) {
		return new AbstractPersistentClassFacade(this, target) {};
	}

	@Override
	public IPrimaryKey createPrimaryKey(Object target) {
		return new AbstractPrimaryKeyFacade(this, target) {};
	}

	@Override
	public IProperty createProperty(Object target) {
		return new AbstractPropertyFacade(this, target) {};
	}

	@Override
	public ITypeFactory createTypeFactory() {
		return new AbstractTypeFactoryFacade(this, null) {};
	}
	
	@Override
	public ITable createTable(Object target) {
		return new AbstractTableFacade(this, target) {};
	}

	@Override
	public IHQLQueryPlan createHQLQueryPlan(Object target) {
		return new AbstractHQLQueryPlanFacade(this, target) {};
	}

	@Override
	public IHQLCompletionProposal createHQLCompletionProposal(Object target) {
		return new AbstractHQLCompletionProposalFacade(this, target) {};
	}

	@Override
	public IJDBCReader createJDBCReader(Object target) {
		return new AbstractJDBCReaderFacade(this, target) {};
	}

	@Override
	public ISessionFactory createSessionFactory(Object target) {
		return new AbstractSessionFactoryFacade(this, target) {};
	}
	
	@Override
	public ISession createSession(Object target) {
		return new AbstractSessionFacade(this, target) {};
	}
	
	@Override
	public IHibernateMappingExporter createHibernateMappingExporter(Object target) {
		return new AbstractHibernateMappingExporterFacade(this, target) {};
	}

	@Override
	public IHQLCodeAssist createHQLCodeAssist(Object target) {
		return new AbstractHQLCodeAssistFacade(this, target) {};
	}
	
}
