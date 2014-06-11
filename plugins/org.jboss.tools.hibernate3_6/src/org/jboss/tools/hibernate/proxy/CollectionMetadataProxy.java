package org.jboss.tools.hibernate.proxy;

import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.spi.ICollectionMetadata;

public class CollectionMetadataProxy implements ICollectionMetadata {
	
	private CollectionMetadata target = null;

	public CollectionMetadataProxy(CollectionMetadata value) {
		target = value;
	}

	@Override
	public Type getElementType() {
		return target.getElementType();
	}

}
