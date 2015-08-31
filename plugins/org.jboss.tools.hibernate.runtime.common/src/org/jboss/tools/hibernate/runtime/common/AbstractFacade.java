package org.jboss.tools.hibernate.runtime.common;

public abstract class AbstractFacade implements IFacade {

	private Object target = null;	
	private IFacadeFactory facadeFactory;
	
	public AbstractFacade(IFacadeFactory facadeFactory, Object target) {
		this.facadeFactory = facadeFactory;
		this.target = target;
	}
	
	protected IFacadeFactory getFacadeFactory() {
		return facadeFactory;
	}
	
	protected ClassLoader getFacadeFactoryClassLoader() {
		return facadeFactory.getClassLoader();
	}
	
	public Object getTarget() {
		return target;
	}

}
