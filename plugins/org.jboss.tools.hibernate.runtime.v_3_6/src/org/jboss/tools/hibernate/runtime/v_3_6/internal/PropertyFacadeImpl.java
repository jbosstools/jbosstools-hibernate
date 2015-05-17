package org.jboss.tools.hibernate.runtime.v_3_6.internal;

import org.hibernate.mapping.Property;
import org.hibernate.mapping.Value;
import org.jboss.tools.hibernate.runtime.common.AbstractPropertyFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IValue;

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
	public void setValue(IValue value) {
		assert value instanceof IFacade;
		getTarget().setValue((Value)((IFacade)value).getTarget());
		this.value = value;
	}

	@Override
	public void setPropertyAccessorName(String string) {
		getTarget().setPropertyAccessorName(string);
	}

	@Override
	public void setCascade(String string) {
		getTarget().setCascade(string);
	}

	@Override
	public boolean isBackRef() {
		return getTarget().isBackRef();
	}

	@Override
	public boolean isSelectable() {
		return getTarget().isSelectable();
	}

	@Override
	public boolean isInsertable() {
		return getTarget().isInsertable();
	}

	@Override
	public boolean isUpdateable() {
		return getTarget().isUpdateable();
	}

	@Override
	public String getCascade() {
		return getTarget().getCascade();
	}

	@Override
	public boolean isLazy() {
		return getTarget().isLazy();
	}

	@Override
	public boolean isOptional() {
		return getTarget().isOptional();
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
