package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IEntityMetamodel;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public abstract class AbstractEntityMetamodelFacade 
extends AbstractFacade 
implements IEntityMetamodel {

	public AbstractEntityMetamodelFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

}
