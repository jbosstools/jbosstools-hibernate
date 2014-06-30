package org.jboss.tools.hibernate.proxy;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

import org.hibernate.mapping.Any;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.IdentifierBag;
import org.hibernate.mapping.IndexedCollection;
import org.hibernate.mapping.List;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimitiveArray;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Set;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.Value;
import org.jboss.tools.hibernate.spi.IColumn;
import org.jboss.tools.hibernate.spi.IProperty;
import org.jboss.tools.hibernate.spi.ITable;
import org.jboss.tools.hibernate.spi.IType;
import org.jboss.tools.hibernate.spi.IValue;
import org.jboss.tools.hibernate.spi.IValueVisitor;

public class ValueProxy implements IValue {
	
	private Value target = null;
	private IValue collectionElement = null;
	private ITable collectionTable = null;
	private ITable table = null;
	private IValue key = null;
	private IValue index = null;
	private IType type = null;
	private HashSet<IColumn> columns = null;

	public ValueProxy(Value value) {
		target = value;
	}

	@Override
	public boolean isSimpleValue() {
		return target.isSimpleValue();
	}

	@Override
	public boolean isCollection() {
		return target instanceof Collection;
	}

	public Value getTarget() {
		return target;
	}

	@Override
	public IValue getCollectionElement() {
		if (isCollection() && collectionElement == null) {
			Value element = ((Collection)target).getElement();
			if (element != null) {
				collectionElement = new ValueProxy(element);
			}
		}
		return collectionElement;
	}

	@Override
	public boolean isOneToMany() {
		return target instanceof OneToMany;
	}

	@Override
	public boolean isManyToOne() {
		return target instanceof ManyToOne;
	}

	@Override
	public boolean isOneToOne() {
		return target instanceof OneToOne;
	}

	@Override
	public boolean isMap() {
		return target instanceof Map;
	}

	@Override
	public boolean isComponent() {
		return target instanceof Component;
	}

	@Override
	public Boolean isEmbedded() {
		Boolean result = null;
		if (isComponent()) {
			result = ((Component)target).isEmbedded();
		} else if (isToOne()) {
			result = ((ToOne)target).isEmbedded();
		}
		return result;
	}

	@Override
	public boolean isToOne() {
		return target instanceof ToOne;
	}

	@Override
	public Object accept(IValueVisitor valueVisitor) {
		return valueVisitor.accept(this);
	}

	@Override
	public ITable getTable() {
		if (target.getTable() != null && table == null) {
			table = new TableProxy(target.getTable());
		}
		return table;
	}

	@Override
	public IType getType() {
		if (target.getType() != null && type == null) {
			type = new TypeProxy(target.getType());
		}
		return type;
	}

	@Override
	public void setElement(IValue element) {
		assert element instanceof ValueProxy;
		if (isCollection()) {
			((Collection)target).setElement(((ValueProxy)element).getTarget());
		}
	}

	@Override
	public void setCollectionTable(ITable table) {
		assert table instanceof TableProxy;
		if (isCollection()) {
			collectionTable = table;
			((Collection)target).setCollectionTable(((TableProxy)table).getTarget());
		}
	}

	@Override
	public void setTable(ITable table) {
		assert table instanceof TableProxy;
		if (isSimpleValue()) {
			((SimpleValue)target).setTable(((TableProxy)table).getTarget());
		}
	}

	@Override
	public boolean isList() {
		return target instanceof List;
	}

	@Override
	public void setIndex(IValue value) {
		assert value instanceof ValueProxy;
		((IndexedCollection)target).setIndex(((ValueProxy)value).getTarget());
	}

	@Override
	public void setTypeName(String name) {
		if (isSimpleValue()) {
			((SimpleValue)target).setTypeName(name);
		}
	}

