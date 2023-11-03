package org.jboss.tools.hibernate.orm.runtime.v_6_4;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hibernate.tool.internal.export.cfg.CfgExporter;
import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.jboss.tools.hibernate.orm.runtime.common.GenericFacadeFactory;
import org.jboss.tools.hibernate.orm.runtime.common.IDatabaseReader;
import org.jboss.tools.hibernate.orm.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IHQLCodeAssist;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IOverrideRepository;
import org.jboss.tools.hibernate.runtime.spi.IProgressListener;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.ITableFilter;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;

public class ServiceImpl {

	public IConfiguration newDefaultConfiguration() {
		return (IConfiguration)GenericFacadeFactory.createFacade(
				IConfiguration.class, 
				WrapperFactory.createNativeConfigurationWrapper());
	}

	public IConfiguration newAnnotationConfiguration() {
		return newDefaultConfiguration();
	}

	public IConfiguration newJpaConfiguration(
			String entityResolver, 
			String persistenceUnit,
			Map<Object, Object> overrides) {
		return (IConfiguration)GenericFacadeFactory.createFacade(
				IConfiguration.class, 
				WrapperFactory.createJpaConfigurationWrapper(persistenceUnit, overrides));
	}

	public IHibernateMappingExporter newHibernateMappingExporter(
			IConfiguration configuration, File file) {
		return (IHibernateMappingExporter)GenericFacadeFactory.createFacade(
				IHibernateMappingExporter.class, 
				WrapperFactory.createHbmExporterWrapper(((IFacade)configuration).getTarget(), file));
	}

	public ISchemaExport newSchemaExport(IConfiguration configuration) {
		return (ISchemaExport)GenericFacadeFactory.createFacade(
				ISchemaExport.class, 
				WrapperFactory.createSchemaExport(((IFacade)configuration).getTarget()));
	}

	public IHQLCodeAssist newHQLCodeAssist(IConfiguration configuration) {
		IHQLCodeAssist result = null;
		if (configuration instanceof IConfiguration) {
			result = (IHQLCodeAssist)GenericFacadeFactory.createFacade(
					IHQLCodeAssist.class, 
					WrapperFactory.createHqlCodeAssistWrapper(((IFacade)configuration).getTarget()));
		}
		return result;
	}

	public IConfiguration newJDBCMetaDataConfiguration() {
		return (IConfiguration)GenericFacadeFactory.createFacade(
				IConfiguration.class, 
				WrapperFactory.createRevengConfigurationWrapper());
	}

	public IExporter createExporter(String exporterClassName) {
		return (IExporter)GenericFacadeFactory.createFacade(
				IExporter.class, 
				WrapperFactory.createExporterWrapper(exporterClassName));
	}

	public IExporter createCfgExporter() {
		return createExporter(CfgExporter.class.getName());
	}
	
	public IArtifactCollector newArtifactCollector() {
		return (IArtifactCollector)GenericFacadeFactory.createFacade(
				IArtifactCollector.class, 
				WrapperFactory.createArtifactCollectorWrapper());
	}

	public ITypeFactory newTypeFactory() {
		return (ITypeFactory)GenericFacadeFactory.createFacade(
				ITypeFactory.class, 
				WrapperFactory.createTypeFactoryWrapper());
	}

	public INamingStrategy newNamingStrategy(String strategyClassName) {
		return (INamingStrategy)GenericFacadeFactory.createFacade(
				INamingStrategy.class, 
				WrapperFactory.createNamingStrategyWrapper(strategyClassName));
	}

	public IOverrideRepository newOverrideRepository() {
		return (IOverrideRepository)GenericFacadeFactory.createFacade(
				IOverrideRepository.class, 
				WrapperFactory.createOverrideRepositoryWrapper());
	}

	public ITableFilter newTableFilter() {
		return (ITableFilter)GenericFacadeFactory.createFacade(
				ITableFilter.class, 
				WrapperFactory.createTableFilterWrapper());
	}

	public IReverseEngineeringStrategy newDefaultReverseEngineeringStrategy() {
		return (IReverseEngineeringStrategy)GenericFacadeFactory.createFacade(
				IReverseEngineeringStrategy.class, 
				WrapperFactory.createRevengStrategyWrapper());
	}

	public IReverseEngineeringSettings newReverseEngineeringSettings(
			IReverseEngineeringStrategy res) {
		return (IReverseEngineeringSettings)GenericFacadeFactory.createFacade(
				IReverseEngineeringSettings.class, 
				WrapperFactory.createRevengSettingsWrapper(((IFacade)res).getTarget()));
	}

	public Map<String, List<ITable>> collectDatabaseTables(
			Properties properties, 
			IReverseEngineeringStrategy strategy,
			final IProgressListener progressListener) {
		return ((IDatabaseReader)GenericFacadeFactory.createFacade(
				IDatabaseReader.class, 
				WrapperFactory.createDatabaseReaderWrapper(
						properties,
						((IFacade)strategy).getTarget())))
				.collectDatabaseTables();
	}

	public IReverseEngineeringStrategy newReverseEngineeringStrategy(
			String strategyName,
			IReverseEngineeringStrategy delegate) {
		return (IReverseEngineeringStrategy)GenericFacadeFactory.createFacade(
				IReverseEngineeringStrategy.class, 
				WrapperFactory.createRevengStrategyWrapper(strategyName, ((IFacade)delegate).getTarget()));
	}

}
