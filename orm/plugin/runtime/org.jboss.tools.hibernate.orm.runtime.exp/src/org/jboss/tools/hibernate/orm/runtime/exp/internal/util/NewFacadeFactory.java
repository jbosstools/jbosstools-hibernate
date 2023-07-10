package org.jboss.tools.hibernate.orm.runtime.exp.internal.util;

import java.io.File;
import java.util.Map;

import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.jboss.tools.hibernate.runtime.common.AbstractFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IEnvironment;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IHQLCodeAssist;
import org.jboss.tools.hibernate.runtime.spi.IHQLCompletionProposal;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IOverrideRepository;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.ITableFilter;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;
import org.jboss.tools.hibernate.runtime.spi.IValue;

public class NewFacadeFactory extends AbstractFacadeFactory {
	
	public static NewFacadeFactory INSTANCE = new NewFacadeFactory();

	private NewFacadeFactory() {}
	
	@Override
	public IArtifactCollector createArtifactCollector(Object target) {
		throw new RuntimeException("Use 'NewFacadeFactory#createArtifactCollector()");
	}
	
	public IArtifactCollector createArtifactCollector() {
		return (IArtifactCollector)GenericFacadeFactory.createFacade(
				IArtifactCollector.class, 
				WrapperFactory.createArtifactCollectorWrapper());
	}

	@Override
	public ICfg2HbmTool createCfg2HbmTool(Object target) {
		throw new RuntimeException("Use 'NewFacadeFactory#createCfg2HbmTool()");
	}
	
	public ICfg2HbmTool createCfg2HbmTool() {
		return (ICfg2HbmTool)GenericFacadeFactory.createFacade(
				ICfg2HbmTool.class,
				WrapperFactory.createCfg2HbmWrapper());
	}
	
	@Override
	public INamingStrategy createNamingStrategy(Object target) {
		throw new RuntimeException("Use 'NewFacadeFactory#createNamingStrategy(String)");
	}
	
	public INamingStrategy createNamingStrategy(String namingStrategyClassName) {
		return (INamingStrategy)GenericFacadeFactory.createFacade(
				INamingStrategy.class, 
				WrapperFactory.createNamingStrategyWrapper(namingStrategyClassName));
	}
	
	@Override
	public IOverrideRepository createOverrideRepository(Object target) {
		throw new RuntimeException("Use 'NewFacadeFactory#createOverrideRepository()");		
	}
	
	public IOverrideRepository createOverrideRepository() {
		return (IOverrideRepository)GenericFacadeFactory.createFacade(
				IOverrideRepository.class, 
				WrapperFactory.createOverrideRepositoryWrapper());
	}

	@Override 
	public IReverseEngineeringStrategy createReverseEngineeringStrategy(Object target) {
		throw new RuntimeException("use 'NewFacadeFactory#createReverseEngineeringStrategy(String)");
	}
	
	public IReverseEngineeringStrategy createReverseEngineeringStrategy(Object...objects) {
		return (IReverseEngineeringStrategy)GenericFacadeFactory.createFacade(
				IReverseEngineeringStrategy.class, 
				WrapperFactory.createRevengStrategyWrapper(objects));				
	}
	
	public IReverseEngineeringSettings createReverseEngineeringSettings(Object revengStrategy) {
		return (IReverseEngineeringSettings)GenericFacadeFactory.createFacade(
				IReverseEngineeringSettings.class, 
				WrapperFactory.createRevengSettingsWrapper(revengStrategy));
				
	}
	
	public IColumn createColumn(String name) {
		return (IColumn)GenericFacadeFactory.createFacade(
				IColumn.class, 
				WrapperFactory.createColumnWrapper(name));
	}

	public IConfiguration createNativeConfiguration() {
		return (IConfiguration)GenericFacadeFactory.createFacade(
				IConfiguration.class, 
				WrapperFactory.createNativeConfigurationWrapper());
	}
	
	public IConfiguration createRevengConfiguration() {
		return (IConfiguration)GenericFacadeFactory.createFacade(
				IConfiguration.class, 
				WrapperFactory.createRevengConfigurationWrapper());
	}
 	
	public IConfiguration createJpaConfiguration(String persistenceUnit, Map<?,?> properties) {
		return (IConfiguration)GenericFacadeFactory.createFacade(
				IConfiguration.class, 
				WrapperFactory.createJpaConfigurationWrapper(persistenceUnit, properties));
	}
	
	public IPersistentClass createRootClass() {
		return (IPersistentClass)GenericFacadeFactory.createFacade(
				IPersistentClass.class, 
				WrapperFactory.createRootClassWrapper());
	}	
	
	public IPersistentClass createSingleTableSubclass(IPersistentClass persistentClass) {
		return (IPersistentClass)GenericFacadeFactory.createFacade(
				IPersistentClass.class, 
				WrapperFactory.createSingleTableSubClassWrapper(((IFacade)persistentClass).getTarget()));
	}

