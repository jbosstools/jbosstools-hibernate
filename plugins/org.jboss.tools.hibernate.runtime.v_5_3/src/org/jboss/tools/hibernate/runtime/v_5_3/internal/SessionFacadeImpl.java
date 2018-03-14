package org.jboss.tools.hibernate.runtime.v_5_3.internal;

import org.jboss.tools.hibernate.runtime.common.AbstractSessionFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;

public class SessionFacadeImpl extends AbstractSessionFacade {

	public SessionFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}
	
	@Override
	public boolean contains(Object object) {
		boolean result = false;
		try {
			result = super.contains(object);
		} catch (IllegalArgumentException e) {
			if (!e.getMessage().startsWith("Not an entity [")) {
				throw e;
			}
		}
		return result;
	}

}
