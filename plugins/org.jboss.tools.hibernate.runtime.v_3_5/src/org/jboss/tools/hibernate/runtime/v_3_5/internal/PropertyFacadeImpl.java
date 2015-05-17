package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import org.hibernate.mapping.Property;
import org.jboss.tools.hibernate.runtime.common.AbstractPropertyFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class PropertyFacadeImpl extends AbstractPropertyFacade {
	
	public PropertyFacadeImpl(
			IFacadeFactory facadeFactory,
			Property property) {
		super(facadeFactory, property);
	}
	
	public Property getTarget() {
		return (Property)super.getTarget();
	}

	@Override
	public boolean isNaturalIdentifier() {
		return getTarget().isNaturalIdentifier();
	}

	@Override
	public boolean isOptimisticLocked() {
		return getTarget().isOptimisticLocked();
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof PropertyFacadeImpl)) return false;
		return getTarget().equals(((PropertyFacadeImpl)o).getTarget());
	}

}
