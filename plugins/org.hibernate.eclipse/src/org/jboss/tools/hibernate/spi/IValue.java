package org.jboss.tools.hibernate.spi;

import java.util.Iterator;

import org.hibernate.mapping.Property;

public interface IValue {

	boolean isSimpleValue();
	boolean isCollection();
	IValue getCollectionElement();
	boolean isOneToMany();
	boolean isManyToOne();
	boolean isOneToOne();
	boolean isMap();
	boolean isComponent();
	Boolean isEmbedded();
	boolean isToOne();
	Object accept(IValueVisitor valueVisitor);
	ITable getTable();
	IType getType();
	void setElement(IValue element);
	void setCollectionTable(ITable table);
	void setTable(ITable table);
	boolean isList();
	void setIndex(IValue value);
	void setTypeName(String name);
	String getComponentClassName();
	Iterator<IColumn> getColumnIterator();
	Boolean isTypeSpecified();
	ITable getCollectionTable();
	IValue getKey();
	IValue getIndex();
	String getElementClassName();
	String getTypeName();
	boolean isDependantValue();
	boolean isAny();
	boolean isSet();
	boolean isPrimitiveArray();
	boolean isArray();
	boolean isIdentifierBag();
	boolean isBag();
	String getReferencedEntityName();
	String getEntityName();
	Iterator<? extends Property> getPropertyIterator();

}
