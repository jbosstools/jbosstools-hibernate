package org.jboss.tools.hibernate.orm.runtime.exp.internal.util;

import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.jboss.tools.hibernate.runtime.common.AbstractFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IOverrideRepository;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;

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
	
	public IReverseEngineeringStrategy createReverseEngineeringStrategy() {
		return (IReverseEngineeringStrategy)GenericFacadeFactory.createFacade(
				IReverseEngineeringStrategy.class, 
				wrapperFactory.createReverseEngineeringStrategyWrapper());				
	}
	
	public IReverseEngineeringStrategy createReverseEngineeringStrategy(String className) {
		return (IReverseEngineeringStrategy)GenericFacadeFactory.createFacade(
				IReverseEngineeringStrategy.class, 
				wrapperFactory.createReverseEngineeringStrategyWrapper(className));				
	}
	
	public IReverseEngineeringStrategy createReverseEngineeringStrategy(
			String className, 
			IReverseEngineeringStrategy delegate) {
		Object delegateTarget = ((IFacade)delegate).getTarget();
		return (IReverseEngineeringStrategy)GenericFacadeFactory.createFacade(
				IReverseEngineeringStrategy.class, 
				wrapperFactory.createReverseEngineeringStrategyWrapper(className, delegateTarget));				
	}
	
	@Override
	public ClassLoader getClassLoader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPersistentClass createSpecialRootClass(IProperty property) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
