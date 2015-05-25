package org.jboss.tools.hibernate.runtime.v_4_0.internal;

import org.hibernate.Session;
import org.jboss.tools.hibernate.runtime.common.AbstractSessionFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class SessionFactoryImpl extends AbstractSessionFacade {
	
	public SessionFactoryImpl(
			IFacadeFactory facadeFactory,
			Session session) {
		super(facadeFactory, session);
	}

}
