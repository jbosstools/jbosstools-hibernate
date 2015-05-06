package org.jboss.tools.hibernate.runtime.v_3_6.internal;

import java.util.Iterator;
import java.util.Properties;

import org.hibernate.FetchMode;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.Value;
import org.jboss.tools.hibernate.proxy.PersistentClassProxy;
import org.jboss.tools.hibernate.runtime.common.AbstractValueFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.IValue;

public class ValueFacadeImpl extends AbstractValueFacade {
	
	private IPersistentClass owner = null;
	
	public ValueFacadeImpl(IFacadeFactory facadeFactory, Value value) {
		super(facadeFactory, value);
	}

	public Value getTarget() {
		return (Value)super.getTarget();
	}

	@Override
	public Iterator<IProperty> getPropertyIterator() {
		if (properties == null) {
			initializeProperties();
		}
		return properties.iterator();
	}
	
	@Override
	public void addColumn(IColumn column) {
		assert column instanceof IFacade;
		assert getTarget() instanceof SimpleValue;
		((SimpleValue)getTarget()).addColumn((Column)((IFacade)column).getTarget());
	}

	@Override
	public void setTypeParameters(Properties typeParameters) {
		assert getTarget() instanceof SimpleValue;
		((SimpleValue)getTarget()).setTypeParameters(typeParameters);
	}

	@Override
	public String getForeignKeyName() {
		assert getTarget() instanceof SimpleValue;
		return ((SimpleValue)getTarget()).getForeignKeyName();
	}

	@Override
	public IPersistentClass getOwner() {
		assert getTarget() instanceof Component;
		if (owner == null && ((Component)getTarget()).getOwner() != null)
			owner = getFacadeFactory().createPersistentClass(((Component)getTarget()).getOwner());
		return owner;
	}

	@Override
	public IValue getElement() {
		assert getTarget() instanceof Collection;
		IValue result = null;
		if (((Collection)getTarget()).getElement() != null) {
			result = getFacadeFactory().createValue(((Collection)getTarget()).getElement());
		}
		return result;
	}

	@Override
	public String getParentProperty() {
		return ((Component)getTarget()).getParentProperty();
	}

	@Override
	public void setElementClassName(String name) {
		assert getTarget() instanceof Array;
		((Array)getTarget()).setElementClassName(name);
	}

	@Override
	public void setKey(IValue keyValue) {
		assert keyValue instanceof ValueFacadeImpl;
		assert getTarget() instanceof Collection;
		assert ((ValueFacadeImpl)keyValue).getTarget() instanceof KeyValue;
		((Collection)getTarget()).setKey((KeyValue)((ValueFacadeImpl)keyValue).getTarget());
	}

	@Override
	public void setFetchModeJoin() {
		assert (getTarget() instanceof Collection || getTarget() instanceof ToOne);
		if (getTarget() instanceof Collection) {
			((Collection)getTarget()).setFetchMode(FetchMode.JOIN);
		} else if (getTarget() instanceof ToOne) {
			((ToOne)getTarget()).setFetchMode(FetchMode.JOIN);
		}
	}

	@Override
	public boolean isInverse() {
		assert getTarget() instanceof Collection;
		return ((Collection)getTarget()).isInverse();
	}

	@Override
	public IPersistentClass getAssociatedClass() {
		assert getTarget() instanceof OneToMany;
		return ((OneToMany)getTarget()).getAssociatedClass() != null ? 
				getFacadeFactory().createPersistentClass(((OneToMany)getTarget()).getAssociatedClass()) :
					null;
	}

	@Override
	public void setLazy(boolean b) {
		assert getTarget() instanceof Collection;
		((Collection)getTarget()).setLazy(b);
	}

	@Override
	public void setRole(String role) {
		assert getTarget() instanceof Collection;
		((Collection)getTarget()).setRole(role);
	}

	@Override
	public void setReferencedEntityName(String name) {
		assert (getTarget() instanceof ToOne || getTarget() instanceof ManyToOne);
		if (isToOne()) {
			((ToOne)getTarget()).setReferencedEntityName(name);
		} else if (isOneToMany()) {
			((OneToMany)getTarget()).setReferencedEntityName(name);
		}
	}

	@Override
	public void setAssociatedClass(IPersistentClass persistentClass) {
		assert getTarget() instanceof OneToMany;
		assert persistentClass instanceof PersistentClassProxy;
		((OneToMany)getTarget()).setAssociatedClass(((PersistentClassProxy)persistentClass).getTarget());
	}

}
