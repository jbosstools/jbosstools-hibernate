package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Subclass;
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
	
	@BeforeEach
	public void setUp() {
		rootClassFacade = FACADE_FACTORY.createRootClass();
		rootClassTarget = (PersistentClass)((IFacade)rootClassFacade).getTarget();
		singleTableSubclassFacade = FACADE_FACTORY.createSingleTableSubclass(rootClassFacade);
		singleTableSubclassTarget = (PersistentClass)((IFacade)singleTableSubclassFacade).getTarget();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(rootClassFacade);
		assertNotNull(rootClassTarget);
		assertTrue(rootClassTarget instanceof RootClass);
		assertNotNull(singleTableSubclassFacade);
		assertNotNull(singleTableSubclassTarget);
		assertTrue(singleTableSubclassTarget instanceof Subclass);
	}
	
	@Test
	public void testGetClassName() {
		assertNotEquals("Foo", rootClassFacade.getClassName());
		rootClassTarget.setClassName("Foo");
		assertEquals("Foo", rootClassFacade.getClassName());
	}
	
	@Test
	public void testGetEntityName() {
		assertNotEquals("Foo", rootClassFacade.getEntityName());
		rootClassTarget.setEntityName("Foo");
		assertEquals("Foo", rootClassFacade.getEntityName());
	}
	
}
