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

	@Override
	public Integer getPropertyIndexOrNull(String id) {
		return (Integer)Util.invokeMethod(
				getTarget(), 
				"getPropertyIndexOrNull", 
				new Class[] { String.class }, 
				new Object[] { id });
	}

}
