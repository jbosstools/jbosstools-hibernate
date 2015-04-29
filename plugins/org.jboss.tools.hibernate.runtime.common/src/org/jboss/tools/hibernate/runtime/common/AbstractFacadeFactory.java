package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.ICollectionMetadata;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.ICriteria;
import org.jboss.tools.hibernate.runtime.spi.IDatabaseCollector;
import org.jboss.tools.hibernate.runtime.spi.IDialect;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IGenericExporter;
import org.jboss.tools.hibernate.runtime.spi.IHbm2DDLExporter;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingGlobalSettings;
import org.jboss.tools.hibernate.runtime.spi.IMapping;
import org.jboss.tools.hibernate.runtime.spi.IMappings;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IOverrideRepository;
import org.jboss.tools.hibernate.runtime.spi.IQueryExporter;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;
import org.jboss.tools.hibernate.runtime.spi.ITableFilter;
import org.jboss.tools.hibernate.runtime.spi.ITableIdentifier;

public abstract class AbstractFacadeFactory implements IFacadeFactory {

	public IArtifactCollector createArtifactCollector() {
		return new AbstractArtifactCollectorFacade(this) {};
	}
	
	public ICfg2HbmTool createCfg2HbmTool() {
		return new AbstractCfg2HbmToolFacade(this) {};
	}
	
	public INamingStrategy createNamingStrategy(Object target) {
		return new AbstractNamingStrategyFacade(this, target) {};
	}
	
	public IDialect createDialect(Object target) {
		return new AbstractDialectFacade(this, target) {};
	}
	
	public IMapping createMapping(Object target ) {
		return new AbstractMappingFacade(this, target) {};
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
	
	public ITableIdentifier createTableIdentifier(Object target) {
		return new AbstractTableIdentifierFacade(this, target) {};
	}
	
	public IHibernateMappingGlobalSettings createHibernateMappingGlobalSettings(Object target) {
		return new AbstractHibernateMappingGlobalSettingsFacade(this, target) {};
	}
	
	public IMappings createMappings(Object target) {
		return new AbstractMappingsFacade(this, target) {};
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

}
