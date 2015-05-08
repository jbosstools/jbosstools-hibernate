package org.jboss.tools.hibernate.runtime.common;

import java.util.HashSet;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IJoin;
import org.jboss.tools.hibernate.runtime.spi.IProperty;

public abstract class AbstractJoinFacade 
extends AbstractFacade 
implements IJoin {

	protected HashSet<IProperty> properties = null;

	public AbstractJoinFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

}
