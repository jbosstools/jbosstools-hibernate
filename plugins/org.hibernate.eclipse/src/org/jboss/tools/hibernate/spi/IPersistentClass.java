package org.jboss.tools.hibernate.spi;

import java.util.Iterator;

import org.hibernate.mapping.Join;
import org.hibernate.mapping.Property;

public interface IPersistentClass {

	String getClassName();
	String getEntityName();
	boolean isAssignableToRootClass();
	boolean isRootClass();
	Property getIdentifierProperty();
	boolean hasIdentifierProperty();
	boolean isInstanceOfRootClass();
	boolean isInstanceOfSubclass();
	String getNodeName();
	IPersistentClass getRootClass();
	Iterator<Property> getPropertyClosureIterator();
	IPersistentClass getSuperclass();
	Iterator<Property> getPropertyIterator();
	Property getProperty(String string);
	ITable getTable();
	boolean isAbstract();
	IValue getDiscriminator();
	IValue getIdentifier();
	Iterator<Join> getJoinIterator();
	IProperty getVersion();
	void setClassName(String className);
	void setEntityName(String entityName);
	void setDiscriminatorValue(String value);
	void setAbstract(boolean b);
	void addProperty(Property property);

}
