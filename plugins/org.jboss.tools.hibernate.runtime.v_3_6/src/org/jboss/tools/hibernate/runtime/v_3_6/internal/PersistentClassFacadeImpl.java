package org.jboss.tools.hibernate.runtime.v_3_6.internal;

import java.util.HashSet;
import java.util.Iterator;

import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Value;
import org.jboss.tools.hibernate.proxy.PropertyProxy;
import org.jboss.tools.hibernate.proxy.TableProxy;
import org.jboss.tools.hibernate.runtime.common.AbstractPersistentClassFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IJoin;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.IValue;

public class PersistentClassFacadeImpl extends AbstractPersistentClassFacade {
	
	private ITable rootTable = null;
	private IValue discriminator = null;
	private IValue identifier = null;
	private IProperty version = null;
	private HashSet<IPersistentClass> subclasses = null;
	private HashSet<IJoin> joins = null;

	public PersistentClassFacadeImpl(
			IFacadeFactory facadeFactory,
			PersistentClass persistentClass) {
		super(facadeFactory, persistentClass);
	}

	public PersistentClass getTarget() {
		return (PersistentClass)super.getTarget();
	}

	@Override
	public IValue getDiscriminator() {
		if (discriminator == null && getTarget().getDiscriminator() != null) {
			discriminator = getFacadeFactory().createValue(getTarget().getDiscriminator());
		}
		return discriminator;
	}

	@Override
	public IValue getIdentifier() {
		if (identifier == null && getTarget().getIdentifier() != null) {
			identifier = getFacadeFactory().createValue(getTarget().getIdentifier());
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
			joins.add(getFacadeFactory().createJoin(origin.next()));
		}
	}

	@Override
	public IProperty getVersion() {
		if (version == null && getTarget().getVersion() != null) {
			version = getFacadeFactory().createProperty(getTarget().getVersion());
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
		assert value instanceof IFacade;
		assert ((IFacade)value).getTarget() instanceof KeyValue;
		((JoinedSubclass)getTarget()).setKey((KeyValue)((IFacade)value).getTarget());
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
		assert value instanceof IFacade;
		assert ((IFacade)value).getTarget() instanceof KeyValue;
		assert getTarget() instanceof RootClass;
		((RootClass)getTarget()).setIdentifier((KeyValue)((IFacade)value).getTarget());
	}

	@Override
	public void setDiscriminator(IValue discr) {
		assert getTarget() instanceof RootClass;
		assert discr instanceof IFacade;
		((RootClass)getTarget()).setDiscriminator((Value)((IFacade)discr).getTarget());
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
			subclasses.add(getFacadeFactory().createPersistentClass(origin.next()));
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
			rootTable = getFacadeFactory().createTable(getTarget().getRootTable());
		}
		return rootTable;
	}
	
	

}
