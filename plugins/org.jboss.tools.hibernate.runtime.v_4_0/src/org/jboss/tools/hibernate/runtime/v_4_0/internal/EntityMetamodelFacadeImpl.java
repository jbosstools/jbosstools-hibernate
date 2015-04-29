package org.jboss.tools.hibernate.runtime.v_4_0.internal;

import org.hibernate.tuple.entity.EntityMetamodel;
import org.jboss.tools.hibernate.runtime.common.AbstractEntityMetamodelFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class EntityMetamodelFacadeImpl extends AbstractEntityMetamodelFacade {
	
	public EntityMetamodelFacadeImpl(
			IFacadeFactory facadeFactory, 
			EntityMetamodel emm) {
		super(facadeFactory, emm);
	}

}
