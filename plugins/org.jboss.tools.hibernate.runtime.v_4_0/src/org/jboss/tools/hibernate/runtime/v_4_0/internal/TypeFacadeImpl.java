package org.jboss.tools.hibernate.runtime.v_4_0.internal;

import org.hibernate.type.CollectionType;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.runtime.common.AbstractTypeFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class TypeFacadeImpl extends AbstractTypeFacade {
	
	public TypeFacadeImpl(
			IFacadeFactory facadeFactory,
			Type type) {
		super(facadeFactory, type);
	}	

	public Type getTarget() {
		return (Type)super.getTarget();
	}

	@Override
	public String getRole() {
		assert getTarget() instanceof CollectionType;
		return ((CollectionType)getTarget()).getRole();
	}

}
