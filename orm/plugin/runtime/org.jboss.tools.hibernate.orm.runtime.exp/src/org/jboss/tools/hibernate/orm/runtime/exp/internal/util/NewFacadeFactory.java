package org.jboss.tools.hibernate.orm.runtime.exp.internal.util;

import java.io.File;

import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.jboss.tools.hibernate.runtime.common.AbstractFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IEnvironment;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IHQLCodeAssist;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IOverrideRepository;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
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
	
	@Override
	public ICfg2HbmTool createCfg2HbmTool(Object target) {
		throw new RuntimeException("Use 'NewFacadeFactory#createCfg2HbmTool()");
	}
	
	@Override
	public INamingStrategy createNamingStrategy(Object target) {
		throw new RuntimeException("Use 'NewFacadeFactory#createNamingStrategy(String)");
	}
	
	@Override
	public IOverrideRepository createOverrideRepository(Object target) {
		throw new RuntimeException("Use 'NewFacadeFactory#createOverrideRepository()");		
	}
	
	@Override 
	public IReverseEngineeringStrategy createReverseEngineeringStrategy(Object target) {
		throw new RuntimeException("use 'NewFacadeFactory#createReverseEngineeringStrategy(String)");
	}
	
	@Override
	public IPersistentClass createSpecialRootClass(IProperty property) {
		return null;
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
