package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.tool.orm.jbt.wrp.PersistentClassWrapper;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.NewFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IPersistentClassTest {

	private static final NewFacadeFactory FACADE_FACTORY = NewFacadeFactory.INSTANCE;
	
	private IPersistentClass rootClassFacade = null;
	private PersistentClass rootClassTarget = null;
	private IPersistentClass singleTableSubclassFacade = null;
	private PersistentClass singleTableSubclassTarget = null;
	private IPersistentClass joinedSubclassFacade = null;
	private PersistentClass joinedSubclassTarget = null;
	
	@BeforeEach
	public void setUp() {
		rootClassFacade = FACADE_FACTORY.createRootClass();
		PersistentClassWrapper rootClassWrapper = (PersistentClassWrapper)((IFacade)rootClassFacade).getTarget();
		rootClassTarget = rootClassWrapper.getWrappedObject();
		singleTableSubclassFacade = FACADE_FACTORY.createSingleTableSubclass(rootClassFacade);
		PersistentClassWrapper singleTableSubclassWrapper = (PersistentClassWrapper)((IFacade)singleTableSubclassFacade).getTarget();
		singleTableSubclassTarget = singleTableSubclassWrapper.getWrappedObject();
		joinedSubclassFacade = FACADE_FACTORY.createJoinedTableSubclass(rootClassFacade);
		PersistentClassWrapper joinedSubclassWrapper = (PersistentClassWrapper)((IFacade)joinedSubclassFacade).getTarget();
		joinedSubclassTarget = joinedSubclassWrapper.getWrappedObject();
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
		assertNotNull(joinedSubclassFacade);
		assertNotNull(joinedSubclassTarget);
		assertTrue(joinedSubclassTarget instanceof JoinedSubclass);
		assertSame(rootClassTarget, joinedSubclassTarget.getRootClass());
	}
	
	@Test
	public void testGetClassName() {
		assertNotEquals("Foo", rootClassFacade.getClassName());
		assertNotEquals("Foo", singleTableSubclassFacade.getClassName());
		assertNotEquals("Foo", joinedSubclassTarget.getClassName());
		rootClassTarget.setClassName("Foo");
		singleTableSubclassTarget.setClassName("Foo");
		joinedSubclassTarget.setClassName("Foo");
		assertEquals("Foo", rootClassFacade.getClassName());
		assertEquals("Foo", singleTableSubclassFacade.getClassName());
		assertEquals("Foo", joinedSubclassTarget.getClassName());
	}
	
	@Test
	public void testGetEntityName() {
		assertNotEquals("Foo", rootClassFacade.getEntityName());
		assertNotEquals("Foo", singleTableSubclassTarget.getEntityName());
		assertNotEquals("Foo", joinedSubclassTarget.getEntityName());
		rootClassTarget.setEntityName("Foo");
		singleTableSubclassTarget.setEntityName("Foo");
		joinedSubclassTarget.setEntityName("Foo");
		assertEquals("Foo", rootClassFacade.getEntityName());
		assertEquals("Foo", singleTableSubclassTarget.getEntityName());
		assertEquals("Foo", joinedSubclassTarget.getEntityName());
	}
	
	@Test
	public void testIsAssignableToRootClass() {
		assertTrue(rootClassFacade.isAssignableToRootClass());
		assertFalse(singleTableSubclassFacade.isAssignableToRootClass());
		assertFalse(joinedSubclassFacade.isAssignableToRootClass());
	}
	
	@Test
	public void testIsRootClass() {
		assertTrue(rootClassFacade.isRootClass());
		assertFalse(singleTableSubclassFacade.isRootClass());
		assertFalse(joinedSubclassFacade.isRootClass());
	}
	
	@Test
	public void testGetIdentifierProperty() throws Exception {
		assertNull(rootClassFacade.getIdentifierProperty());
		Property propertyTarget = new Property();
		((RootClass)rootClassTarget).setIdentifierProperty(propertyTarget);
		IProperty propertyFacade = rootClassFacade.getIdentifierProperty();
		assertNotNull(propertyFacade);
		assertSame(propertyTarget, ((IFacade)propertyFacade).getTarget());
	}
	
}
