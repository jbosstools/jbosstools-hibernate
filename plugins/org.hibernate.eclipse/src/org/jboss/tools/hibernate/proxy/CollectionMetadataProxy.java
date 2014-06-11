package org.jboss.tools.hibernate.proxy;

import org.hibernate.metadata.CollectionMetadata;
import org.jboss.tools.hibernate.spi.ICollectionMetadata;
import org.jboss.tools.hibernate.spi.IType;

public class CollectionMetadataProxy implements ICollectionMetadata {
	
	private CollectionMetadata target = null;
	private IType elementType = null;

	public CollectionMetadataProxy(CollectionMetadata value) {
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
