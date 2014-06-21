package org.jboss.tools.hibernate.spi;

import java.util.Iterator;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;

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
	Table getTable();
	IType getType();
	void setElement(IValue element);
	void setCollectionTable(Table table);
	void setTable(Table table);
	boolean isList();
	void setIndex(IValue value);
	void setTypeName(String name);
	String getComponentClassName();
	Iterator<Column> getColumnIterator();
	Boolean isTypeSpecified();
	Table getCollectionTable();
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
