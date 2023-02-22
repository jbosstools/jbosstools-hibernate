package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.util.Iterator;

import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Table;
import org.hibernate.tool.orm.jbt.wrp.PersistentClassWrapper;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.NewFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.AbstractPersistentClassFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.ITable;
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
	
	@Test
	public void testHasIdentifierProperty() {
		assertFalse(rootClassFacade.hasIdentifierProperty());
		((RootClass)rootClassTarget).setIdentifierProperty(new Property());
		assertTrue(rootClassFacade.hasIdentifierProperty());
	}
	
	@Test
	public void testIsInstanceOfRootClass() {
		assertTrue(rootClassFacade.isInstanceOfRootClass());
		assertFalse(singleTableSubclassFacade.isInstanceOfRootClass());
		assertFalse(joinedSubclassFacade.isInstanceOfRootClass());
	}
	
	@Test
	public void testIsInstanceOfSubclass() {
		assertFalse(rootClassFacade.isInstanceOfSubclass());
		assertTrue(singleTableSubclassFacade.isInstanceOfSubclass());
		assertTrue(joinedSubclassFacade.isInstanceOfSubclass());
	}
	
	@Test
	public void testGetRootClass() {
		assertSame(((IFacade)rootClassFacade.getRootClass()).getTarget(), rootClassTarget);
		assertSame(((IFacade)singleTableSubclassFacade.getRootClass()).getTarget(), rootClassTarget);
		assertSame(((IFacade)joinedSubclassFacade.getRootClass()).getTarget(), rootClassTarget);
	}
	
	@Test
	public void testGetPropertyClosureIterator() {
		Iterator<IProperty> propertyClosureIterator = rootClassFacade.getPropertyClosureIterator();
		assertFalse(propertyClosureIterator.hasNext());
		Property propertyTarget = new Property();
		rootClassTarget.addProperty(propertyTarget);
		propertyClosureIterator = rootClassFacade.getPropertyClosureIterator();
		assertTrue(propertyClosureIterator.hasNext());
		IProperty propertyFacade = propertyClosureIterator.next();
		assertSame(propertyTarget, ((IFacade)propertyFacade).getTarget());
	}
	
	@Test
	public void testGetSuperClass() {
		assertNull(rootClassFacade.getSuperclass());
		assertSame(rootClassTarget, ((IFacade)singleTableSubclassFacade.getSuperclass()).getTarget());
		assertSame(rootClassTarget, ((IFacade)joinedSubclassFacade.getSuperclass()).getTarget());
	}
	
	@Test
	public void testGetPropertyIterator() {
		Iterator<IProperty> propertyIterator = rootClassFacade.getPropertyIterator();
		assertFalse(propertyIterator.hasNext());
		Property propertyTarget = new Property();
		rootClassTarget.addProperty(propertyTarget);
		propertyIterator = rootClassFacade.getPropertyIterator();
		assertTrue(propertyIterator.hasNext());
		IProperty propertyFacade = propertyIterator.next();
		assertSame(propertyTarget, ((IFacade)propertyFacade).getTarget());
	}
	
	@Test
	public void testGetProperty() {
		try {
			rootClassFacade.getProperty("foo");
			fail();
		} catch (Throwable t) {
			assertEquals(
					"property [foo] not found on entity [null]", 
					t.getMessage());
		}
		Property propertyTarget = new Property();
		propertyTarget.setName("foo");
		rootClassTarget.addProperty(propertyTarget);
		IProperty propertyFacade = rootClassFacade.getProperty("foo");
		assertSame(propertyTarget, ((IFacade)propertyFacade).getTarget());
		try {
			rootClassFacade.getProperty();
			fail();
		} catch (Throwable t) {
			assertEquals(
					"getProperty() is only allowed on SpecialRootClass", 
					t.getMessage());
		}
	}
	
	@Test
	public void testGetTable() throws Exception {
		assertNull(rootClassFacade.getTable());
		Table tableTarget = new Table("test");
		((RootClass)rootClassTarget).setTable(tableTarget);
		ITable tableFacade = rootClassFacade.getTable();
		assertSame(tableTarget, ((IFacade)tableFacade).getTarget());
	}
	
}
