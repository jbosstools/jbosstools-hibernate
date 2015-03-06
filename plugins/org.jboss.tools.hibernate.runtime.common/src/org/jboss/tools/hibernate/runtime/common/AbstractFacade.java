package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;


public abstract class AbstractFacade implements IFacade {

	private Object target = null;	
	protected IFacadeFactory facadeFactory;
	
	protected static Object createTarget(String name, IFacadeFactory facadeFactory) {
		return Util.getInstance(name, facadeFactory.getClassLoader());
	}
	
	public AbstractFacade(IFacadeFactory facadeFactory, Object target) {
		this.facadeFactory = facadeFactory;
		this.target = target;
	}
	
	protected ClassLoader getClassLoader() {
		return facadeFactory.getClassLoader();
	}
	
	public Object getTarget() {
		return target;
	}

}
