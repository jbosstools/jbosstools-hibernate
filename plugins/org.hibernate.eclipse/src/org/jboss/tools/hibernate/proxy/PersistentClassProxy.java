package org.jboss.tools.hibernate.proxy;

import java.util.Iterator;

import org.hibernate.mapping.Join;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Subclass;
import org.jboss.tools.hibernate.spi.IPersistentClass;
import org.jboss.tools.hibernate.spi.IProperty;
import org.jboss.tools.hibernate.spi.ITable;
import org.jboss.tools.hibernate.spi.IValue;

public class PersistentClassProxy implements IPersistentClass {
	
	private PersistentClass target = null;
	private IPersistentClass rootClass = null;
	private IPersistentClass superClass = null;
	private ITable table = null;
	private IValue discriminator = null;
	private IValue identifier = null;
	private IProperty version = null;

	public PersistentClassProxy(PersistentClass persistentClass) {
		target = persistentClass;
	}

	public PersistentClass getTarget() {
		return target;
	}

	@Override
	public String getClassName() {
		return target.getClassName();
	}

	@Override
	public String getEntityName() {
		return target.getEntityName();
	}

	@Override
	public boolean isAssignableToRootClass() {
		return RootClass.class.isAssignableFrom(target.getClass());
	}

	@Override
	public boolean isRootClass() {
		return target.getClass() == RootClass.class;
	}

	@Override
	public Property getIdentifierProperty() {
		return target.getIdentifierProperty();
	}

	@Override
	public boolean hasIdentifierProperty() {
		return target.hasIdentifierProperty();
	}

	@Override
	public boolean isInstanceOfRootClass() {
		return target instanceof RootClass;
	}

	@Override
	public boolean isInstanceOfSubclass() {
		return target instanceof Subclass;
	}

	@Override
	public String getNodeName() {
		return target.getNodeName();
	}

