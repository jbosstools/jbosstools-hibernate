package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;


public abstract class AbstractFacade implements IFacade {

	private Object target = null;	
	private IFacadeFactory facadeFactory;
	
	protected static Object createTarget(String name, IFacadeFactory facadeFactory) {
		return Util.getInstance(name, facadeFactory.getClassLoader());
	}
	
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
