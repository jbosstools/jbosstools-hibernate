package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IOverrideRepository;

public abstract class AbstractOverrideRepositoryFacade 
extends AbstractFacade 
implements IOverrideRepository {

	public AbstractOverrideRepositoryFacade(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
		// TODO Auto-generated constructor stub
	}

}
