package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import org.jboss.tools.hibernate.runtime.common.AbstractClassMetadataFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IEntityMetamodel;

public class ClassMetadataFacadeImpl extends AbstractClassMetadataFacade {

	public ClassMetadataFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}
	
	public IEntityMetamodel getEntityMetamodel() {
		return new EntityMetamodelFacadeImpl(getFacadeFactory(), getTarget());
	}

}
