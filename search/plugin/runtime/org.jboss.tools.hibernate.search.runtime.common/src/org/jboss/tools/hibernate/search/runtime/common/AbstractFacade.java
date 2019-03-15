package org.jboss.tools.hibernate.search.runtime.common;

import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.search.runtime.common.IFacadeFactory;

public class AbstractFacade implements IFacade {

	private Object target = null;	
	private IFacadeFactory facadeFactory;
	
	public AbstractFacade(IFacadeFactory facadeFactory, Object target) {
		this.facadeFactory = facadeFactory;
		this.target = target;
	}
	
	protected IFacadeFactory getFacadeFactory() {
		return facadeFactory;
	}
	
	@Override
	public Object getTarget() {
		return target;
	}
	
	protected ClassLoader getFacadeFactoryClassLoader() {
		return facadeFactory.getClassLoader();
	}

}
