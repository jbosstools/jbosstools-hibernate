package org.jboss.tools.hibernate.runtime.v_3_6.internal;

import org.hibernate.Session;
import org.jboss.tools.hibernate.runtime.common.AbstractSessionFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class SessionFacadeImpl extends AbstractSessionFacade {
	
	public SessionFacadeImpl(
			IFacadeFactory facadeFactory,
			Session session) {
		super(facadeFactory, session);
	}


	public Session getTarget() {
		return (Session)super.getTarget();
	}

}
