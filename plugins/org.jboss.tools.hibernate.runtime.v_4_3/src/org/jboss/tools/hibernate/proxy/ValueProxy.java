package org.jboss.tools.hibernate.proxy;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

import org.hibernate.FetchMode;
import org.hibernate.mapping.Any;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.IdentifierBag;
import org.hibernate.mapping.IndexedCollection;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.List;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PrimitiveArray;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Set;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.Value;
import org.jboss.tools.hibernate.runtime.common.AbstractValueFacade;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.jboss.tools.hibernate.runtime.spi.IValueVisitor;

public class ValueProxy extends AbstractValueFacade {
	
	private IValue collectionElement = null;
	private ITable collectionTable = null;
	private ITable table = null;
	private IValue key = null;
	private IValue index = null;
	private IType type = null;
	private HashSet<IColumn> columns = null;
	private IPersistentClass owner = null;
	private HashSet<IProperty> properties = null;
	
	public ValueProxy(IFacadeFactory facadeFactory, Value value) {
		super(facadeFactory, value);
	}

	public Value getTarget() {
		return (Value)super.getTarget();
	}

	@Override
	public boolean isSimpleValue() {
		return getTarget().isSimpleValue();
	}

	@Override
	public boolean isCollection() {
		return getTarget() instanceof Collection;
	}

	@Override
	public IValue getCollectionElement() {
		if (isCollection() && collectionElement == null) {
			Value element = ((Collection)getTarget()).getElement();
			if (element != null) {
				collectionElement = getFacadeFactory().createValue(element);
			}
		}
		return collectionElement;
	}

	@Override
	public boolean isOneToMany() {
		return getTarget() instanceof OneToMany;
	}

	@Override
	public boolean isManyToOne() {
		return getTarget() instanceof ManyToOne;
	}

	@Override
	public boolean isOneToOne() {
		return getTarget() instanceof OneToOne;
	}

	@Override
	public boolean isMap() {
		return getTarget() instanceof Map;
	}

	@Override
	public boolean isComponent() {
		return getTarget() instanceof Component;
	}

	@Override
	public Boolean isEmbedded() {
		Boolean result = null;
		if (isComponent()) {
			result = ((Component)getTarget()).isEmbedded();
		} else if (isToOne()) {
			result = ((ToOne)getTarget()).isEmbedded();
		}
		return result;
	}

	@Override
	public boolean isToOne() {
		return getTarget() instanceof ToOne;
	}

	@Override
	public Object accept(IValueVisitor valueVisitor) {
		return valueVisitor.accept(this);
	}

	@Override
	public ITable getTable() {
		if (getTarget().getTable() != null && table == null) {
			table = getFacadeFactory().createTable(getTarget().getTable());
		}
		return table;
	}

	@Override
	public IType getType() {
		if (getTarget().getType() != null && type == null) {
			type = getFacadeFactory().createType(getTarget().getType());
		}
		return type;
	}

	@Override
	public void setElement(IValue element) {
		assert element instanceof ValueProxy;
		if (isCollection()) {
			((Collection)getTarget()).setElement(((ValueProxy)element).getTarget());
		}
	}

	@Override
	public void setCollectionTable(ITable table) {
		assert table instanceof TableProxy;
		if (isCollection()) {
			collectionTable = table;
			((Collection)getTarget()).setCollectionTable(((TableProxy)table).getTarget());
		}
	}

	@Override
	public void setTable(ITable table) {
		assert table instanceof TableProxy;
		if (isSimpleValue()) {
			((SimpleValue)getTarget()).setTable(((TableProxy)table).getTarget());
		}
	}

	@Override
	public boolean isList() {
		return getTarget() instanceof List;
	}

	@Override
	public void setIndex(IValue value) {
		assert value instanceof ValueProxy;
		((IndexedCollection)getTarget()).setIndex(((ValueProxy)value).getTarget());
	}

	@Override
	public void setTypeName(String name) {
		if (isSimpleValue()) {
			((SimpleValue)getTarget()).setTypeName(name);
		}
	}

	@Override
	public String getComponentClassName() {
		return isComponent() ? ((Component)getTarget()).getComponentClassName() : null;
	}

	@Override
	public Iterator<IColumn> getColumnIterator() {
		if (columns == null) {
			initializeColumns();
		}
		return columns.iterator();
	}
	
	@SuppressWarnings("rawtypes")
	private void initializeColumns() {
		columns = new HashSet<IColumn>();
		Iterator iterator = getTarget().getColumnIterator();
		while (iterator.hasNext()) {
			Object object = iterator.next();
			if (object instanceof Column) {
				columns.add(getFacadeFactory().createColumn(object));
			}
		}
	}

	@Override
	public Boolean isTypeSpecified() {
		return isSimpleValue() ? ((SimpleValue)getTarget()).isTypeSpecified() : null; 
	}
	
	@Override
	public String toString() {
		return getTarget().toString();
	}

	@Override
	public ITable getCollectionTable() {
		if (isCollection() && collectionTable == null) {
			Table ct = ((Collection)getTarget()).getCollectionTable();
			if (ct != null) {
				collectionTable = getFacadeFactory().createTable(ct);
			}
		}
		return collectionTable;
	}

	@Override
	public IValue getKey() {
		if (key == null && isCollection()) {
			Collection collection = (Collection)getTarget();
			if (collection.getKey() != null) {
				key = getFacadeFactory().createValue(collection.getKey());
			}
		}
		return key;
	}

	public boolean isDependantValue() {
		return getTarget() instanceof DependantValue;
	}

	@Override
	public boolean isAny() {
		return getTarget() instanceof Any;
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
		assert column instanceof ColumnFacadeFactory;
		assert getTarget() instanceof SimpleValue;
		((SimpleValue)getTarget()).addColumn(((ColumnFacadeFactory)column).getTarget());
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
		assert keyValue instanceof ValueProxy;
		assert getTarget() instanceof Collection;
		assert ((ValueProxy)keyValue).getTarget() instanceof KeyValue;
		((Collection)getTarget()).setKey((KeyValue)((ValueProxy)keyValue).getTarget());
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
