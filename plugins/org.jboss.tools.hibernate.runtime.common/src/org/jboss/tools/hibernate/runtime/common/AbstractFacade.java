package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;


public abstract class AbstractFacade implements IFacade {

	private Object target = null;
	
	protected IFacadeFactory facadeFactory;
	
	public AbstractFacade(IFacadeFactory facadeFactory) {
		this.facadeFactory = facadeFactory;
	}
	
	protected abstract String getTargetClassName();
	
	public Object getTarget() {
		if (target == null) {
			target = Util.getInstance(getTargetClassName(), this);
		}
		return target;
	}

}
