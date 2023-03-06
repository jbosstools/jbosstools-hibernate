package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;

import org.hibernate.MappingException;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.util.DummyMetadataBuildingContext;
import org.hibernate.tool.orm.jbt.util.SpecialRootClass;
import org.hibernate.tool.orm.jbt.wrp.PersistentClassWrapper;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.NewFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IJoin;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.IValue;
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
	private IPersistentClass specialRootClassFacade = null;
	private PersistentClass specialRootClassTarget = null;
	
	private IProperty propertyFacade = null;
	private Property propertyTarget = null;
	
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
		propertyFacade = FACADE_FACTORY.createProperty();
		propertyTarget = (Property)((IFacade)propertyFacade).getTarget();
		specialRootClassFacade = FACADE_FACTORY.createSpecialRootClass(propertyFacade);
		PersistentClassWrapper specialRootClassWrapper = (PersistentClassWrapper)((IFacade)specialRootClassFacade).getTarget();
		specialRootClassTarget = specialRootClassWrapper.getWrappedObject();
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
		assertNotNull(specialRootClassFacade);
		assertNotNull(specialRootClassTarget);
		assertTrue(specialRootClassTarget instanceof SpecialRootClass);
		assertSame(propertyTarget, ((SpecialRootClass)specialRootClassTarget).getProperty());
	}
	
	@Test
	public void testGetClassName() {
		assertNotEquals("Foo", rootClassFacade.getClassName());
		assertNotEquals("Bar", singleTableSubclassFacade.getClassName());
		assertNotEquals("Oof", joinedSubclassFacade.getClassName());
		assertNotEquals("Rab", specialRootClassFacade.getClassName());
		rootClassTarget.setClassName("Foo");
		singleTableSubclassTarget.setClassName("Bar");
		joinedSubclassTarget.setClassName("Oof");
		specialRootClassTarget.setClassName("Rab");
		assertEquals("Foo", rootClassFacade.getClassName());
		assertEquals("Bar", singleTableSubclassFacade.getClassName());
		assertEquals("Oof", joinedSubclassFacade.getClassName());
		assertEquals("Rab", specialRootClassFacade.getClassName());
	}
	
	@Test
	public void testGetEntityName() {
		assertNotEquals("Foo", rootClassFacade.getEntityName());
		assertNotEquals("Bar", singleTableSubclassFacade.getEntityName());
		assertNotEquals("Oof", joinedSubclassFacade.getEntityName());
		assertNotEquals("Rab", specialRootClassFacade.getEntityName());
		rootClassTarget.setEntityName("Foo");
		singleTableSubclassTarget.setEntityName("Bar");
		joinedSubclassTarget.setEntityName("Oof");
		specialRootClassTarget.setEntityName("Rab");
		assertEquals("Foo", rootClassFacade.getEntityName());
		assertEquals("Bar", singleTableSubclassFacade.getEntityName());
		assertEquals("Oof", joinedSubclassFacade.getEntityName());
		assertEquals("Rab", specialRootClassFacade.getEntityName());
	}
	
	@Test
	public void testIsAssignableToRootClass() {
		assertTrue(rootClassFacade.isAssignableToRootClass());
		assertFalse(singleTableSubclassFacade.isAssignableToRootClass());
		assertFalse(joinedSubclassFacade.isAssignableToRootClass());
		assertTrue(specialRootClassFacade.isAssignableToRootClass());
	}
	
	@Test
	public void testIsRootClass() {
		assertTrue(rootClassFacade.isRootClass());
		assertFalse(singleTableSubclassFacade.isRootClass());
		assertFalse(joinedSubclassFacade.isRootClass());
		assertFalse(specialRootClassFacade.isRootClass());
	}
	
	@Test
	public void testGetIdentifierProperty() {
		assertNull(rootClassFacade.getIdentifierProperty());
		assertNull(singleTableSubclassFacade.getIdentifierProperty());
		assertNull(joinedSubclassFacade.getIdentifierProperty());
		Property propertyTarget = new Property();
		((RootClass)rootClassTarget).setIdentifierProperty(propertyTarget);
		IProperty propertyFacade = rootClassFacade.getIdentifierProperty();
		assertNotNull(propertyFacade);
		assertSame(propertyTarget, ((IFacade)propertyFacade).getTarget());
		propertyFacade = singleTableSubclassFacade.getIdentifierProperty();
		assertNotNull(propertyFacade);
		assertSame(propertyTarget, ((IFacade)propertyFacade).getTarget());
		propertyFacade = joinedSubclassFacade.getIdentifierProperty();
		assertNotNull(propertyFacade);
		assertSame(propertyTarget, ((IFacade)propertyFacade).getTarget());
		assertNull(specialRootClassFacade.getIdentifierProperty());
		((RootClass)specialRootClassTarget).setIdentifierProperty(propertyTarget);
		propertyFacade = specialRootClassFacade.getIdentifierProperty();
		assertNotNull(propertyFacade);
		assertSame(propertyTarget, ((IFacade)propertyFacade).getTarget());
	}
	
	@Test
	public void testHasIdentifierProperty() {
		assertFalse(rootClassFacade.hasIdentifierProperty());
		assertFalse(singleTableSubclassFacade.hasIdentifierProperty());
		assertFalse(joinedSubclassFacade.hasIdentifierProperty());
		((RootClass)rootClassTarget).setIdentifierProperty(new Property());
		assertTrue(rootClassFacade.hasIdentifierProperty());
		assertTrue(singleTableSubclassFacade.hasIdentifierProperty());
		assertTrue(joinedSubclassFacade.hasIdentifierProperty());
		assertFalse(specialRootClassFacade.hasIdentifierProperty());
		((RootClass)specialRootClassTarget).setIdentifierProperty(new Property());
		assertTrue(specialRootClassFacade.hasIdentifierProperty());
	}
	
	@Test
	public void testIsInstanceOfRootClass() {
		assertTrue(rootClassFacade.isInstanceOfRootClass());
		assertFalse(singleTableSubclassFacade.isInstanceOfRootClass());
		assertFalse(joinedSubclassFacade.isInstanceOfRootClass());
		assertTrue(specialRootClassFacade.isInstanceOfRootClass());
	}
	
	@Test
	public void testIsInstanceOfSubclass() {
		assertFalse(rootClassFacade.isInstanceOfSubclass());
		assertTrue(singleTableSubclassFacade.isInstanceOfSubclass());
		assertTrue(joinedSubclassFacade.isInstanceOfSubclass());
		assertFalse(specialRootClassFacade.isInstanceOfSubclass());
	}
	
	@Test
	public void testGetRootClass() {
		assertSame(((IFacade)rootClassFacade.getRootClass()).getTarget(), rootClassTarget);
		assertSame(((IFacade)singleTableSubclassFacade.getRootClass()).getTarget(), rootClassTarget);
		assertSame(((IFacade)joinedSubclassFacade.getRootClass()).getTarget(), rootClassTarget);
		assertSame(((IFacade)specialRootClassFacade.getRootClass()).getTarget(), specialRootClassTarget);
	}
	
	@Test
	public void testGetPropertyClosureIterator() {
		Iterator<IProperty> propertyClosureIterator = rootClassFacade.getPropertyClosureIterator();
		assertFalse(propertyClosureIterator.hasNext());
		propertyClosureIterator = singleTableSubclassFacade.getPropertyClosureIterator();
		assertFalse(propertyClosureIterator.hasNext());
		propertyClosureIterator = joinedSubclassFacade.getPropertyClosureIterator();
		assertFalse(propertyClosureIterator.hasNext());
		Property propertyTarget = new Property();
		rootClassTarget.addProperty(propertyTarget);
		propertyClosureIterator = rootClassFacade.getPropertyClosureIterator();
		assertTrue(propertyClosureIterator.hasNext());
		IProperty propertyFacade = propertyClosureIterator.next();
		assertSame(propertyTarget, ((IFacade)propertyFacade).getTarget());
		propertyClosureIterator = singleTableSubclassFacade.getPropertyClosureIterator();
		assertTrue(propertyClosureIterator.hasNext());
		propertyFacade = propertyClosureIterator.next();
		assertSame(propertyTarget, ((IFacade)propertyFacade).getTarget());
		propertyClosureIterator = joinedSubclassFacade.getPropertyClosureIterator();
		assertTrue(propertyClosureIterator.hasNext());
		propertyFacade = propertyClosureIterator.next();
		assertSame(propertyTarget, ((IFacade)propertyFacade).getTarget());
		propertyClosureIterator = specialRootClassFacade.getPropertyClosureIterator();
		assertFalse(propertyClosureIterator.hasNext());
		specialRootClassTarget.addProperty(propertyTarget);
		propertyClosureIterator = specialRootClassFacade.getPropertyClosureIterator();
		assertTrue(propertyClosureIterator.hasNext());
		propertyFacade = propertyClosureIterator.next();
		assertSame(propertyTarget, ((IFacade)propertyFacade).getTarget());
	}
	
	@Test
	public void testGetSuperClass() {
		assertNull(rootClassFacade.getSuperclass());
		assertSame(rootClassTarget, ((IFacade)singleTableSubclassFacade.getSuperclass()).getTarget());
		assertSame(rootClassTarget, ((IFacade)joinedSubclassFacade.getSuperclass()).getTarget());
		assertNull(specialRootClassFacade.getSuperclass());
	}
	
	@Test
	public void testGetPropertyIterator() {
		Iterator<IProperty> propertyIterator = rootClassFacade.getPropertyIterator();
		assertFalse(propertyIterator.hasNext());
		propertyIterator = joinedSubclassFacade.getPropertyIterator();
		assertFalse(propertyIterator.hasNext());
		Property propertyTarget = new Property();
		rootClassTarget.addProperty(propertyTarget);
		propertyIterator = rootClassFacade.getPropertyIterator();
		assertTrue(propertyIterator.hasNext());
		IProperty propertyFacade = propertyIterator.next();
		assertSame(propertyTarget, ((IFacade)propertyFacade).getTarget());
		propertyIterator = singleTableSubclassFacade.getPropertyIterator();
		assertFalse(propertyIterator.hasNext());
		singleTableSubclassTarget.addProperty(propertyTarget);
		propertyIterator = singleTableSubclassFacade.getPropertyIterator();
		assertTrue(propertyIterator.hasNext());
		propertyFacade = propertyIterator.next();
		assertSame(propertyTarget, ((IFacade)propertyFacade).getTarget());
		propertyIterator = joinedSubclassFacade.getPropertyIterator();
		assertFalse(propertyIterator.hasNext());
		joinedSubclassTarget.addProperty(propertyTarget);
		propertyIterator = joinedSubclassFacade.getPropertyIterator();
		assertTrue(propertyIterator.hasNext());
		propertyFacade = propertyIterator.next();
		assertSame(propertyTarget, ((IFacade)propertyFacade).getTarget());
		propertyIterator = specialRootClassFacade.getPropertyIterator();
		assertFalse(propertyIterator.hasNext());
		specialRootClassTarget.addProperty(propertyTarget);
		propertyIterator = specialRootClassFacade.getPropertyIterator();
		assertTrue(propertyIterator.hasNext());
		propertyFacade = propertyIterator.next();
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
		try {
			singleTableSubclassFacade.getProperty("foo");
			fail();
		} catch (Throwable t) {
			assertEquals(
					"property [foo] not found on entity [null]", 
					t.getMessage());
		}
		try {
			joinedSubclassFacade.getProperty("foo");
			fail();
		} catch (Throwable t) {
			assertEquals(
					"property [foo] not found on entity [null]", 
					t.getMessage());
		}
		propertyTarget.setName("foo");
		rootClassTarget.addProperty(propertyTarget);
		IProperty propertyFacade = rootClassFacade.getProperty("foo");
		assertSame(propertyTarget, ((IFacade)propertyFacade).getTarget());
		propertyFacade = singleTableSubclassFacade.getProperty("foo");
		assertSame(propertyTarget, ((IFacade)propertyFacade).getTarget());
		propertyFacade = joinedSubclassFacade.getProperty("foo");
		assertSame(propertyTarget, ((IFacade)propertyFacade).getTarget());
		try {
			specialRootClassFacade.getProperty("foo");
			fail();
		} catch (Throwable t) {
			assertEquals(
					"property [foo] not found on entity [null]", 
					t.getMessage());
		}
		specialRootClassTarget.addProperty(propertyTarget);
		propertyFacade = specialRootClassFacade.getProperty("foo");
		assertSame(propertyTarget, ((IFacade)propertyFacade).getTarget());
		try {
			propertyFacade = rootClassFacade.getProperty();
			fail();
		} catch (Throwable t) {
			assertEquals(
					"getProperty() is only allowed on SpecialRootClass", 
					t.getMessage());
		}
		try {
			propertyFacade = singleTableSubclassFacade.getProperty();
			fail();
		} catch (Throwable t) {
			assertEquals(
					"getProperty() is only allowed on SpecialRootClass", 
					t.getMessage());
		}
		try {
			propertyFacade = joinedSubclassFacade.getProperty();
			fail();
		} catch (Throwable t) {
			assertEquals(
					"getProperty() is only allowed on SpecialRootClass", 
					t.getMessage());
		}
		propertyFacade = specialRootClassFacade.getProperty();
		assertSame(propertyTarget, ((IFacade)propertyFacade).getTarget());
	}
	
	@Test
	public void testGetTable() {
		assertNull(rootClassFacade.getTable());
		assertNull(singleTableSubclassFacade.getTable());
		Table tableTarget = new Table("test");
		((RootClass)rootClassTarget).setTable(tableTarget);
		ITable tableFacade = rootClassFacade.getTable();
		assertSame(tableTarget, ((IFacade)tableFacade).getTarget());
		tableFacade = singleTableSubclassFacade.getTable();
		assertSame(tableTarget, ((IFacade)tableFacade).getTarget());
		assertNull(joinedSubclassFacade.getTable());
		((JoinedSubclass)joinedSubclassTarget).setTable(tableTarget);
		tableFacade = joinedSubclassFacade.getTable();
		assertSame(tableTarget, ((IFacade)tableFacade).getTarget());
		assertNull(specialRootClassFacade.getTable());
		((RootClass)specialRootClassTarget).setTable(tableTarget);
		tableFacade = specialRootClassFacade.getTable();
		assertSame(tableTarget, ((IFacade)tableFacade).getTarget());
	}
	
	@Test 
	public void testIsAbstract() {
		assertNull(rootClassFacade.isAbstract());
		assertNull(singleTableSubclassFacade.isAbstract());
		assertNull(joinedSubclassFacade.isAbstract());
		assertNull(specialRootClassFacade.isAbstract());
		rootClassTarget.setAbstract(true);
		singleTableSubclassTarget.setAbstract(true);
		joinedSubclassTarget.setAbstract(true);
		specialRootClassTarget.setAbstract(true);
		assertTrue(rootClassFacade.isAbstract());
		assertTrue(singleTableSubclassFacade.isAbstract());
		assertTrue(joinedSubclassFacade.isAbstract());
		assertTrue(specialRootClassFacade.isAbstract());
		rootClassTarget.setAbstract(false);
		singleTableSubclassTarget.setAbstract(false);
		joinedSubclassTarget.setAbstract(false);
		specialRootClassTarget.setAbstract(false);
		assertFalse(rootClassFacade.isAbstract());
		assertFalse(singleTableSubclassFacade.isAbstract());
		assertFalse(joinedSubclassFacade.isAbstract());
		assertFalse(specialRootClassFacade.isAbstract());
	}
	
	@Test
	public void testGetDiscriminator() {
		assertNull(rootClassFacade.getDiscriminator());
		assertNull(singleTableSubclassFacade.getDiscriminator());
		assertNull(joinedSubclassFacade.getDiscriminator());
		Value valueTarget = createValue();
		((RootClass)rootClassTarget).setDiscriminator(valueTarget);
		IValue valueFacade = rootClassFacade.getDiscriminator();
		assertSame(valueTarget, ((IFacade)valueFacade).getTarget());
		valueFacade = singleTableSubclassFacade.getDiscriminator();
		assertSame(valueTarget, ((IFacade)valueFacade).getTarget());
		valueFacade = joinedSubclassFacade.getDiscriminator();
		assertSame(valueTarget, ((IFacade)valueFacade).getTarget());
		assertNull(specialRootClassFacade.getDiscriminator());
		((RootClass)specialRootClassTarget).setDiscriminator(valueTarget);
		valueFacade = specialRootClassFacade.getDiscriminator();
		assertSame(valueTarget, ((IFacade)valueFacade).getTarget());
	}
	
	@Test
	public void testGetIdentifier() {
		assertNull(rootClassFacade.getIdentifier());
		assertNull(singleTableSubclassFacade.getIdentifier());
		assertNull(joinedSubclassFacade.getIdentifier());
		KeyValue valueTarget = createValue();
		((RootClass)rootClassTarget).setIdentifier(valueTarget);
		IValue valueFacade = rootClassFacade.getIdentifier();
		assertSame(valueTarget, ((IFacade)valueFacade).getTarget());
		valueFacade = singleTableSubclassFacade.getIdentifier();
		assertSame(valueTarget, ((IFacade)valueFacade).getTarget());
		valueFacade = joinedSubclassFacade.getIdentifier();
		assertSame(valueTarget, ((IFacade)valueFacade).getTarget());
		assertNull(specialRootClassFacade.getIdentifier());
		((RootClass)specialRootClassTarget).setIdentifier(valueTarget);
		valueFacade = specialRootClassFacade.getIdentifier();
		assertSame(valueTarget, ((IFacade)valueFacade).getTarget());
	}
	
	@Test
	public void testGetJoinIterator() {
		Join joinTarget = new Join();
		assertFalse(rootClassFacade.getJoinIterator().hasNext());
		rootClassTarget.addJoin(joinTarget);
		Iterator<IJoin> joinIterator = rootClassFacade.getJoinIterator();
		IJoin joinFacade = joinIterator.next();
		assertSame(((IFacade)joinFacade).getTarget(), joinTarget);
		assertFalse(singleTableSubclassFacade.getJoinIterator().hasNext());
		singleTableSubclassTarget.addJoin(joinTarget);
		joinIterator = singleTableSubclassFacade.getJoinIterator();
		joinFacade = joinIterator.next();
		assertSame(((IFacade)joinFacade).getTarget(), joinTarget);
		assertFalse(joinedSubclassFacade.getJoinIterator().hasNext());
		joinedSubclassTarget.addJoin(joinTarget);
		joinIterator = joinedSubclassFacade.getJoinIterator();
		joinFacade = joinIterator.next();
		assertSame(((IFacade)joinFacade).getTarget(), joinTarget);
		assertFalse(specialRootClassFacade.getJoinIterator().hasNext());
		specialRootClassTarget.addJoin(joinTarget);
		joinIterator = specialRootClassFacade.getJoinIterator();
		joinFacade = joinIterator.next();
		assertSame(((IFacade)joinFacade).getTarget(), joinTarget);
	}
	
	@Test
	public void testGetVersion() {
		Property versionTarget = new Property();
		assertNull(rootClassFacade.getVersion());
		assertNull(singleTableSubclassFacade.getVersion());
		assertNull(joinedSubclassFacade.getVersion());
		((RootClass)rootClassTarget).setVersion(versionTarget);
		IProperty propertyFacade = rootClassFacade.getVersion();
		assertSame(versionTarget, ((IFacade)propertyFacade).getTarget());
		propertyFacade = singleTableSubclassFacade.getVersion();
		assertSame(versionTarget, ((IFacade)propertyFacade).getTarget());
		propertyFacade = joinedSubclassFacade.getVersion();
		assertSame(versionTarget, ((IFacade)propertyFacade).getTarget());
		assertNull(specialRootClassFacade.getVersion());
		((RootClass)specialRootClassTarget).setVersion(versionTarget);
		propertyFacade = specialRootClassFacade.getVersion();
		assertSame(versionTarget, ((IFacade)propertyFacade).getTarget());
	}
	
	@Test
	public void testSetClassName() {
		assertNull(rootClassTarget.getClassName());
		assertNull(singleTableSubclassTarget.getClassName());
		assertNull(joinedSubclassTarget.getClassName());
		assertNull(specialRootClassTarget.getClassName());
		rootClassFacade.setClassName("foo");
		singleTableSubclassFacade.setClassName("bar");
		joinedSubclassFacade.setClassName("oof");
		specialRootClassFacade.setClassName("rab");
		assertEquals("foo", rootClassTarget.getClassName());
		assertEquals("bar", singleTableSubclassTarget.getClassName());
		assertEquals("oof", joinedSubclassTarget.getClassName());
		assertEquals("rab", specialRootClassTarget.getClassName());
	}
	
	@Test
	public void testSetEntityName() {
		assertNull(rootClassTarget.getEntityName());
		rootClassFacade.setEntityName("foo");
		assertEquals("foo", rootClassTarget.getEntityName());
		assertNull(singleTableSubclassTarget.getEntityName());
		singleTableSubclassFacade.setEntityName("bar");
		assertEquals("bar", singleTableSubclassTarget.getEntityName());
		assertNull(joinedSubclassTarget.getEntityName());
		joinedSubclassFacade.setEntityName("oof");
		assertEquals("oof", joinedSubclassTarget.getEntityName());
		assertNull(specialRootClassTarget.getEntityName());
		specialRootClassFacade.setEntityName("rab");
		assertEquals("rab", specialRootClassTarget.getEntityName());
	}
	
	@Test
	public void testSetDiscriminatorValue() {
		assertNull(rootClassTarget.getDiscriminatorValue());
		rootClassFacade.setDiscriminatorValue("foo");
		assertEquals("foo", rootClassTarget.getDiscriminatorValue());
		assertNull(singleTableSubclassTarget.getDiscriminatorValue());
		singleTableSubclassFacade.setDiscriminatorValue("bar");
		assertEquals("bar", singleTableSubclassTarget.getDiscriminatorValue());
		assertNull(joinedSubclassTarget.getDiscriminatorValue());
		joinedSubclassFacade.setDiscriminatorValue("oof");
		assertEquals("oof", joinedSubclassTarget.getDiscriminatorValue());
		assertNull(specialRootClassTarget.getDiscriminatorValue());
		specialRootClassFacade.setDiscriminatorValue("rab");
		assertEquals("rab", specialRootClassTarget.getDiscriminatorValue());
	}
	
	@Test
	public void testSetAbstract() {
		assertNull(rootClassTarget.isAbstract());
		rootClassFacade.setAbstract(true);
		assertTrue(rootClassTarget.isAbstract());
		rootClassFacade.setAbstract(false);
		assertFalse(rootClassTarget.isAbstract());
		assertNull(singleTableSubclassTarget.isAbstract());
		singleTableSubclassFacade.setAbstract(true);
		assertTrue(singleTableSubclassTarget.isAbstract());
		singleTableSubclassFacade.setAbstract(false);
		assertFalse(singleTableSubclassTarget.isAbstract());
		assertNull(joinedSubclassTarget.isAbstract());
		joinedSubclassFacade.setAbstract(true);
		assertTrue(joinedSubclassTarget.isAbstract());
		joinedSubclassFacade.setAbstract(false);
		assertFalse(joinedSubclassTarget.isAbstract());
		assertNull(specialRootClassTarget.isAbstract());
		specialRootClassFacade.setAbstract(true);
		assertTrue(specialRootClassTarget.isAbstract());
		specialRootClassFacade.setAbstract(false);
		assertFalse(specialRootClassTarget.isAbstract());
	}
	
	@Test
	public void testAddProperty() {
		IProperty firstPropertyFacade = FACADE_FACTORY.createProperty();
		Property firstPropertyTarget = (Property)((IFacade)firstPropertyFacade).getTarget();
		firstPropertyTarget.setName("foo");
		IProperty secondPropertyFacade = FACADE_FACTORY.createProperty();
		Property secondPropertyTarget = (Property)((IFacade)secondPropertyFacade).getTarget();
		secondPropertyTarget.setName("bar");
		try {
			rootClassTarget.getProperty("foo");
			fail();
		} catch (MappingException e) {
			assertEquals(
					"property [foo] not found on entity [null]", 
					e.getMessage());
		}
		try {
			singleTableSubclassTarget.getProperty("foo");
			fail();
		} catch (MappingException e) {
			assertEquals(
					"property [foo] not found on entity [null]", 
					e.getMessage());
		}
		try {
			joinedSubclassTarget.getProperty("foo");
			fail();
		} catch (MappingException e) {
			assertEquals(
					"property [foo] not found on entity [null]", 
					e.getMessage());
		}
		try {
			specialRootClassTarget.getProperty("foo");
			fail();
		} catch (MappingException e) {
			assertEquals(
					"property [foo] not found on entity [null]", 
					e.getMessage());
		}
		rootClassFacade.addProperty(firstPropertyFacade);
		assertSame(rootClassTarget.getProperty("foo"), firstPropertyTarget);
		assertSame(singleTableSubclassTarget.getProperty("foo"), firstPropertyTarget);
		singleTableSubclassFacade.addProperty(secondPropertyFacade);
		try {
			rootClassTarget.getProperty("bar");
			fail();
		} catch (MappingException e) {
			assertEquals(
					"property [bar] not found on entity [null]", 
					e.getMessage());
		}
		assertSame(singleTableSubclassTarget.getProperty("bar"), secondPropertyTarget);
		try {
			joinedSubclassTarget.getProperty("bar");
			fail();
		} catch (MappingException e) {
			assertEquals(
					"property [bar] not found on entity [null]", 
					e.getMessage());
		}
		joinedSubclassFacade.addProperty(secondPropertyFacade);
		assertSame(joinedSubclassTarget.getProperty("foo"), firstPropertyTarget);
		assertSame(joinedSubclassTarget.getProperty("bar"), secondPropertyTarget);
		specialRootClassFacade.addProperty(firstPropertyFacade);
		assertSame(specialRootClassTarget.getProperty("foo"), firstPropertyTarget);
	}
	
	@Test
	public void testIsInstanceOfJoinedSubclass() {
		assertFalse(rootClassFacade.isInstanceOfJoinedSubclass());
		assertFalse(singleTableSubclassFacade.isInstanceOfJoinedSubclass());
		assertTrue(joinedSubclassFacade.isInstanceOfJoinedSubclass());
		assertFalse(specialRootClassFacade.isInstanceOfJoinedSubclass());
	}
	
	@Test
	public void testSetTable() {
		Table tableTarget = new Table("");
		ITable tableFacade = FACADE_FACTORY.createTable(tableTarget);
		assertNull(rootClassTarget.getTable());
		assertNull(singleTableSubclassTarget.getTable());
		rootClassFacade.setTable(tableFacade);
		assertSame(tableTarget, rootClassTarget.getTable());
		assertSame(tableTarget, singleTableSubclassTarget.getTable());
		try {
			singleTableSubclassFacade.setTable(tableFacade);
			fail();
		} catch (RuntimeException e) {
			assertEquals(e.getMessage(), "Method 'setTable(Table)' is not supported.");
		}
		assertNull(joinedSubclassTarget.getTable());
		joinedSubclassFacade.setTable(tableFacade);
		assertSame(tableTarget, joinedSubclassTarget.getTable());
		assertNull(specialRootClassTarget.getTable());
		specialRootClassFacade.setTable(tableFacade);
		assertSame(tableTarget, specialRootClassTarget.getTable());
	}
	
	@Test
	public void testSetKey() {
		assertNull(rootClassTarget.getKey());
		assertNull(singleTableSubclassTarget.getKey());
		assertNull(joinedSubclassTarget.getKey());
		assertNull(specialRootClassTarget.getKey());
		Value valueTarget = createValue();
		IValue valueFacade = FACADE_FACTORY.createValue(valueTarget);
		try {
			rootClassFacade.setKey(valueFacade);
			fail();
		} catch (RuntimeException e) {
			assertEquals("setKey(KeyValue) is only allowed on JoinedSubclass", e.getMessage());
		}
		try {
			singleTableSubclassFacade.setKey(valueFacade);
			fail();
		} catch (RuntimeException e) {
			assertEquals("setKey(KeyValue) is only allowed on JoinedSubclass", e.getMessage());
		}
		joinedSubclassFacade.setKey(valueFacade);
		assertSame(valueTarget, joinedSubclassTarget.getKey());
		try {
			specialRootClassFacade.setKey(valueFacade);
			fail();
		} catch (RuntimeException e) {
			assertEquals("setKey(KeyValue) is only allowed on JoinedSubclass", e.getMessage());
		}
	}
	
	@Test
	public void testIsInstanceOfSpecialRootClass() {
		assertFalse(rootClassFacade.isInstanceOfSpecialRootClass());
		assertFalse(singleTableSubclassFacade.isInstanceOfSpecialRootClass());
		assertFalse(joinedSubclassFacade.isInstanceOfSpecialRootClass());
		assertTrue(specialRootClassFacade.isInstanceOfSpecialRootClass());
	}
	
	@Test
	public void testGetParentProperty() {
		try {
			rootClassFacade.getParentProperty();
			fail();
		} catch (RuntimeException e) {
			assertEquals("getParentProperty() is only allowed on SpecialRootClass", e.getMessage());
		}
		try {
			singleTableSubclassFacade.getParentProperty();
			fail();
		} catch (RuntimeException e) {
			assertEquals("getParentProperty() is only allowed on SpecialRootClass", e.getMessage());
		}
		try {
			joinedSubclassFacade.getParentProperty();
			fail();
		} catch (RuntimeException e) {
			assertEquals("getParentProperty() is only allowed on SpecialRootClass", e.getMessage());
		}
		assertNull(specialRootClassFacade.getParentProperty());
		Component component = new Component(
				DummyMetadataBuildingContext.INSTANCE, 
				rootClassTarget);
		component.setParentProperty("foo");
		IValue componentFacade = FACADE_FACTORY.createValue(component);
		IProperty propertyFacade = FACADE_FACTORY.createProperty();
		propertyFacade.setValue(componentFacade);
		propertyFacade.setPersistentClass(rootClassFacade);
		specialRootClassFacade = FACADE_FACTORY.createSpecialRootClass(propertyFacade);
		IProperty parentProperty = specialRootClassFacade.getParentProperty();
		assertNotNull(parentProperty);
		assertEquals("foo", parentProperty.getName());
	}
	
	@Test
	public void testSetIdentifierProperty() {
		IProperty propertyFacade = FACADE_FACTORY.createProperty();
		Property propertyTarget = (Property)((IFacade)propertyFacade).getTarget();
		assertNull(rootClassTarget.getIdentifierProperty());
		rootClassFacade.setIdentifierProperty(propertyFacade);
		assertSame(propertyTarget, rootClassTarget.getIdentifierProperty());
		assertNull(specialRootClassTarget.getIdentifierProperty());
		specialRootClassFacade.setIdentifierProperty(propertyFacade);
		assertSame(propertyTarget, specialRootClassTarget.getIdentifierProperty());
		try {
			singleTableSubclassFacade.setIdentifierProperty(propertyFacade);
			fail();
		} catch (RuntimeException e) {
			assertEquals("setIdentifierProperty(Property) is only allowed on RootClass instances", e.getMessage());
		}
		try {
			joinedSubclassFacade.setIdentifierProperty(propertyFacade);
			fail();
		} catch (RuntimeException e) {
			assertEquals("setIdentifierProperty(Property) is only allowed on RootClass instances", e.getMessage());
		}
	}
	
	@Test
	public void testSetIdentifier() {
		Value valueTarget = createValue();
		IValue valueFacade = FACADE_FACTORY.createValue(valueTarget);
		assertNull(rootClassTarget.getIdentifier());
		assertNull(singleTableSubclassTarget.getIdentifier());
		assertNull(joinedSubclassTarget.getIdentifier());
		rootClassFacade.setIdentifier(valueFacade);
		assertSame(valueTarget, rootClassTarget.getIdentifier());
		assertSame(valueTarget, singleTableSubclassTarget.getIdentifier());
		assertSame(valueTarget, joinedSubclassTarget.getIdentifier());
		try {
			singleTableSubclassFacade.setIdentifier(valueFacade);
			fail();
		} catch (RuntimeException e) {
			assertEquals("Method 'setIdentifier(KeyValue)' can only be called on RootClass instances", e.getMessage());
		}
		try {
			joinedSubclassFacade.setIdentifier(valueFacade);
			fail();
		} catch (RuntimeException e) {
			assertEquals("Method 'setIdentifier(KeyValue)' can only be called on RootClass instances", e.getMessage());
		}
		assertNull(specialRootClassTarget.getIdentifier());
		specialRootClassFacade.setIdentifier(valueFacade);
		assertSame(valueTarget, specialRootClassTarget.getIdentifier());
	}
	
	@Test
	public void testSetDiscriminator() throws Exception {
		Value valueTarget = createValue();
		IValue valueFacade = FACADE_FACTORY.createValue(valueTarget);
		assertNull(rootClassTarget.getDiscriminator());
		assertNull(singleTableSubclassTarget.getDiscriminator());
		assertNull(joinedSubclassTarget.getDiscriminator());
		assertNull(specialRootClassTarget.getDiscriminator());
		rootClassFacade.setDiscriminator(valueFacade);
		assertSame(valueTarget, rootClassTarget.getDiscriminator());
		assertSame(valueTarget, singleTableSubclassTarget.getDiscriminator());
		assertSame(valueTarget, joinedSubclassTarget.getDiscriminator());
		try {
			singleTableSubclassFacade.setDiscriminator(valueFacade);
			fail();
		} catch (RuntimeException e) {
			assertEquals("Method 'setDiscriminator(Value)' can only be called on RootClass instances", e.getMessage());
		}
		try {
			joinedSubclassFacade.setDiscriminator(valueFacade);
			fail();
		} catch (RuntimeException e) {
			assertEquals("Method 'setDiscriminator(Value)' can only be called on RootClass instances", e.getMessage());
		}
		assertNull(specialRootClassTarget.getDiscriminator());
		specialRootClassFacade.setDiscriminator(valueFacade);
		assertSame(valueTarget, specialRootClassTarget.getDiscriminator());
	}
	
	@Test
	public void testSetProxyInterfaceName() {
		assertNull(rootClassTarget.getProxyInterfaceName());
		rootClassFacade.setProxyInterfaceName("foo");
		assertEquals("foo", rootClassTarget.getProxyInterfaceName());
		assertNull(singleTableSubclassTarget.getProxyInterfaceName());
		singleTableSubclassFacade.setProxyInterfaceName("bar");
		assertEquals("bar", singleTableSubclassTarget.getProxyInterfaceName());
		assertNull(joinedSubclassTarget.getProxyInterfaceName());
		joinedSubclassFacade.setProxyInterfaceName("oof");
		assertEquals("oof", joinedSubclassTarget.getProxyInterfaceName());
		assertNull(specialRootClassTarget.getProxyInterfaceName());
		specialRootClassFacade.setProxyInterfaceName("rab");
		assertEquals("rab", specialRootClassTarget.getProxyInterfaceName());
	}
	
	@Test
	public void testSetLazy() {
		rootClassFacade.setLazy(true);
		assertTrue(rootClassTarget.isLazy());
		rootClassFacade.setLazy(false);
		assertFalse(rootClassTarget.isLazy());
		singleTableSubclassFacade.setLazy(true);
		assertTrue(singleTableSubclassTarget.isLazy());
		singleTableSubclassFacade.setLazy(false);
		assertFalse(singleTableSubclassTarget.isLazy());
		joinedSubclassFacade.setLazy(true);
		assertTrue(joinedSubclassTarget.isLazy());
		joinedSubclassFacade.setLazy(false);
		assertFalse(joinedSubclassTarget.isLazy());
		specialRootClassFacade.setLazy(true);
		assertTrue(specialRootClassTarget.isLazy());
		specialRootClassFacade.setLazy(false);
		assertFalse(specialRootClassTarget.isLazy());
	}
	
	private KeyValue createValue() {
		return (KeyValue)Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[] { KeyValue.class }, 
				new InvocationHandler() {	
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						return null;
					}
		});
	}
}
