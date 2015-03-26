package org.jboss.tools.hibernate.proxy;

import org.hibernate.metadata.CollectionMetadata;
import org.jboss.tools.hibernate.runtime.common.AbstractCollectionMetadataFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IType;

public class CollectionMetadataProxy extends AbstractCollectionMetadataFacade {
	
	private CollectionMetadata target = null;
	private IType elementType = null;

	public CollectionMetadataProxy(
			IFacadeFactory facadeFactory,
			CollectionMetadata value) {
		super(facadeFactory, value);
		target = value;
	}

	@Override
	public IType getElementType() {
		if (elementType == null) {
			elementType = new TypeProxy(target.getElementType());
		}
		return elementType;
	}

}
