package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IForeignKey;

public abstract class AbstractForeignKeyFacade 
extends AbstractFacade 
implements IForeignKey {

	public AbstractForeignKeyFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}	

}
