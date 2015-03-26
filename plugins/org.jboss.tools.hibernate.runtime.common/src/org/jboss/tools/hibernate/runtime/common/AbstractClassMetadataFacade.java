package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public abstract class AbstractClassMetadataFacade 
extends AbstractFacade 
implements IClassMetadata {

	public AbstractClassMetadataFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

}
