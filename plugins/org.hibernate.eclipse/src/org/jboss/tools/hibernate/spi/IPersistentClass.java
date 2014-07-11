package org.jboss.tools.hibernate.spi;

import java.util.Iterator;

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
	Boolean isAbstract();
	IValue getDiscriminator();
	IValue getIdentifier();
	Iterator<IJoin> getJoinIterator();
	IProperty getVersion();
	void setClassName(String className);
	void setEntityName(String entityName);
	void setDiscriminatorValue(String value);
	void setAbstract(boolean b);
	void addProperty(Property property);
	boolean isInstanceOfJoinedSubclass();
	void setTable(ITable table);
	void setKey(IValue value);
	boolean isInstanceOfSpecialRootClass();
	Property getProperty();
	Property getParentProperty();
	void setIdentifierProperty(IProperty property);
	void setIdentifier(IValue value);
	void setDiscriminator(IValue discr);
	void setProxyInterfaceName(String interfaceName);
	void setLazy(boolean b);
	Iterator<?> getSubclassIterator();
	boolean isCustomDeleteCallable();
	boolean isCustomInsertCallable();
	boolean isCustomUpdateCallable();
	boolean isDiscriminatorInsertable();
	boolean isDiscriminatorValueNotNull();
	boolean isDiscriminatorValueNull();
	boolean isExplicitPolymorphism();
	boolean isForceDiscriminator();
	boolean isInherited();
	boolean isJoinedSubclass();
	boolean isLazy();
	boolean isLazyPropertiesCacheable();
	boolean isMutable();
	boolean isPolymorphic();
	boolean isVersioned();
	int getBatchSize();
	String getCacheConcurrencyStrategy();
	String getCustomSQLDelete();
	String getCustomSQLInsert();
	String getCustomSQLUpdate();
	String getDiscriminatorValue();
	String getLoaderName();
	int getOptimisticLockMode();
	String getTemporaryIdTableDDL();
	String getTemporaryIdTableName();
	String getWhere();
	ITable getRootTable();

}
