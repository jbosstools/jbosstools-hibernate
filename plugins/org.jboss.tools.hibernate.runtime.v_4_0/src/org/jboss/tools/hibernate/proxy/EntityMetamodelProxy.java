package org.jboss.tools.hibernate.proxy;

import org.hibernate.tuple.entity.EntityMetamodel;
import org.jboss.tools.hibernate.runtime.common.AbstractEntityMetamodelFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class EntityMetamodelProxy extends AbstractEntityMetamodelFacade {
	
	private EntityMetamodel target = null;

	public EntityMetamodelProxy(
			IFacadeFactory facadeFactory, 
			EntityMetamodel emm) {
		super(facadeFactory, emm);
		target = emm;
	}

	@Override
	public Integer getPropertyIndexOrNull(String id) {
		return target.getPropertyIndexOrNull(id);
	}

	@Override
	public Object getTuplizerPropertyValue(Object entity, int i) {
		return target.getTuplizer().getPropertyValue(entity, i);
	}

}
