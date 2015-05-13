package org.jboss.tools.hibernate.runtime.v_3_6.internal;

import java.util.Iterator;

import org.hibernate.mapping.PersistentClass;
import org.jboss.tools.hibernate.runtime.common.AbstractPersistentClassFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.ITable;

public class PersistentClassFacadeImpl extends AbstractPersistentClassFacade {
	
	private ITable rootTable = null;

	public PersistentClassFacadeImpl(
			IFacadeFactory facadeFactory,
			PersistentClass persistentClass) {
		super(facadeFactory, persistentClass);
	}

	public PersistentClass getTarget() {
		return (PersistentClass)super.getTarget();
	}

	@Override
	public Iterator<IPersistentClass> getSubclassIterator() {
		if (subclasses == null) {
			initializeSubclasses();
		}
		return subclasses.iterator();
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
