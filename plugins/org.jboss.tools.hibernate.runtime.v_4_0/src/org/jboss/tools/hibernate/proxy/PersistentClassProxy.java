package org.jboss.tools.hibernate.proxy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.hibernate.mapping.Join;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Subclass;
import org.jboss.tools.hibernate.runtime.common.AbstractPersistentClassFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IJoin;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.IValue;

public class PersistentClassProxy extends AbstractPersistentClassFacade {
	
	private IPersistentClass rootClass = null;
	private IPersistentClass superClass = null;
	private ITable table = null;
	private ITable rootTable = null;
	private IValue discriminator = null;
	private IValue identifier = null;
	private IProperty version = null;
	private IProperty identifierProperty = null;
	private HashSet<IPersistentClass> subclasses = null;
	private HashSet<IJoin> joins = null;
	private HashSet<IProperty> propertyClosures = null;
	private HashMap<String, IProperty> properties = null;

	public PersistentClassProxy(
			IFacadeFactory facadeFactory,
			PersistentClass persistentClass) {
		super(facadeFactory, persistentClass);
	}

	public PersistentClass getTarget() {
		return (PersistentClass)super.getTarget();
	}

	@Override
	public String getClassName() {
		return getTarget().getClassName();
	}

	@Override
	public String getEntityName() {
		return getTarget().getEntityName();
	}

	@Override
	public boolean isAssignableToRootClass() {
		return RootClass.class.isAssignableFrom(getTarget().getClass());
	}

	@Override
	public boolean isRootClass() {
		return getTarget().getClass() == RootClass.class;
	}

	@Override
	public IProperty getIdentifierProperty() {
		if (identifierProperty == null  && getTarget().getIdentifierProperty() != null) {
			identifierProperty = new PropertyProxy(getTarget().getIdentifierProperty());
		}
		return identifierProperty;
	}

	@Override
	public boolean hasIdentifierProperty() {
		return getTarget().hasIdentifierProperty();
	}

	@Override
	public boolean isInstanceOfRootClass() {
		return getTarget() instanceof RootClass;
	}

	@Override
	public boolean isInstanceOfSubclass() {
		return getTarget() instanceof Subclass;
	}

	@Override
	public String getNodeName() {
		return getTarget().getNodeName();
	}

	@Override
	public IPersistentClass getRootClass() {
		if (rootClass == null && getTarget().getRootClass() != null) {
			rootClass = new PersistentClassProxy(getFacadeFactory(), getTarget().getRootClass());
		}
		return rootClass;
	}

	@Override
	public Iterator<IProperty> getPropertyClosureIterator() {
		if (propertyClosures == null) {
			initializePropertyClosures();
		}
		return propertyClosures.iterator();
	}
	
	@SuppressWarnings("unchecked")
	private void initializePropertyClosures() {
		propertyClosures = new HashSet<IProperty>();
		Iterator<Property> origin = getTarget().getPropertyClosureIterator();
		while (origin.hasNext()) {
			propertyClosures.add(new PropertyProxy(origin.next()));
		}
	}

	@Override
	public IPersistentClass getSuperclass() {
		if (superClass != null) {
			superClass = new PersistentClassProxy(getFacadeFactory(), getTarget().getSuperclass());
		}
		return superClass;
	}

	@Override
	public Iterator<IProperty> getPropertyIterator() {
		if (properties == null) {
			initializeProperties();
		}
		return properties.values().iterator();
	}
	
	@SuppressWarnings("unchecked")
	private void initializeProperties() {
		properties = new HashMap<String, IProperty>();
		Iterator<Property> origin = getTarget().getPropertyIterator();
		while (origin.hasNext()) {
			Property property = origin.next();
			properties.put(property.getName(), new PropertyProxy(property));
		}
	}

	@Override
	public IProperty getProperty(String string) {
		if (properties == null) {
			initializeProperties();
		}
		return properties.get(string);
	}

