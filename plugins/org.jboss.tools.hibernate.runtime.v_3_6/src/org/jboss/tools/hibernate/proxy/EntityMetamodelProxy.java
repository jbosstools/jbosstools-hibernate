package org.jboss.tools.hibernate.proxy;

import org.hibernate.EntityMode;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.jboss.tools.hibernate.runtime.common.AbstractEntityMetamodelFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class EntityMetamodelProxy extends AbstractEntityMetamodelFacade {
	
	public EntityMetamodelProxy(
			IFacadeFactory facadeFactory, 
			EntityMetamodel emm) {
		super(facadeFactory, emm);
	}

	public EntityMetamodel getTarget() {
		return (EntityMetamodel)super.getTarget();
	}

	@Override
	public Integer getPropertyIndexOrNull(String id) {
		return getTarget().getPropertyIndexOrNull(id);
	}

	@Override
	public Object getTuplizerPropertyValue(Object entity, int i) {
		return getTarget().getTuplizer(EntityMode.POJO).getPropertyValue(entity, i);
	}

}
