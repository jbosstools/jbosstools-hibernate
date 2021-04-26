package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import org.hibernate.type.EntityType;
import org.jboss.tools.hibernate.runtime.common.AbstractTypeFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;

public class TypeFacadeImpl extends AbstractTypeFacade {

	public TypeFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}
	
	@Override
	public String getReturnedClassName() {
		if (isEntityType()) {
			return ((EntityType)getTarget()).getAssociatedEntityName();
		} else {
			return super.getReturnedClassName();
		}
	}

}