	@Override
	public String getComponentClassName() {
		return isComponent() ? ((Component)target).getComponentClassName() : null;
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
		Iterator iterator = target.getColumnIterator();
		while (iterator.hasNext()) {
			Object object = iterator.next();
			if (object instanceof Column) {
				columns.add(new ColumnProxy(object));
			}
		}
	}

	@Override
	public Boolean isTypeSpecified() {
		return isSimpleValue() ? ((SimpleValue)target).isTypeSpecified() : null; 
	}
	
	@Override
	public String toString() {
		return target.toString();
	}

	@Override
	public ITable getCollectionTable() {
		if (isCollection() && collectionTable == null) {
			Table ct = ((Collection)target).getCollectionTable();
			if (ct != null) {
				collectionTable = new TableProxy(ct);
			}
		}
		return collectionTable;
	}

	@Override
	public IValue getKey() {
		if (key == null && isCollection()) {
			Collection collection = (Collection)target;
			if (collection.getKey() != null) {
				key = new ValueProxy(collection.getKey());
			}
		}
		return key;
	}

	public boolean isDependantValue() {
		return target instanceof DependantValue;
	}

	@Override
	public boolean isAny() {
		return target instanceof Any;
	}

	@Override
	public boolean isSet() {
		return target instanceof Set;
	}

	@Override
	public IValue getIndex() {
		if (index == null && isList()) {
			List list = (List)target;
			if (list.getIndex() != null) {
				index = new ValueProxy(list.getIndex());
			}
		}
		return index;
	}

	@Override
	public boolean isArray() {
		return target instanceof Array;
	}

	@Override
	public String getElementClassName() {
		String result = null;
		if (isArray()) {
			result = ((Array)target).getElementClassName();
		}
		return result;
	}

	@Override
	public boolean isPrimitiveArray() {
		return target instanceof PrimitiveArray;
	}

	@Override
	public String getTypeName() {
		String result = null;
		if (isSimpleValue())  {
			result = ((SimpleValue)target).getTypeName();
		}
		return result;
	}

	@Override
	public boolean isIdentifierBag() {
		return target instanceof IdentifierBag;
	}

	@Override
	public boolean isBag() {
		return target instanceof Bag;
	}

	@Override
	public String getReferencedEntityName() {
		String result = null;
		if (target instanceof OneToMany) {
			result = ((OneToMany)target).getReferencedEntityName();
		} else if (target instanceof ToOne) {
			result = ((ToOne)target).getReferencedEntityName();
		}
		return result;
	}

	@Override
	public String getEntityName() {
		String result = null;
		if (target instanceof OneToOne) {
			result = ((OneToOne)target).getEntityName();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<IProperty> getPropertyIterator() {
		HashSet<IProperty> result = new HashSet<IProperty>();
		if (target instanceof Component) {
			Iterator<Property> origin = ((Component)target).getPropertyIterator();
			while (origin.hasNext()) {
				result.add(new PropertyProxy(origin.next()));
			}
		}
		return result.iterator();
	}

	@Override
	public void addColumn(IColumn column) {
		assert column instanceof ColumnProxy;
		assert target instanceof SimpleValue;
		((SimpleValue)target).addColumn(((ColumnProxy)column).getTarget());
	}

	@Override
	public void setTypeParameters(Properties typeParameters) {
		assert target instanceof SimpleValue;
		((SimpleValue)target).setTypeParameters(typeParameters);
	}

	@Override
	public String getForeignKeyName() {
		assert target instanceof SimpleValue;
		return ((SimpleValue)target).getForeignKeyName();
	}

	@Override
	public PersistentClass getOwner() {
		assert target instanceof Component;
		return ((Component)target).getOwner();
	}

	@Override
	public IValue getElement() {
		assert target instanceof Collection;
		IValue result = null;
		if (((Collection)target).getElement() != null) {
			result = new ValueProxy(((Collection)target).getElement());
		}
		return result;
	}

	@Override
	public String getParentProperty() {
		return ((Component)target).getParentProperty();
	}

}
