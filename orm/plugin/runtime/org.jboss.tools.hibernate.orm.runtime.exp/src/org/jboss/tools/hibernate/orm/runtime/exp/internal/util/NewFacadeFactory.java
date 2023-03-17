package org.jboss.tools.hibernate.orm.runtime.exp.internal.util;

import java.util.Map;
import java.util.Properties;

import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.IDatabaseReader;
import org.jboss.tools.hibernate.runtime.common.AbstractFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IHQLCompletionProposal;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IOverrideRepository;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.IValue;

public class NewFacadeFactory extends AbstractFacadeFactory {
	
	public static NewFacadeFactory INSTANCE = new NewFacadeFactory();

	private WrapperFactory wrapperFactory = new WrapperFactory();
	
	private NewFacadeFactory() {}
	
	@Override
	public IArtifactCollector createArtifactCollector(Object target) {
		throw new RuntimeException("Use 'NewFacadeFactory#createArtifactCollector()");
	}
	
	public IArtifactCollector createArtifactCollector() {
		return (IArtifactCollector)GenericFacadeFactory.createFacade(
				IArtifactCollector.class, 
				wrapperFactory.createArtifactCollectorWrapper());
	}

	@Override
	public ICfg2HbmTool createCfg2HbmTool(Object target) {
		throw new RuntimeException("Use 'NewFacadeFactory#createCfg2HbmTool()");
	}
	
	public ICfg2HbmTool createCfg2HbmTool() {
		return (ICfg2HbmTool)GenericFacadeFactory.createFacade(
				ICfg2HbmTool.class,
				wrapperFactory.createCfg2HbmWrapper());
	}
	
	@Override
	public INamingStrategy createNamingStrategy(Object target) {
		throw new RuntimeException("Use 'NewFacadeFactory#createNamingStrategy(String)");
	}
	
	public INamingStrategy createNamingStrategy(String namingStrategyClassName) {
		return (INamingStrategy)GenericFacadeFactory.createFacade(
				INamingStrategy.class, 
				wrapperFactory.createNamingStrategyWrapper(namingStrategyClassName));
	}
	
	@Override
	public IOverrideRepository createOverrideRepository(Object target) {
		throw new RuntimeException("Use 'NewFacadeFactory#createOverrideRepository()");		
	}
	
	public IOverrideRepository createOverrideRepository() {
		return (IOverrideRepository)GenericFacadeFactory.createFacade(
				IOverrideRepository.class, 
				wrapperFactory.createOverrideRepositoryWrapper());
	}

	@Override 
	public IReverseEngineeringStrategy createReverseEngineeringStrategy(Object target) {
		throw new RuntimeException("use 'NewFacadeFactory#createReverseEngineeringStrategy(String)");
	}
	
	public IReverseEngineeringStrategy createReverseEngineeringStrategy(Object...objects) {
		return (IReverseEngineeringStrategy)GenericFacadeFactory.createFacade(
				IReverseEngineeringStrategy.class, 
				wrapperFactory.createRevengStrategyWrapper(objects));				
	}
	
	public IReverseEngineeringSettings createReverseEngineeringSettings(Object revengStrategy) {
		return (IReverseEngineeringSettings)GenericFacadeFactory.createFacade(
				IReverseEngineeringSettings.class, 
				wrapperFactory.createRevengSettingsWrapper(revengStrategy));
				
	}
	
	public IColumn createColumn(String name) {
		return (IColumn)GenericFacadeFactory.createFacade(
				IColumn.class, 
				wrapperFactory.createColumnWrapper(name));
	}

	public IConfiguration createNativeConfiguration() {
		return (IConfiguration)GenericFacadeFactory.createFacade(
				IConfiguration.class, 
				wrapperFactory.createNativeConfigurationWrapper());
	}
	
	public IConfiguration createRevengConfiguration() {
		return (IConfiguration)GenericFacadeFactory.createFacade(
				IConfiguration.class, 
				wrapperFactory.createRevengConfigurationWrapper());
	}
 	
	public IConfiguration createJpaConfiguration(String persistenceUnit, Map<?,?> properties) {
		return (IConfiguration)GenericFacadeFactory.createFacade(
				IConfiguration.class, 
				wrapperFactory.createJpaConfigurationWrapper(persistenceUnit, properties));
	}
	
	public IPersistentClass createRootClass() {
		return (IPersistentClass)GenericFacadeFactory.createFacade(
				IPersistentClass.class, 
				wrapperFactory.createRootClassWrapper());
	}	
	
	public IPersistentClass createSingleTableSubclass(IPersistentClass persistentClass) {
		return (IPersistentClass)GenericFacadeFactory.createFacade(
				IPersistentClass.class, 
				wrapperFactory.createSingleTableSubClassWrapper(((IFacade)persistentClass).getTarget()));
	}

	public IPersistentClass createJoinedTableSubclass(IPersistentClass persistentClass) {
		return (IPersistentClass)GenericFacadeFactory.createFacade(
				IPersistentClass.class, 
				wrapperFactory.createJoinedTableSubClassWrapper(((IFacade)persistentClass).getTarget()));
	}
	
	@Override
	public IPersistentClass createSpecialRootClass(IProperty property) {
		return (IPersistentClass)GenericFacadeFactory.createFacade(
				IPersistentClass.class, 
				wrapperFactory.createSpecialRootClassWrapper(((IFacade)property).getTarget()));
	}

	public IProperty createProperty() {
		return (IProperty)GenericFacadeFactory.createFacade(
				IProperty.class, 
				wrapperFactory.createPropertyWrapper());
	}
	
	@Override 
	public IHQLCompletionProposal createHQLCompletionProposal(Object target) {
		return (IHQLCompletionProposal)GenericFacadeFactory.createFacade(
				IHQLCompletionProposal.class, 
				wrapperFactory.createHqlCompletionProposalWrapper(target));
	}
	
	public IValue createArray(IPersistentClass persistentClass) {
		return (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				wrapperFactory.createArrayWrapper(((IFacade)persistentClass).getTarget()));
	}

	public IValue createBag(IPersistentClass persistentClass) {
		return (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				wrapperFactory.createBagWrapper(((IFacade)persistentClass).getTarget()));
	}
	
	public IValue createList(IPersistentClass persistentClass) {
		return (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				wrapperFactory.createListWrapper(((IFacade)persistentClass).getTarget()));
	}

	public IValue createManyToOne(ITable tableFacade) {
		return (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				wrapperFactory.createManyToOneWrapper(((IFacade)tableFacade).getTarget()));
	}

	public IValue createMap(IPersistentClass rootClassFacade) {
		return (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				wrapperFactory.createMapWrapper(((IFacade)rootClassFacade).getTarget()));
	}

	public IDatabaseReader createDatabaseReader(
			Properties properties, 
			IReverseEngineeringStrategy strategy) {
		return (IDatabaseReader)GenericFacadeFactory.createFacade(
				IDatabaseReader.class, 
				wrapperFactory.createDatabaseReaderWrapper(
						properties,
						((IFacade)strategy).getTarget()));
	}
	
	public ITable createTable(String name) {
		return (ITable)GenericFacadeFactory.createFacade(
				ITable.class, 
				wrapperFactory.createTableWrapper(name));
	}

	@Override
	public ClassLoader getClassLoader() {
		return INSTANCE.getClass().getClassLoader();
	}

}
