package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.ICollectionMetadata;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IType;

public abstract class AbstractCollectionMetadataFacade 
extends AbstractFacade 
implements ICollectionMetadata {

	private IType elementType = null;

	public AbstractCollectionMetadataFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	public IType getElementType() {
		if (elementType == null) {
			elementType = getFacadeFactory().createType(getTargetElementType());
		}
		return elementType;
	}
	
	private Object getTargetElementType() {
		return Util.invokeMethod(getTarget(), "getElementType", new Class[] {}, new Object[] {});
	}

}