	@Override
	public IPersistentClass getRootClass() {
		if (rootClass == null && target.getRootClass() != null) {
			rootClass = new PersistentClassProxy(target.getRootClass());
		}
		return rootClass;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<Property> getPropertyClosureIterator() {
		return target.getPropertyClosureIterator();
	}

	@Override
	public IPersistentClass getSuperclass() {
		if (superClass != null) {
			superClass = new PersistentClassProxy(target.getSuperclass());
		}
		return superClass;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<Property> getPropertyIterator() {
		return target.getPropertyIterator();
	}

	@Override
	public Property getProperty(String string) {
		return target.getProperty(string);
	}

	@Override
	public ITable getTable() {
		if (table == null && target.getTable() != null) {
			table = new TableProxy(target.getTable());
		}
		return table;
	}

	@Override
	public Boolean isAbstract() {
		return target.isAbstract();
	}

	@Override
	public IValue getDiscriminator() {
		if (discriminator == null && target.getDiscriminator() != null) {
			discriminator = new ValueProxy(target.getDiscriminator());
		}
		return discriminator;
	}

	@Override
	public IValue getIdentifier() {
		if (identifier == null && target.getIdentifier() != null) {
			identifier = new ValueProxy(target.getIdentifier());
		}
		return identifier;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<Join> getJoinIterator() {
		return target.getJoinIterator();
	}

	@Override
	public IProperty getVersion() {
		if (version == null && target.getVersion() != null) {
			version = new PropertyProxy(target.getVersion());
		}
		return version;
	}

	@Override
	public void setClassName(String className) {
		target.setClassName(className);
	}

	@Override
	public void setEntityName(String entityName) {
		target.setEntityName(entityName);
	}

	@Override
	public void setDiscriminatorValue(String value) {
		target.setDiscriminatorValue(value);
	}

	@Override
	public void setAbstract(boolean b) {
		target.setAbstract(b);
	}

	@Override
	public void addProperty(Property property) {
		target.addProperty(property);
	}

	@Override
	public boolean isInstanceOfJoinedSubclass() {
		return target instanceof JoinedSubclass;
	}

	@Override
	public void setTable(ITable table) {
		assert (target instanceof JoinedSubclass || target instanceof RootClass);
		assert table instanceof TableProxy;
		if (target instanceof RootClass) {
			((RootClass)target).setTable(((TableProxy)table).getTarget());
		} else if (target instanceof JoinedSubclass) {
			((JoinedSubclass)target).setTable(((TableProxy)table).getTarget());
		}
	}

	@Override
	public void setKey(IValue value) {
		assert target instanceof JoinedSubclass;
		assert value instanceof ValueProxy;
		assert ((ValueProxy)value).getTarget() instanceof KeyValue;
		((JoinedSubclass)target).setKey((KeyValue)((ValueProxy)value).getTarget());
	}

	public boolean isInstanceOfSpecialRootClass() {
		return false;
	}

	@Override
	public Property getProperty() {
		throw new RuntimeException("getProperty() is only allowed on SpecialRootClass"); //$NON-NLS-1$
	}

	@Override
	public Property getParentProperty() {
		throw new RuntimeException("getProperty() is only allowed on SpecialRootClass"); //$NON-NLS-1$
	}

	@Override
	public void setIdentifierProperty(IProperty property) {
		assert property instanceof PropertyProxy;
		assert target instanceof RootClass;
		((RootClass)target).setIdentifierProperty(((PropertyProxy)property).getTarget());
	}

	@Override
	public void setIdentifier(IValue value) {
		assert value instanceof ValueProxy;
		assert ((ValueProxy)value).getTarget() instanceof KeyValue;
		assert target instanceof RootClass;
		((RootClass)target).setIdentifier((KeyValue)((ValueProxy)value).getTarget());
	}

	@Override
	public void setDiscriminator(IValue discr) {
		assert target instanceof RootClass;
		assert discr instanceof ValueProxy;
		((RootClass)target).setDiscriminator(((ValueProxy)discr).getTarget());
	}

	@Override
	public void setProxyInterfaceName(String name) {
		target.setProxyInterfaceName(name);
	}

	@Override
	public void setLazy(boolean b) {
		target.setLazy(b);
	}

	@Override
	public Iterator<?> getSubclassIterator() {
		return target.getSubclassIterator();
	}

	@Override
	public boolean isCustomDeleteCallable() {
		return target.isCustomDeleteCallable();
	}

	@Override
	public boolean isCustomInsertCallable() {
		return target.isCustomInsertCallable();
	}

	@Override
	public boolean isCustomUpdateCallable() {
		return target.isCustomUpdateCallable();
	}

	@Override
	public boolean isDiscriminatorInsertable() {
		return target.isDiscriminatorInsertable();
	}

	@Override
	public boolean isDiscriminatorValueNotNull() {
		return target.isDiscriminatorValueNotNull();
	}

	@Override
	public boolean isDiscriminatorValueNull() {
		return target.isDiscriminatorValueNull();
	}

	@Override
	public boolean isExplicitPolymorphism() {
		return target.isExplicitPolymorphism();
	}

	@Override
	public boolean isForceDiscriminator() {
		return target.isForceDiscriminator();
	}

	@Override
	public boolean isInherited() {
		return target.isInherited();
	}

	@Override
	public boolean isJoinedSubclass() {
		return target.isJoinedSubclass();
	}

	@Override
	public boolean isLazy() {
		return target.isLazy();
	}

	@Override
	public boolean isLazyPropertiesCacheable() {
		return target.isLazyPropertiesCacheable();
	}

	@Override
	public boolean isMutable() {
		return target.isMutable();
	}

	@Override
	public boolean isPolymorphic() {
		return target.isPolymorphic();
	}

	@Override
	public boolean isVersioned() {
		return target.isVersioned();
	}

	@Override
	public int getBatchSize() {
		return target.getBatchSize();
	}

	@Override
	public String getCacheConcurrencyStrategy() {
		return target.getCacheConcurrencyStrategy();
	}

	@Override
	public String getCustomSQLDelete() {
		return target.getCustomSQLDelete();
	}

	@Override
	public String getCustomSQLInsert() {
		return target.getCustomSQLInsert();
	}

	@Override
	public String getCustomSQLUpdate() {
		return target.getCustomSQLUpdate();
	}

	@Override
	public String getDiscriminatorValue() {
		return target.getDiscriminatorValue();
	}

	@Override
	public String getLoaderName() {
		return target.getLoaderName();
	}

	@Override
	public int getOptimisticLockMode() {
		return target.getOptimisticLockMode();
	}

	@Override
	public String getTemporaryIdTableDDL() {
		return target.getTemporaryIdTableDDL();
	}

	@Override
	public String getTemporaryIdTableName() {
		return target.getTemporaryIdTableName();
	}

	@Override
	public String getWhere() {
		return target.getWhere();
	}
	
	

}