	public IPersistentClass createJoinedTableSubclass(IPersistentClass persistentClass) {
		return (IPersistentClass)GenericFacadeFactory.createFacade(
				IPersistentClass.class, 
				WrapperFactory.createJoinedTableSubClassWrapper(((IFacade)persistentClass).getTarget()));
	}
	
	@Override
	public IPersistentClass createSpecialRootClass(IProperty property) {
		return (IPersistentClass)GenericFacadeFactory.createFacade(
				IPersistentClass.class, 
				WrapperFactory.createSpecialRootClassWrapper(((IFacade)property).getTarget()));
	}

	public IProperty createProperty() {
		return (IProperty)GenericFacadeFactory.createFacade(
				IProperty.class, 
				WrapperFactory.createPropertyWrapper());
	}
	
	@Override 
	public IHQLCompletionProposal createHQLCompletionProposal(Object target) {
		return (IHQLCompletionProposal)GenericFacadeFactory.createFacade(
				IHQLCompletionProposal.class, 
				WrapperFactory.createHqlCompletionProposalWrapper(target));
	}
	
	public IValue createArray(IPersistentClass persistentClass) {
		return (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createArrayWrapper(((IFacade)persistentClass).getTarget()));
	}

	public IValue createBag(IPersistentClass persistentClass) {
		return (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createBagWrapper(((IFacade)persistentClass).getTarget()));
	}
	
	public IValue createList(IPersistentClass persistentClass) {
		return (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createListWrapper(((IFacade)persistentClass).getTarget()));
	}

	public IValue createManyToOne(ITable tableFacade) {
		return (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createManyToOneWrapper(((IFacade)tableFacade).getTarget()));
	}

	public IValue createMap(IPersistentClass persistentClass) {
		return (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createMapWrapper(((IFacade)persistentClass).getTarget()));
	}

	public IValue createOneToMany(IPersistentClass persistentClass) {
		return (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createOneToManyWrapper(((IFacade)persistentClass).getTarget()));
	}

	public IValue createOneToOne(IPersistentClass persistentClass) {
		return (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createOneToOneWrapper(((IFacade)persistentClass).getTarget()));
	}

	public ITable createTable(String name) {
		return (ITable)GenericFacadeFactory.createFacade(
				ITable.class, 
				WrapperFactory.createTableWrapper(name));
	}

	public IValue createPrimitiveArray(IPersistentClass persistentClass) {
		return (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createPrimitiveArrayWrapper(((IFacade)persistentClass).getTarget()));
	}

	public IValue createSet(IPersistentClass persistentClass) {
		return (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createSetWrapper(((IFacade)persistentClass).getTarget()));
	}

	public IValue createSimpleValue() {
		return (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createSimpleValueWrapper());
	}

	public IValue createComponent(IPersistentClass persistentClass) {
		return (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createComponentWrapper(((IFacade)persistentClass).getTarget()));
	}

	public ITableFilter createTableFilter() {
		return (ITableFilter)GenericFacadeFactory.createFacade(
				ITableFilter.class, 
				WrapperFactory.createTableFilterWrapper());
	}
	
	public ITypeFactory createTypeFactory() {
		return (ITypeFactory)GenericFacadeFactory.createFacade(
				ITypeFactory.class, 
				WrapperFactory.createTypeFactoryWrapper());
	}
	
	public IEnvironment createEnvironment() {
		return (IEnvironment)GenericFacadeFactory.createFacade(
				IEnvironment.class, 
				WrapperFactory.createEnvironmentWrapper());
	}
	
	public ISchemaExport createSchemaExport(IConfiguration configuration) {
		return (ISchemaExport)GenericFacadeFactory.createFacade(
				ISchemaExport.class, 
				WrapperFactory.createSchemaExport(((IFacade)configuration).getTarget()));
	}
	
	public IHibernateMappingExporter createHibernateMappingExporter(
			IConfiguration configuration, File file) {
		return (IHibernateMappingExporter)GenericFacadeFactory.createFacade(
				IHibernateMappingExporter.class, 
				WrapperFactory.createHbmExporterWrapper(((IFacade)configuration).getTarget(), file));
	}
	
	public IExporter createExporter(String exporterClassName) {
		return (IExporter)GenericFacadeFactory.createFacade(
				IExporter.class, 
				WrapperFactory.createExporterWrapper(exporterClassName));
	}
	
	public IHQLCodeAssist createHQLCodeAssist(IConfiguration configuration) {
		return (IHQLCodeAssist)GenericFacadeFactory.createFacade(
				IHQLCodeAssist.class, 
				WrapperFactory.createHqlCodeAssistWrapper(((IFacade)configuration).getTarget()));
	}

	@Override
	public ClassLoader getClassLoader() {
		return INSTANCE.getClass().getClassLoader();
	}

}