	@Override
	public ITable getTable() {
		if (table == null && getTarget().getTable() != null) {
			table = new TableProxy(getFacadeFactory(), getTarget().getTable());
		}
		return table;
	}

	@Override
	public Boolean isAbstract() {
		return getTarget().isAbstract();
	}

	@Override
	public IValue getDiscriminator() {
		if (discriminator == null && getTarget().getDiscriminator() != null) {
			discriminator = new ValueProxy(getFacadeFactory(), getTarget().getDiscriminator());
		}
		return discriminator;
	}

	@Override
	public IValue getIdentifier() {
		if (identifier == null && getTarget().getIdentifier() != null) {
			identifier = new ValueProxy(getFacadeFactory(), getTarget().getIdentifier());
		}
		return identifier;
	}

	@Override
	public Iterator<IJoin> getJoinIterator() {
		if (joins == null) {
			initializeJoins();
		}
		return joins.iterator();
	}
	
	private void initializeJoins() {
		joins = new HashSet<IJoin>();
		Iterator<?> origin = getTarget().getJoinIterator();
		while (origin.hasNext()) {
			joins.add(new JoinProxy(getFacadeFactory(), (Join)origin.next()));
		}
	}

	@Override
	public IProperty getVersion() {
		if (version == null && getTarget().getVersion() != null) {
			version = new PropertyProxy(getTarget().getVersion());
		}
		return version;
	}

	@Override
	public void setClassName(String className) {
		getTarget().setClassName(className);
	}

	@Override
	public void setEntityName(String entityName) {
		getTarget().setEntityName(entityName);
	}

	@Override
	public void setDiscriminatorValue(String value) {
		getTarget().setDiscriminatorValue(value);
	}

	@Override
	public void setAbstract(boolean b) {
		getTarget().setAbstract(b);
	}

	@Override
	public void addProperty(IProperty property) {
		assert property instanceof PropertyProxy;
		getTarget().addProperty(((PropertyProxy)property).getTarget());
		properties = null;
		propertyClosures = null;
	}

	@Override
	public boolean isInstanceOfJoinedSubclass() {
		return getTarget() instanceof JoinedSubclass;
	}

	@Override
	public void setTable(ITable table) {
		assert (getTarget() instanceof JoinedSubclass || getTarget() instanceof RootClass);
		assert table instanceof TableProxy;
		if (getTarget() instanceof RootClass) {
			((RootClass)getTarget()).setTable(((TableProxy)table).getTarget());
		} else if (getTarget() instanceof JoinedSubclass) {
			((JoinedSubclass)getTarget()).setTable(((TableProxy)table).getTarget());
		}
	}

	@Override
	public void setKey(IValue value) {
		assert getTarget() instanceof JoinedSubclass;
		assert value instanceof ValueProxy;
		assert ((ValueProxy)value).getTarget() instanceof KeyValue;
		((JoinedSubclass)getTarget()).setKey((KeyValue)((ValueProxy)value).getTarget());
	}

	public boolean isInstanceOfSpecialRootClass() {
		return false;
	}

	@Override
	public IProperty getProperty() {
		throw new RuntimeException("getProperty() is only allowed on SpecialRootClass"); //$NON-NLS-1$
	}

	@Override
	public IProperty getParentProperty() {
		throw new RuntimeException("getProperty() is only allowed on SpecialRootClass"); //$NON-NLS-1$
	}

	@Override
	public void setIdentifierProperty(IProperty property) {
		assert getTarget() instanceof RootClass;
		assert property instanceof PropertyProxy;
		((RootClass)getTarget()).setIdentifierProperty(((PropertyProxy)property).getTarget());
		identifierProperty = property;
	}

	@Override
	public void setIdentifier(IValue value) {
		assert value instanceof ValueProxy;
		assert ((ValueProxy)value).getTarget() instanceof KeyValue;
		assert getTarget() instanceof RootClass;
		((RootClass)getTarget()).setIdentifier((KeyValue)((ValueProxy)value).getTarget());
	}

