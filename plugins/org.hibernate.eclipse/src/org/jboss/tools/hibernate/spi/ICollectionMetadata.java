package org.jboss.tools.hibernate.spi;

import org.hibernate.type.Type;

public interface ICollectionMetadata {

	Type getElementType();

}
