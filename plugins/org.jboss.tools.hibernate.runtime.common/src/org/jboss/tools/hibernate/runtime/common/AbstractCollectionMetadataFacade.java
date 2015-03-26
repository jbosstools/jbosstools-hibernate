package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.ICollectionMetadata;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public abstract class AbstractCollectionMetadataFacade 
extends AbstractFacade 
implements ICollectionMetadata {

	public AbstractCollectionMetadataFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

}
