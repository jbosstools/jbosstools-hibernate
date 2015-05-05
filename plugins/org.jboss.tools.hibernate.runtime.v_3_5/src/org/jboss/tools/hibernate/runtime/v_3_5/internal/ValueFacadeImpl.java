package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

import org.hibernate.FetchMode;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.IdentifierBag;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.List;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PrimitiveArray;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Set;
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
	
	private IValue index = null;
	private IPersistentClass owner = null;
	private HashSet<IProperty> properties = null;
	
	public ValueFacadeImpl(IFacadeFactory facadeFactory, Value value) {
		super(facadeFactory, value);
	}

	public Value getTarget() {
		return (Value)super.getTarget();
	}

	@Override
	public boolean isSet() {
		return getTarget() instanceof Set;
	}

	@Override
	public IValue getIndex() {
		if (index == null && isList()) {
			List list = (List)getTarget();
			if (list.getIndex() != null) {
				index = getFacadeFactory().createValue(list.getIndex());
			}
		}
		return index;
	}

	@Override
	public boolean isArray() {
		return getTarget() instanceof Array;
	}

	@Override
	public String getElementClassName() {
		String result = null;
		if (isArray()) {
			result = ((Array)getTarget()).getElementClassName();
		}
		return result;
	}

	@Override
	public boolean isPrimitiveArray() {
		return getTarget() instanceof PrimitiveArray;
	}

	@Override
	public String getTypeName() {
		String result = null;
		if (isSimpleValue())  {
			result = ((SimpleValue)getTarget()).getTypeName();
		}
		return result;
	}

	@Override
	public boolean isIdentifierBag() {
		return getTarget() instanceof IdentifierBag;
	}

	@Override
	public boolean isBag() {
		return getTarget() instanceof Bag;
	}

	@Override
	public String getReferencedEntityName() {
		String result = null;
		if (getTarget() instanceof OneToMany) {
			result = ((OneToMany)getTarget()).getReferencedEntityName();
		} else if (getTarget() instanceof ToOne) {
			result = ((ToOne)getTarget()).getReferencedEntityName();
		}
		return result;
	}

	@Override
	public String getEntityName() {
		String result = null;
		if (getTarget() instanceof OneToOne) {
			result = ((OneToOne)getTarget()).getEntityName();
		}
		return result;
	}

	@Override
	public Iterator<IProperty> getPropertyIterator() {
		if (properties == null) {
			initializeProperties();
		}
		return properties.iterator();
	}
	
	@SuppressWarnings("unchecked")
	private void initializeProperties() {
		properties = new HashSet<IProperty>();
		Iterator<Property> origin = ((Component)getTarget()).getPropertyIterator();
		while (origin.hasNext()) {
			properties.add(getFacadeFactory().createProperty(origin.next()));
		}
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
