package org.jboss.tools.hibernate.proxy;

import org.hibernate.metadata.CollectionMetadata;
import org.jboss.tools.hibernate.runtime.common.AbstractCollectionMetadataFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IType;

public class CollectionMetadataProxy extends AbstractCollectionMetadataFacade {
	
	private IType elementType = null;

	public CollectionMetadataProxy(
			IFacadeFactory facadeFactory,
			CollectionMetadata value) {
		super(facadeFactory, value);
	}

	@Override
	public IType getElementType() {
		if (elementType == null) {
			elementType = new TypeProxy(getFacadeFactory(), ((CollectionMetadata)getTarget()).getElementType());
		}
		return elementType;
	}

}
