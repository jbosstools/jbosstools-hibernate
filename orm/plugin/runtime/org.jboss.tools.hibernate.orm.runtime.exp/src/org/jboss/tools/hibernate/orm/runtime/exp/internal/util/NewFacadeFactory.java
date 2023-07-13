package org.jboss.tools.hibernate.orm.runtime.exp.internal.util;

import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.jboss.tools.hibernate.runtime.common.AbstractFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IHQLCodeAssist;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IOverrideRepository;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;

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
