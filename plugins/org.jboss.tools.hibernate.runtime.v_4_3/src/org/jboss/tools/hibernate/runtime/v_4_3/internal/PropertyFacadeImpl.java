package org.jboss.tools.hibernate.runtime.v_4_3.internal;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Value;
import org.jboss.tools.hibernate.runtime.common.AbstractPropertyFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.jboss.tools.hibernate.runtime.spi.IValue;

public class PropertyFacadeImpl extends AbstractPropertyFacade {
	
	private IType type = null;
	private IPersistentClass persistentClass = null;
		
	public PropertyFacadeImpl(
			IFacadeFactory facadeFactory,
			Property property) {
		super(facadeFactory, property);
	}
	
	public Property getTarget() {
		return (Property)super.getTarget();
	}

	@Override
	public IValue getValue() {
		if (value == null && getTarget().getValue() != null) {
			value = getFacadeFactory().createValue(getTarget().getValue());
		}
		return value;
	}

	@Override
	public void setName(String name) {
		getTarget().setName(name);
	}

	@Override
	public void setPersistentClass(IPersistentClass persistentClass) {
		assert persistentClass instanceof IFacade;
		getTarget().setPersistentClass((PersistentClass)((IFacade)persistentClass).getTarget());
	}

	@Override
	public IPersistentClass getPersistentClass() {
		if (persistentClass == null && getTarget().getPersistentClass() != null) {
			persistentClass = getFacadeFactory().createPersistentClass(getTarget().getPersistentClass());
		}
		return persistentClass;
	}

	@Override
	public boolean isComposite() {
		return getTarget().isComposite();
	}

	@Override
	public String getPropertyAccessorName() {
		return getTarget().getPropertyAccessorName();
	}

	@Override
	public String getName() {
		return getTarget().getName();
	}

	@Override
	public boolean classIsPropertyClass() {
		return getTarget().getClass() == Property.class;
	}

	@Override
	public String getNodeName() {
		return getTarget().getNodeName();
	}

	@Override
	public IType getType() {
		if (type == null && getTarget().getType() != null) {
			type = getFacadeFactory().createType(getTarget().getType());
		}
		return type;
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
