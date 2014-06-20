package org.jboss.tools.hibernate.proxy;

import java.util.Iterator;

import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.IndexedCollection;
import org.hibernate.mapping.List;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.Value;
import org.hibernate.mapping.ValueVisitor;
import org.jboss.tools.hibernate.spi.IType;
import org.jboss.tools.hibernate.spi.IValue;

public class ValueProxy implements IValue {
	
	private Value target = null;
	private IValue collectionElement = null;
	private IType type = null;

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
	public Object accept(ValueVisitor valueVisitor) {
		return target.accept(valueVisitor);
	}

	@Override
	public Table getTable() {
		return target.getTable();
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
	public void setCollectionTable(Table table) {
		if (isCollection()) {
			((Collection)target).setCollectionTable(table);
		}
	}

	@Override
	public void setTable(Table table) {
		if (isSimpleValue()) {
			((SimpleValue)target).setTable(table);
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

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<Column> getColumnIterator() {
		return target.getColumnIterator();
	}

	@Override
	public Boolean isTypeSpecified() {
		return isSimpleValue() ? ((SimpleValue)target).isTypeSpecified() : null; 
	}
	
	@Override
	public String toString() {
		return target.toString();
	}

}
