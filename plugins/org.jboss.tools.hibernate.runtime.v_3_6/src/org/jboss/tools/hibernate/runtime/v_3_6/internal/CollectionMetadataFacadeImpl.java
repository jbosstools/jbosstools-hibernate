package org.jboss.tools.hibernate.runtime.v_3_6.internal;

import org.hibernate.metadata.CollectionMetadata;
import org.jboss.tools.hibernate.runtime.common.AbstractCollectionMetadataFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class CollectionMetadataFacadeImpl extends AbstractCollectionMetadataFacade {
	
	public CollectionMetadataFacadeImpl(
			IFacadeFactory facadeFactory,
			CollectionMetadata value) {
		super(facadeFactory, value);
	}

}
