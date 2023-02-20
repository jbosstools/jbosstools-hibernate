package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Subclass;
import org.hibernate.tool.orm.jbt.wrp.PersistentClassWrapper;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.NewFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IPersistentClassTest {

	private static final NewFacadeFactory FACADE_FACTORY = NewFacadeFactory.INSTANCE;
	
	private IPersistentClass rootClassFacade = null;
	private PersistentClass rootClassTarget = null;
	private IPersistentClass singleTableSubclassFacade = null;
	private PersistentClass singleTableSubclassTarget = null;
	private IPersistentClass joinedTableSubclassFacade = null;
	private PersistentClass joinedTableSubclassTarget = null;
	
	@BeforeEach
	public void setUp() {
		rootClassFacade = FACADE_FACTORY.createRootClass();
		PersistentClassWrapper rootClassWrapper = (PersistentClassWrapper)((IFacade)rootClassFacade).getTarget();
		rootClassTarget = rootClassWrapper.getWrappedObject();
		singleTableSubclassFacade = FACADE_FACTORY.createSingleTableSubclass(rootClassFacade);
		PersistentClassWrapper singleTableSubclassWrapper = (PersistentClassWrapper)((IFacade)singleTableSubclassFacade).getTarget();
		singleTableSubclassTarget = singleTableSubclassWrapper.getWrappedObject();
		joinedTableSubclassFacade = FACADE_FACTORY.createJoinedTableSubclass(rootClassFacade);
		joinedTableSubclassTarget = (PersistentClass)((IFacade)joinedTableSubclassFacade).getTarget();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(rootClassFacade);
		assertNotNull(rootClassTarget);
		assertTrue(rootClassTarget instanceof RootClass);
		assertNotNull(singleTableSubclassFacade);
		assertNotNull(singleTableSubclassTarget);
		assertTrue(singleTableSubclassTarget instanceof SingleTableSubclass);
		assertSame(rootClassTarget, singleTableSubclassTarget.getRootClass());
		assertNotNull(joinedTableSubclassFacade);
		assertNotNull(joinedTableSubclassTarget);
		assertTrue(joinedTableSubclassTarget instanceof Subclass);
		assertSame(rootClassTarget, joinedTableSubclassTarget.getRootClass());
	}
	
	@Test
	public void testGetClassName() {
		assertNotEquals("Foo", rootClassFacade.getClassName());
		assertNotEquals("Foo", singleTableSubclassFacade.getClassName());
		assertNotEquals("Foo", joinedTableSubclassTarget.getClassName());
		rootClassTarget.setClassName("Foo");
		singleTableSubclassTarget.setClassName("Foo");
		joinedTableSubclassTarget.setClassName("Foo");
		assertEquals("Foo", rootClassFacade.getClassName());
		assertEquals("Foo", singleTableSubclassFacade.getClassName());
		assertEquals("Foo", joinedTableSubclassTarget.getClassName());
	}
	
	@Test
	public void testGetEntityName() {
		assertNotEquals("Foo", rootClassFacade.getEntityName());
		assertNotEquals("Foo", singleTableSubclassTarget.getEntityName());
		assertNotEquals("Foo", joinedTableSubclassTarget.getEntityName());
		rootClassTarget.setEntityName("Foo");
		singleTableSubclassTarget.setEntityName("Foo");
		joinedTableSubclassTarget.setEntityName("Foo");
		assertEquals("Foo", rootClassFacade.getEntityName());
		assertEquals("Foo", singleTableSubclassTarget.getEntityName());
		assertEquals("Foo", joinedTableSubclassTarget.getEntityName());
	}
	
}
