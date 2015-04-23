package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IType;

public abstract class AbstractClassMetadataFacade 
extends AbstractFacade 
implements IClassMetadata {

	protected IType[] propertyTypes = null;
	protected IType identifierType = null;

	public AbstractClassMetadataFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	public String getEntityName() {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getEntityName", 
				new Class[] {}, 
				new Object[] {});
	}

}
