package org.jboss.tools.hibernate.runtime.v_3_6.internal;

import org.hibernate.metadata.CollectionMetadata;
import org.jboss.tools.hibernate.runtime.common.AbstractCollectionMetadataFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IType;

public class CollectionMetadataFacadeImpl extends AbstractCollectionMetadataFacade {
	
	private IType elementType = null;

	public CollectionMetadataFacadeImpl(
			IFacadeFactory facadeFactory,
			CollectionMetadata value) {
		super(facadeFactory, value);
	}

	@Override
	public IType getElementType() {
		if (elementType == null) {
			elementType = getFacadeFactory().createType(((CollectionMetadata)getTarget()).getElementType());
		}
		return elementType;
	}

}
