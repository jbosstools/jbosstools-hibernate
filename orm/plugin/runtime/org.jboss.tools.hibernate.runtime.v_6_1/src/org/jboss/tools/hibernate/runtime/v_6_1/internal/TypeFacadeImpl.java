package org.jboss.tools.hibernate.runtime.v_6_1.internal;

import org.hibernate.type.BasicType;
import org.hibernate.type.EntityType;
import org.jboss.tools.hibernate.runtime.common.AbstractTypeFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.v_6_1.internal.legacy.IntegerType;

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

	@Override
	public String toString(Object value) {
		String result = null;
		Object target = getTarget();
		if (target instanceof BasicType) {
			return ((BasicType)target).getJavaTypeDescriptor().toString(value);
		}
		return result;
	}
	
	@Override
	protected String getIntegerTypeClassName() {
		return IntegerType.class.getName();
	}
	
}
