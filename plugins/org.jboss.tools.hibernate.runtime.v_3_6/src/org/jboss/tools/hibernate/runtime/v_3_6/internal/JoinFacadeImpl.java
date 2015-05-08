package org.jboss.tools.hibernate.runtime.v_3_6.internal;

import org.hibernate.mapping.Join;
import org.jboss.tools.hibernate.runtime.common.AbstractJoinFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class JoinFacadeImpl extends AbstractJoinFacade {
	
	public JoinFacadeImpl(
			IFacadeFactory facadeFactory,
			Join join) {
		super(facadeFactory, join);
	}

}
