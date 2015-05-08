package org.jboss.tools.hibernate.runtime.v_3_6.internal;

import java.util.Iterator;

import org.hibernate.mapping.Join;
import org.jboss.tools.hibernate.runtime.common.AbstractJoinFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IProperty;

public class JoinFacadeImpl extends AbstractJoinFacade {
	
	public JoinFacadeImpl(
			IFacadeFactory facadeFactory,
			Join join) {
		super(facadeFactory, join);
	}

	@Override
	public Iterator<IProperty> getPropertyIterator() {
		if (properties == null) {
			initializeProperties();
		}
		return properties.iterator();
	}
	
}