	@Override
	public void setDiscriminator(IValue discr) {
		assert getTarget() instanceof RootClass;
		assert discr instanceof ValueProxy;
		((RootClass)getTarget()).setDiscriminator(((ValueProxy)discr).getTarget());
	}

	@Override
	public void setProxyInterfaceName(String name) {
		getTarget().setProxyInterfaceName(name);
	}

	@Override
	public void setLazy(boolean b) {
		getTarget().setLazy(b);
	}

	@Override
	public Iterator<IPersistentClass> getSubclassIterator() {
		if (subclasses == null) {
			initializeSubclasses();
		}
		return subclasses.iterator();
	}
	
	private void initializeSubclasses() {
		Iterator<?> origin = getTarget().getSubclassIterator();
		subclasses = new HashSet<IPersistentClass>();
		while (origin.hasNext()) {
			subclasses.add(new PersistentClassProxy(getFacadeFactory(), (Subclass)origin.next()));
		}
	}

	@Override
	public boolean isCustomDeleteCallable() {
		return getTarget().isCustomDeleteCallable();
	}

	@Override
	public boolean isCustomInsertCallable() {
		return getTarget().isCustomInsertCallable();
	}

	@Override
	public boolean isCustomUpdateCallable() {
		return getTarget().isCustomUpdateCallable();
	}

	@Override
	public boolean isDiscriminatorInsertable() {
		return getTarget().isDiscriminatorInsertable();
	}

	@Override
	public boolean isDiscriminatorValueNotNull() {
		return getTarget().isDiscriminatorValueNotNull();
	}

	@Override
	public boolean isDiscriminatorValueNull() {
		return getTarget().isDiscriminatorValueNull();
	}

	@Override
	public boolean isExplicitPolymorphism() {
		return getTarget().isExplicitPolymorphism();
	}

	@Override
	public boolean isForceDiscriminator() {
		return getTarget().isForceDiscriminator();
	}

	@Override
	public boolean isInherited() {
		return getTarget().isInherited();
	}

	@Override
	public boolean isJoinedSubclass() {
		return getTarget().isJoinedSubclass();
	}

	@Override
	public boolean isLazy() {
		return getTarget().isLazy();
	}

	@Override
	public boolean isLazyPropertiesCacheable() {
		return getTarget().isLazyPropertiesCacheable();
	}

	@Override
	public boolean isMutable() {
		return getTarget().isMutable();
	}

	@Override
	public boolean isPolymorphic() {
		return getTarget().isPolymorphic();
	}

	@Override
	public boolean isVersioned() {
		return getTarget().isVersioned();
	}

	@Override
	public int getBatchSize() {
		return getTarget().getBatchSize();
	}

	@Override
	public String getCacheConcurrencyStrategy() {
		return getTarget().getCacheConcurrencyStrategy();
	}

	@Override
	public String getCustomSQLDelete() {
		return getTarget().getCustomSQLDelete();
	}

	@Override
	public String getCustomSQLInsert() {
		return getTarget().getCustomSQLInsert();
	}

	@Override
	public String getCustomSQLUpdate() {
		return getTarget().getCustomSQLUpdate();
	}

	@Override
	public String getDiscriminatorValue() {
		return getTarget().getDiscriminatorValue();
	}

	@Override
	public String getLoaderName() {
		return getTarget().getLoaderName();
	}

	@Override
	public int getOptimisticLockMode() {
		return getTarget().getOptimisticLockMode();
	}

	@Override
	public String getTemporaryIdTableDDL() {
		return getTarget().getTemporaryIdTableDDL();
	}

	@Override
	public String getTemporaryIdTableName() {
		return getTarget().getTemporaryIdTableName();
	}

	@Override
	public String getWhere() {
		return getTarget().getWhere();
	}

	@Override
	public ITable getRootTable() {
		if (rootTable == null) {
			rootTable = new TableProxy(getFacadeFactory(), getTarget().getRootTable());
		}
		return rootTable;
	}
	
	

}
