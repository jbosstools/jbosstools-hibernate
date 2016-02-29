package org.jboss.tools.hibernate.runtime.spi;

import java.util.Iterator;

public interface IPersistentClass {

	String getClassName();
	String getEntityName();
	boolean isAssignableToRootClass();
	boolean isRootClass();
	IProperty getIdentifierProperty();
	boolean hasIdentifierProperty();
	boolean isInstanceOfRootClass();
	boolean isInstanceOfSubclass();
	IPersistentClass getRootClass();
	Iterator<IProperty> getPropertyClosureIterator();
	IPersistentClass getSuperclass();
	Iterator<IProperty> getPropertyIterator();
	IProperty getProperty(String string);
	ITable getTable();
	Boolean isAbstract();
	IValue getDiscriminator();
	IValue getIdentifier();
	Iterator<IJoin> getJoinIterator();
	IProperty getVersion();
	void setClassName(String className);
	void setEntityName(String entityName);
	void setDiscriminatorValue(String value);
	void setAbstract(Boolean b);
	void addProperty(IProperty property);
	boolean isInstanceOfJoinedSubclass();
	void setTable(ITable table);
	void setKey(IValue value);
	boolean isInstanceOfSpecialRootClass();
	IProperty getProperty();
	IProperty getParentProperty();
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
	String getWhere();
	ITable getRootTable();

}
