package org.jboss.tools.hibernate.runtime.v_3_6.internal;

import org.hibernate.EntityMode;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.jboss.tools.hibernate.runtime.common.AbstractEntityMetamodelFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class EntityMetamodelFacadeImpl extends AbstractEntityMetamodelFacade {
	
	public EntityMetamodelFacadeImpl(
			IFacadeFactory facadeFactory, 
			EntityMetamodel emm) {
		super(facadeFactory, emm);
	}

	@Override
	public Object getTuplizerPropertyValue(Object entity, int i) {
		return ((EntityMetamodel)getTarget()).getTuplizer(EntityMode.POJO).getPropertyValue(entity, i);
	}

}
