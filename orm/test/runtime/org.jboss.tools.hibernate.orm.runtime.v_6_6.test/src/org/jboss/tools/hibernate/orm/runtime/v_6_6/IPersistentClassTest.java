package org.jboss.tools.hibernate.orm.runtime.v_6_6;

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
import org.hibernate.engine.OptimisticLockStyle;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.api.factory.WrapperFactory;
import org.hibernate.tool.orm.jbt.api.wrp.PersistentClassWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.Wrapper;
import org.hibernate.tool.orm.jbt.internal.util.DummyMetadataBuildingContext;
import org.hibernate.tool.orm.jbt.internal.util.SpecialRootClass;
import org.jboss.tools.hibernate.orm.runtime.common.GenericFacadeFactory;
import org.jboss.tools.hibernate.orm.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IJoin;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class IPersistentClassTest {

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
	
	@Disabled
	@BeforeEach
	public void beforeEach() {
		rootClassFacade = (IPersistentClass)GenericFacadeFactory.createFacade(
				IPersistentClass.class, 
				WrapperFactory.createRootClassWrapper());
		PersistentClassWrapper rootClassWrapper = (PersistentClassWrapper)((IFacade)rootClassFacade).getTarget();
		rootClassTarget = (PersistentClass)rootClassWrapper.getWrappedObject();
		singleTableSubclassFacade = (IPersistentClass)GenericFacadeFactory.createFacade(
				IPersistentClass.class, 
				WrapperFactory.createSingleTableSubClassWrapper(rootClassWrapper));
		PersistentClassWrapper singleTableSubclassWrapper = (PersistentClassWrapper)((IFacade)singleTableSubclassFacade).getTarget();
		singleTableSubclassTarget = (PersistentClass)singleTableSubclassWrapper.getWrappedObject();
		joinedSubclassFacade = (IPersistentClass)GenericFacadeFactory.createFacade(
				IPersistentClass.class, 
				WrapperFactory.createJoinedTableSubClassWrapper(rootClassWrapper));
		PersistentClassWrapper joinedSubclassWrapper = (PersistentClassWrapper)((IFacade)joinedSubclassFacade).getTarget();
		joinedSubclassTarget = (PersistentClass)joinedSubclassWrapper.getWrappedObject();
		propertyFacade = (IProperty)GenericFacadeFactory.createFacade(
				IProperty.class, 
				WrapperFactory.createPropertyWrapper());
		Wrapper propertyWrapper = (Wrapper)((IFacade)propertyFacade).getTarget();
		propertyTarget = (Property)propertyWrapper.getWrappedObject();
		specialRootClassFacade = (IPersistentClass)GenericFacadeFactory.createFacade(
				IPersistentClass.class, 
				WrapperFactory.createSpecialRootClassWrapper(propertyWrapper));
		PersistentClassWrapper specialRootClassWrapper = (PersistentClassWrapper)((IFacade)specialRootClassFacade).getTarget();
		specialRootClassTarget = (PersistentClass)specialRootClassWrapper.getWrappedObject();
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
		assertSame(propertyTarget, ((Wrapper)((IFacade)propertyFacade).getTarget()).getWrappedObject());
		propertyFacade = singleTableSubclassFacade.getIdentifierProperty();
		assertNotNull(propertyFacade);
		assertSame(propertyTarget, ((Wrapper)((IFacade)propertyFacade).getTarget()).getWrappedObject());
		propertyFacade = joinedSubclassFacade.getIdentifierProperty();
		assertNotNull(propertyFacade);
		assertSame(propertyTarget, ((Wrapper)((IFacade)propertyFacade).getTarget()).getWrappedObject());
		assertNull(specialRootClassFacade.getIdentifierProperty());
		((RootClass)specialRootClassTarget).setIdentifierProperty(propertyTarget);
		propertyFacade = specialRootClassFacade.getIdentifierProperty();
		assertNotNull(propertyFacade);
		assertSame(propertyTarget, ((Wrapper)((IFacade)propertyFacade).getTarget()).getWrappedObject());
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
		assertSame(((Wrapper)((IFacade)rootClassFacade.getRootClass()).getTarget()).getWrappedObject(), rootClassTarget);
		assertSame(((Wrapper)((IFacade)singleTableSubclassFacade.getRootClass()).getTarget()).getWrappedObject(), rootClassTarget);
		assertSame(((Wrapper)((IFacade)joinedSubclassFacade.getRootClass()).getTarget()).getWrappedObject(), rootClassTarget);
		assertSame(((Wrapper)((IFacade)specialRootClassFacade.getRootClass()).getTarget()).getWrappedObject(), specialRootClassTarget);
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
		assertSame(propertyTarget, ((Wrapper)((IFacade)propertyFacade).getTarget()).getWrappedObject());
		propertyClosureIterator = singleTableSubclassFacade.getPropertyClosureIterator();
		assertTrue(propertyClosureIterator.hasNext());
		propertyFacade = propertyClosureIterator.next();
		assertSame(propertyTarget, ((Wrapper)((IFacade)propertyFacade).getTarget()).getWrappedObject());
		propertyClosureIterator = joinedSubclassFacade.getPropertyClosureIterator();
		assertTrue(propertyClosureIterator.hasNext());
		propertyFacade = propertyClosureIterator.next();
		assertSame(propertyTarget, ((Wrapper)((IFacade)propertyFacade).getTarget()).getWrappedObject());
		propertyClosureIterator = specialRootClassFacade.getPropertyClosureIterator();
		assertFalse(propertyClosureIterator.hasNext());
		specialRootClassTarget.addProperty(propertyTarget);
		propertyClosureIterator = specialRootClassFacade.getPropertyClosureIterator();
		assertTrue(propertyClosureIterator.hasNext());
		propertyFacade = propertyClosureIterator.next();
		assertSame(propertyTarget, ((Wrapper)((IFacade)propertyFacade).getTarget()).getWrappedObject());
	}
	
	@Test
	public void testGetSuperClass() {
		assertNull(rootClassFacade.getSuperclass());
		assertSame(rootClassTarget, ((Wrapper)((IFacade)singleTableSubclassFacade.getSuperclass()).getTarget()).getWrappedObject());
		assertSame(rootClassTarget, ((Wrapper)((IFacade)joinedSubclassFacade.getSuperclass()).getTarget()).getWrappedObject());
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
		assertSame(propertyTarget, ((Wrapper)((IFacade)propertyFacade).getTarget()).getWrappedObject());
		propertyIterator = singleTableSubclassFacade.getPropertyIterator();
		assertFalse(propertyIterator.hasNext());
		singleTableSubclassTarget.addProperty(propertyTarget);
		propertyIterator = singleTableSubclassFacade.getPropertyIterator();
		assertTrue(propertyIterator.hasNext());
		propertyFacade = propertyIterator.next();
		assertSame(propertyTarget, ((Wrapper)((IFacade)propertyFacade).getTarget()).getWrappedObject());
		propertyIterator = joinedSubclassFacade.getPropertyIterator();
		assertFalse(propertyIterator.hasNext());
		joinedSubclassTarget.addProperty(propertyTarget);
		propertyIterator = joinedSubclassFacade.getPropertyIterator();
		assertTrue(propertyIterator.hasNext());
		propertyFacade = propertyIterator.next();
		assertSame(propertyTarget, ((Wrapper)((IFacade)propertyFacade).getTarget()).getWrappedObject());
		propertyIterator = specialRootClassFacade.getPropertyIterator();
		assertFalse(propertyIterator.hasNext());
		specialRootClassTarget.addProperty(propertyTarget);
		propertyIterator = specialRootClassFacade.getPropertyIterator();
		assertTrue(propertyIterator.hasNext());
		propertyFacade = propertyIterator.next();
		assertSame(propertyTarget, ((Wrapper)((IFacade)propertyFacade).getTarget()).getWrappedObject());
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
		assertSame(propertyTarget, ((Wrapper)((IFacade)propertyFacade).getTarget()).getWrappedObject());
		propertyFacade = singleTableSubclassFacade.getProperty("foo");
		assertSame(propertyTarget, ((Wrapper)((IFacade)propertyFacade).getTarget()).getWrappedObject());
		propertyFacade = joinedSubclassFacade.getProperty("foo");
		assertSame(propertyTarget, ((Wrapper)((IFacade)propertyFacade).getTarget()).getWrappedObject());
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
		assertSame(propertyTarget, ((Wrapper)((IFacade)propertyFacade).getTarget()).getWrappedObject());
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
		assertSame(propertyTarget, ((Wrapper)((IFacade)propertyFacade).getTarget()).getWrappedObject());
	}
	
	@Test
	public void testGetTable() {
		assertNull(rootClassFacade.getTable());
		assertNull(singleTableSubclassFacade.getTable());
		Table tableTarget = new Table("test");
		((RootClass)rootClassTarget).setTable(tableTarget);
		ITable tableFacade = rootClassFacade.getTable();
		assertSame(tableTarget, ((Wrapper)((IFacade)tableFacade).getTarget()).getWrappedObject());
		tableFacade = singleTableSubclassFacade.getTable();
		assertSame(tableTarget, ((Wrapper)((IFacade)tableFacade).getTarget()).getWrappedObject());
		assertNull(joinedSubclassFacade.getTable());
		((JoinedSubclass)joinedSubclassTarget).setTable(tableTarget);
		tableFacade = joinedSubclassFacade.getTable();
		assertSame(tableTarget, ((Wrapper)((IFacade)tableFacade).getTarget()).getWrappedObject());
		assertNull(specialRootClassFacade.getTable());
		((RootClass)specialRootClassTarget).setTable(tableTarget);
		tableFacade = specialRootClassFacade.getTable();
		assertSame(tableTarget, ((Wrapper)((IFacade)tableFacade).getTarget()).getWrappedObject());
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
		Value v = createValue();
		((RootClass)rootClassTarget).setDiscriminator(v);
		IValue valueFacade = rootClassFacade.getDiscriminator();
		Object valueWrapper = ((IFacade)valueFacade).getTarget();
		assertTrue(valueWrapper instanceof Wrapper);
		assertSame(v, ((Wrapper)valueWrapper).getWrappedObject());
		valueFacade = singleTableSubclassFacade.getDiscriminator();
		valueWrapper = ((IFacade)valueFacade).getTarget();
		assertTrue(valueWrapper instanceof Wrapper);
		assertSame(v, ((Wrapper)valueWrapper).getWrappedObject());
		valueFacade = joinedSubclassFacade.getDiscriminator();
		valueWrapper = ((IFacade)valueFacade).getTarget();
		assertTrue(valueWrapper instanceof Wrapper);
		assertSame(v, ((Wrapper)valueWrapper).getWrappedObject());
		assertNull(specialRootClassFacade.getDiscriminator());
		((RootClass)specialRootClassTarget).setDiscriminator(v);
		valueFacade = specialRootClassFacade.getDiscriminator();
		valueWrapper = ((IFacade)valueFacade).getTarget();
		assertTrue(valueWrapper instanceof Wrapper);
		assertSame(v, ((Wrapper)valueWrapper).getWrappedObject());
	}
	
	@Test
	public void testGetIdentifier() {
		assertNull(rootClassFacade.getIdentifier());
		assertNull(singleTableSubclassFacade.getIdentifier());
		assertNull(joinedSubclassFacade.getIdentifier());
		KeyValue valueTarget = createValue();
		((RootClass)rootClassTarget).setIdentifier(valueTarget);
		IValue valueFacade = rootClassFacade.getIdentifier();
		assertSame(valueTarget, ((Wrapper)((IFacade)valueFacade).getTarget()).getWrappedObject());
		valueFacade = singleTableSubclassFacade.getIdentifier();
		assertSame(valueTarget, ((Wrapper)((IFacade)valueFacade).getTarget()).getWrappedObject());
		valueFacade = joinedSubclassFacade.getIdentifier();
		assertSame(valueTarget, ((Wrapper)((IFacade)valueFacade).getTarget()).getWrappedObject());
		assertNull(specialRootClassFacade.getIdentifier());
		((RootClass)specialRootClassTarget).setIdentifier(valueTarget);
		valueFacade = specialRootClassFacade.getIdentifier();
		assertSame(valueTarget, ((Wrapper)((IFacade)valueFacade).getTarget()).getWrappedObject());
	}
	
	@Test
	public void testGetJoinIterator() {
		Join joinTarget = new Join();
		assertFalse(rootClassFacade.getJoinIterator().hasNext());
		rootClassTarget.addJoin(joinTarget);
		Iterator<IJoin> joinIterator = rootClassFacade.getJoinIterator();
		IJoin joinFacade = joinIterator.next();
		assertSame(((Wrapper)((IFacade)joinFacade).getTarget()).getWrappedObject(), joinTarget);
		assertFalse(singleTableSubclassFacade.getJoinIterator().hasNext());
		singleTableSubclassTarget.addJoin(joinTarget);
		joinIterator = singleTableSubclassFacade.getJoinIterator();
		joinFacade = joinIterator.next();
		assertSame(((Wrapper)((IFacade)joinFacade).getTarget()).getWrappedObject(), joinTarget);
		assertFalse(joinedSubclassFacade.getJoinIterator().hasNext());
		joinedSubclassTarget.addJoin(joinTarget);
		joinIterator = joinedSubclassFacade.getJoinIterator();
		joinFacade = joinIterator.next();
		assertSame(((Wrapper)((IFacade)joinFacade).getTarget()).getWrappedObject(), joinTarget);
		assertFalse(specialRootClassFacade.getJoinIterator().hasNext());
		specialRootClassTarget.addJoin(joinTarget);
		joinIterator = specialRootClassFacade.getJoinIterator();
		joinFacade = joinIterator.next();
		assertSame(((Wrapper)((IFacade)joinFacade).getTarget()).getWrappedObject(), joinTarget);
	}
	
	@Test
	public void testGetVersion() {
		Property versionTarget = new Property();
		assertNull(rootClassFacade.getVersion());
		assertNull(singleTableSubclassFacade.getVersion());
		assertNull(joinedSubclassFacade.getVersion());
		((RootClass)rootClassTarget).setVersion(versionTarget);
		IProperty propertyFacade = rootClassFacade.getVersion();
		assertSame(versionTarget, ((Wrapper)((IFacade)propertyFacade).getTarget()).getWrappedObject());
		propertyFacade = singleTableSubclassFacade.getVersion();
		assertSame(versionTarget, ((Wrapper)((IFacade)propertyFacade).getTarget()).getWrappedObject());
		propertyFacade = joinedSubclassFacade.getVersion();
		assertSame(versionTarget, ((Wrapper)((IFacade)propertyFacade).getTarget()).getWrappedObject());
		assertNull(specialRootClassFacade.getVersion());
		((RootClass)specialRootClassTarget).setVersion(versionTarget);
		propertyFacade = specialRootClassFacade.getVersion();
		assertSame(versionTarget, ((Wrapper)((IFacade)propertyFacade).getTarget()).getWrappedObject());
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
		IProperty firstPropertyFacade = (IProperty)GenericFacadeFactory.createFacade(
				IProperty.class, 
				WrapperFactory.createPropertyWrapper());
		Property firstPropertyTarget = (Property)((Wrapper)((IFacade)firstPropertyFacade).getTarget()).getWrappedObject();
		firstPropertyTarget.setName("foo");
		IProperty secondPropertyFacade = (IProperty)GenericFacadeFactory.createFacade(
				IProperty.class, 
				WrapperFactory.createPropertyWrapper());
		Property secondPropertyTarget = (Property)((Wrapper)((IFacade)secondPropertyFacade).getTarget()).getWrappedObject();
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
		ITable tableFacade = (ITable)GenericFacadeFactory.createFacade(
				ITable.class, 
				WrapperFactory.createTableWrapper(""));
		Table tableTarget = (Table)((Wrapper)((IFacade)tableFacade).getTarget()).getWrappedObject();
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
		IValue valueFacade = (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createSimpleValueWrapper());
		Value valueTarget = (Value)((Wrapper)((IFacade)valueFacade).getTarget()).getWrappedObject();
		try {
			rootClassFacade.setKey(valueFacade);
			fail();
		} catch (RuntimeException e) {
			assertEquals("setKey(Value) is only allowed on JoinedSubclass", e.getMessage());
		}
		try {
			singleTableSubclassFacade.setKey(valueFacade);
			fail();
		} catch (RuntimeException e) {
			assertEquals("setKey(Value) is only allowed on JoinedSubclass", e.getMessage());
		}
		joinedSubclassFacade.setKey(valueFacade);
		assertSame(valueTarget, joinedSubclassTarget.getKey());
		try {
			specialRootClassFacade.setKey(valueFacade);
			fail();
		} catch (RuntimeException e) {
			assertEquals("setKey(Value) is only allowed on JoinedSubclass", e.getMessage());
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
		IValue componentFacade = (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createComponentWrapper(((IFacade)rootClassFacade).getTarget()));
		Component componentTarget = (Component)((Wrapper)((IFacade)componentFacade).getTarget()).getWrappedObject();
		componentTarget.setParentProperty("foo");
		IProperty propertyFacade = (IProperty)GenericFacadeFactory.createFacade(
				IProperty.class, 
				WrapperFactory.createPropertyWrapper());
		propertyFacade.setValue(componentFacade);
		propertyFacade.setPersistentClass(rootClassFacade);
		specialRootClassFacade = (IPersistentClass)GenericFacadeFactory.createFacade(
				IPersistentClass.class, 
				WrapperFactory.createSpecialRootClassWrapper(((IFacade)propertyFacade).getTarget()));
		IProperty parentProperty = specialRootClassFacade.getParentProperty();
		assertNotNull(parentProperty);
		assertEquals("foo", parentProperty.getName());
	}
	
	@Test
	public void testSetIdentifierProperty() {
		IProperty propertyFacade = (IProperty)GenericFacadeFactory.createFacade(
				IProperty.class, 
				WrapperFactory.createPropertyWrapper());
		Property propertyTarget = (Property)((Wrapper)((IFacade)propertyFacade).getTarget()).getWrappedObject();
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
		IValue valueFacade = (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createSimpleValueWrapper());
		Value valueTarget = (Value)((Wrapper)((IFacade)valueFacade).getTarget()).getWrappedObject();
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
			assertEquals("Method 'setIdentifier(Value)' can only be called on RootClass instances", e.getMessage());
		}
		try {
			joinedSubclassFacade.setIdentifier(valueFacade);
			fail();
		} catch (RuntimeException e) {
			assertEquals("Method 'setIdentifier(Value)' can only be called on RootClass instances", e.getMessage());
		}
		assertNull(specialRootClassTarget.getIdentifier());
		specialRootClassFacade.setIdentifier(valueFacade);
		assertSame(valueTarget, specialRootClassTarget.getIdentifier());
	}
	
	@Test
	public void testSetDiscriminator() throws Exception {
		IValue valueFacade = (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createSimpleValueWrapper());
		Value valueTarget = (Value)((Wrapper)((IFacade)valueFacade).getTarget()).getWrappedObject();
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
	
	@Test
	public void testGetSubclassIterator() {
		Iterator<?> subclassIterator = rootClassFacade.getSubclassIterator();
		assertFalse(subclassIterator.hasNext());
		Subclass firstSubclass = new Subclass(rootClassTarget, DummyMetadataBuildingContext.INSTANCE);
		firstSubclass.setEntityName("first");
		rootClassTarget.addSubclass(firstSubclass);
		subclassIterator = rootClassFacade.getSubclassIterator();
		assertTrue(subclassIterator.hasNext());
		assertSame(firstSubclass, ((Wrapper)((IFacade)subclassIterator.next()).getTarget()).getWrappedObject());
		subclassIterator = singleTableSubclassFacade.getSubclassIterator();
		assertFalse(subclassIterator.hasNext());
		Subclass secondSubclass = new Subclass(singleTableSubclassTarget, DummyMetadataBuildingContext.INSTANCE);
		secondSubclass.setEntityName("second");
		singleTableSubclassTarget.addSubclass(secondSubclass);
		subclassIterator = singleTableSubclassFacade.getSubclassIterator();
		assertTrue(subclassIterator.hasNext());
		assertSame(secondSubclass, ((Wrapper)((IFacade)subclassIterator.next()).getTarget()).getWrappedObject());
		subclassIterator = joinedSubclassFacade.getSubclassIterator();
		assertFalse(subclassIterator.hasNext());
		Subclass thirdSubclass = new Subclass(joinedSubclassTarget, DummyMetadataBuildingContext.INSTANCE);
		thirdSubclass.setEntityName("third");
		joinedSubclassTarget.addSubclass(thirdSubclass);
		subclassIterator = joinedSubclassFacade.getSubclassIterator();
		assertTrue(subclassIterator.hasNext());
		assertSame(thirdSubclass, ((Wrapper)((IFacade)subclassIterator.next()).getTarget()).getWrappedObject());
		subclassIterator = specialRootClassFacade.getSubclassIterator();
		assertFalse(subclassIterator.hasNext());
		Subclass fourthSubclass = new Subclass(joinedSubclassTarget, DummyMetadataBuildingContext.INSTANCE);
		fourthSubclass.setEntityName("four");
		specialRootClassTarget.addSubclass(fourthSubclass);
		subclassIterator = specialRootClassFacade.getSubclassIterator();
		assertTrue(subclassIterator.hasNext());
		assertSame(fourthSubclass, ((Wrapper)((IFacade)subclassIterator.next()).getTarget()).getWrappedObject());
	}
	
	@Test
	public void testIsCustomDeleteCallable() {
		rootClassTarget.setCustomSQLDelete("foo", false, null);
		assertFalse(rootClassFacade.isCustomDeleteCallable());
		rootClassTarget.setCustomSQLDelete("bar", true, null);
		assertTrue(rootClassFacade.isCustomDeleteCallable());
		singleTableSubclassTarget.setCustomSQLDelete("foo", false, null);
		assertFalse(singleTableSubclassFacade.isCustomDeleteCallable());
		singleTableSubclassTarget.setCustomSQLDelete("bar", true, null);
		assertTrue(singleTableSubclassFacade.isCustomDeleteCallable());
		joinedSubclassTarget.setCustomSQLDelete("foo", false, null);
		assertFalse(joinedSubclassFacade.isCustomDeleteCallable());
		joinedSubclassTarget.setCustomSQLDelete("bar", true, null);
		assertTrue(joinedSubclassFacade.isCustomDeleteCallable());
		specialRootClassTarget.setCustomSQLDelete("foo", false, null);
		assertFalse(specialRootClassFacade.isCustomDeleteCallable());
		specialRootClassTarget.setCustomSQLDelete("bar", true, null);
		assertTrue(specialRootClassFacade.isCustomDeleteCallable());
	}
	
	@Test
	public void testIsCustomInsertCallable() {
		rootClassTarget.setCustomSQLInsert("bar", false, null);
		assertFalse(rootClassFacade.isCustomInsertCallable());
		rootClassTarget.setCustomSQLInsert("foo", true, null);
		assertTrue(rootClassFacade.isCustomInsertCallable());
		singleTableSubclassTarget.setCustomSQLInsert("foo", false, null);
		assertFalse(singleTableSubclassFacade.isCustomInsertCallable());
		singleTableSubclassTarget.setCustomSQLInsert("bar", true, null);
		assertTrue(singleTableSubclassFacade.isCustomInsertCallable());
		joinedSubclassTarget.setCustomSQLInsert("foo", false, null);
		assertFalse(joinedSubclassFacade.isCustomInsertCallable());
		joinedSubclassTarget.setCustomSQLInsert("bar", true, null);
		assertTrue(joinedSubclassFacade.isCustomInsertCallable());
		specialRootClassTarget.setCustomSQLInsert("foo", false, null);
		assertFalse(specialRootClassFacade.isCustomInsertCallable());
		specialRootClassTarget.setCustomSQLInsert("bar", true, null);
		assertTrue(specialRootClassFacade.isCustomInsertCallable());
	}
	
	@Test
	public void testIsCustomUpdateCallable() {
		rootClassTarget.setCustomSQLUpdate("foo", false, null);
		assertFalse(rootClassFacade.isCustomUpdateCallable());
		rootClassTarget.setCustomSQLUpdate("bar", true, null);
		assertTrue(rootClassFacade.isCustomUpdateCallable());
		singleTableSubclassTarget.setCustomSQLUpdate("foo", false, null);
		assertFalse(singleTableSubclassFacade.isCustomUpdateCallable());
		singleTableSubclassTarget.setCustomSQLUpdate("bar", true, null);
		assertTrue(singleTableSubclassFacade.isCustomUpdateCallable());
		joinedSubclassTarget.setCustomSQLUpdate("foo", false, null);
		assertFalse(joinedSubclassFacade.isCustomUpdateCallable());
		joinedSubclassTarget.setCustomSQLUpdate("bar", true, null);
		assertTrue(joinedSubclassFacade.isCustomUpdateCallable());
		specialRootClassTarget.setCustomSQLUpdate("foo", false, null);
		assertFalse(specialRootClassFacade.isCustomUpdateCallable());
		specialRootClassTarget.setCustomSQLUpdate("bar", true, null);
		assertTrue(specialRootClassFacade.isCustomUpdateCallable());
	}
	
	@Test
	public void testIsDiscriminatorValueInsertable() {
		assertTrue(rootClassFacade.isDiscriminatorInsertable());
		assertTrue(singleTableSubclassFacade.isDiscriminatorInsertable());
		assertTrue(joinedSubclassFacade.isDiscriminatorInsertable());
		assertTrue(specialRootClassFacade.isDiscriminatorInsertable());		
		((RootClass)rootClassTarget).setDiscriminatorInsertable(false);
		assertFalse(rootClassFacade.isDiscriminatorInsertable());
		assertFalse(singleTableSubclassFacade.isDiscriminatorInsertable());
		assertFalse(joinedSubclassFacade.isDiscriminatorInsertable());
		assertTrue(specialRootClassFacade.isDiscriminatorInsertable());		
		((RootClass)specialRootClassTarget).setDiscriminatorInsertable(false);
		assertFalse(specialRootClassFacade.isDiscriminatorInsertable());
	}
	
	@Test
	public void testIsDiscriminatorValueNotNull() {
		rootClassTarget.setDiscriminatorValue("null");
		assertFalse(rootClassFacade.isDiscriminatorValueNotNull());
		rootClassTarget.setDiscriminatorValue("not null");
		assertTrue(rootClassFacade.isDiscriminatorValueNotNull());
		singleTableSubclassTarget.setDiscriminatorValue("null");
		assertFalse(singleTableSubclassFacade.isDiscriminatorValueNotNull());
		singleTableSubclassTarget.setDiscriminatorValue("not null");
		assertTrue(singleTableSubclassFacade.isDiscriminatorValueNotNull());
		joinedSubclassTarget.setDiscriminatorValue("null");
		assertFalse(joinedSubclassFacade.isDiscriminatorValueNotNull());
		joinedSubclassTarget.setDiscriminatorValue("not null");
		assertTrue(joinedSubclassFacade.isDiscriminatorValueNotNull());
		specialRootClassTarget.setDiscriminatorValue("null");
		assertFalse(specialRootClassFacade.isDiscriminatorValueNotNull());
		specialRootClassTarget.setDiscriminatorValue("not null");
		assertTrue(specialRootClassFacade.isDiscriminatorValueNotNull());
	}
	
	@Test
	public void testIsDiscriminatorValueNull() {
		rootClassTarget.setDiscriminatorValue("not null");
		assertFalse(rootClassFacade.isDiscriminatorValueNull());
		rootClassTarget.setDiscriminatorValue("null");
		assertTrue(rootClassFacade.isDiscriminatorValueNull());
		singleTableSubclassTarget.setDiscriminatorValue("not null");
		assertFalse(singleTableSubclassFacade.isDiscriminatorValueNull());
		singleTableSubclassTarget.setDiscriminatorValue("null");
		assertTrue(singleTableSubclassFacade.isDiscriminatorValueNull());
		joinedSubclassTarget.setDiscriminatorValue("not null");
		assertFalse(joinedSubclassFacade.isDiscriminatorValueNull());
		joinedSubclassTarget.setDiscriminatorValue("null");
		assertTrue(joinedSubclassFacade.isDiscriminatorValueNull());
		specialRootClassTarget.setDiscriminatorValue("not null");
		assertFalse(specialRootClassFacade.isDiscriminatorValueNull());
		specialRootClassTarget.setDiscriminatorValue("null");
		assertTrue(specialRootClassFacade.isDiscriminatorValueNull());
	}
	
	@Test
	public void testIsExplicitPolymorphism() {
		assertFalse(rootClassFacade.isExplicitPolymorphism());
		assertFalse(singleTableSubclassFacade.isExplicitPolymorphism());
		assertFalse(joinedSubclassFacade.isExplicitPolymorphism());
		((RootClass)rootClassTarget).setExplicitPolymorphism(true);
		assertTrue(rootClassFacade.isExplicitPolymorphism());
		assertTrue(singleTableSubclassFacade.isExplicitPolymorphism());
		assertTrue(joinedSubclassFacade.isExplicitPolymorphism());
		assertFalse(specialRootClassFacade.isExplicitPolymorphism());
		((RootClass)specialRootClassTarget).setExplicitPolymorphism(true);
		assertTrue(specialRootClassFacade.isExplicitPolymorphism());
	}
	
	@Test
	public void testIsForceDiscriminator() {
		assertFalse(rootClassFacade.isForceDiscriminator());
		assertFalse(singleTableSubclassFacade.isForceDiscriminator());
		assertFalse(joinedSubclassFacade.isForceDiscriminator());
		((RootClass)rootClassTarget).setForceDiscriminator(true);
		assertTrue(rootClassFacade.isForceDiscriminator());
		assertTrue(singleTableSubclassFacade.isForceDiscriminator());
		assertTrue(joinedSubclassFacade.isForceDiscriminator());
		assertFalse(specialRootClassFacade.isForceDiscriminator());
		((RootClass)specialRootClassTarget).setForceDiscriminator(true);
		assertTrue(specialRootClassFacade.isForceDiscriminator());
	}
	
	@Test
	public void testIsInherited() {
		assertFalse(rootClassFacade.isInherited());
		assertTrue(singleTableSubclassFacade.isInherited());
		assertTrue(joinedSubclassFacade.isInherited());
		assertFalse(specialRootClassFacade.isInherited());
	}
	
	@Test
	public void testIsJoinedSubclass() {
		((RootClass)rootClassTarget).setTable(new Table("foo"));
		((JoinedSubclass)joinedSubclassTarget).setTable(new Table("bar"));
		assertFalse(rootClassFacade.isJoinedSubclass());
		assertFalse(singleTableSubclassFacade.isJoinedSubclass());
		assertTrue(joinedSubclassFacade.isJoinedSubclass());
		assertFalse(specialRootClassFacade.isJoinedSubclass());
	}
	
	@Test
	public void testIsLazy() {
		rootClassTarget.setLazy(true);
		assertTrue(rootClassFacade.isLazy());
		rootClassTarget.setLazy(false);
		assertFalse(rootClassFacade.isLazy());
		singleTableSubclassTarget.setLazy(true);
		assertTrue(singleTableSubclassFacade.isLazy());
		singleTableSubclassTarget.setLazy(false);
		assertFalse(singleTableSubclassFacade.isLazy());
		joinedSubclassTarget.setLazy(true);
		assertTrue(joinedSubclassFacade.isLazy());
		joinedSubclassTarget.setLazy(false);
		assertFalse(joinedSubclassFacade.isLazy());
		specialRootClassTarget.setLazy(true);
		assertTrue(specialRootClassFacade.isLazy());
		specialRootClassTarget.setLazy(false);
		assertFalse(specialRootClassFacade.isLazy());
	}
	
	@Test
	public void testIsLazyPropertiesCacheable() {
		((RootClass)rootClassTarget).setLazyPropertiesCacheable(true);
		assertTrue(rootClassFacade.isLazyPropertiesCacheable());
		((RootClass)rootClassTarget).setLazyPropertiesCacheable(false);
		assertFalse(rootClassFacade.isLazyPropertiesCacheable());
		((RootClass)specialRootClassTarget).setLazyPropertiesCacheable(true);
		assertTrue(specialRootClassFacade.isLazyPropertiesCacheable());
		((RootClass)specialRootClassTarget).setLazyPropertiesCacheable(false);
		assertFalse(specialRootClassFacade.isLazyPropertiesCacheable());
		try {
			singleTableSubclassFacade.isLazyPropertiesCacheable();
			fail();
		} catch (RuntimeException e) {
			assertEquals("Method 'isLazyPropertiesCacheable()' can only be called on RootClass instances", e.getMessage());
		}
		try {
			joinedSubclassFacade.isLazyPropertiesCacheable();
			fail();
		} catch (RuntimeException e) {
			assertEquals("Method 'isLazyPropertiesCacheable()' can only be called on RootClass instances", e.getMessage());
		}
	}
	
	@Test
	public void testIsMutable() {
		assertTrue(rootClassFacade.isMutable());
		assertTrue(singleTableSubclassFacade.isMutable());
		assertTrue(joinedSubclassFacade.isMutable());
		((RootClass)rootClassTarget).setMutable(false);
		assertFalse(rootClassFacade.isMutable());
		assertFalse(singleTableSubclassFacade.isMutable());
		assertFalse(joinedSubclassFacade.isMutable());
		assertTrue(specialRootClassFacade.isMutable());
		((RootClass)specialRootClassTarget).setMutable(false);
		assertFalse(specialRootClassFacade.isMutable());
	}
	
	@Test
	public void testIsPolymorphic() {
		assertFalse(rootClassFacade.isPolymorphic());
		assertTrue(singleTableSubclassFacade.isPolymorphic());
		assertTrue(joinedSubclassFacade.isPolymorphic());
		assertFalse(specialRootClassFacade.isPolymorphic());
		((RootClass)rootClassTarget).setPolymorphic(true);
		assertTrue(rootClassFacade.isPolymorphic());
		((RootClass)specialRootClassTarget).setPolymorphic(true);
		assertTrue(specialRootClassFacade.isPolymorphic());
	}
	
	@Test
	public void testIsVersioned() {
		assertFalse(rootClassFacade.isVersioned());
		assertFalse(singleTableSubclassFacade.isVersioned());
		assertFalse(joinedSubclassFacade.isVersioned());
		((RootClass)rootClassTarget).setVersion(new Property());
		assertTrue(rootClassFacade.isVersioned());
		assertTrue(singleTableSubclassFacade.isVersioned());
		assertTrue(joinedSubclassFacade.isVersioned());
		assertFalse(specialRootClassFacade.isVersioned());
		((RootClass)specialRootClassTarget).setVersion(new Property());
		assertTrue(specialRootClassFacade.isVersioned());
	}
	
	@Test
	public void testGetBatchSize() {
		rootClassTarget.setBatchSize(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, rootClassFacade.getBatchSize());
		rootClassTarget.setBatchSize(Integer.MIN_VALUE);
		assertEquals(Integer.MIN_VALUE, rootClassFacade.getBatchSize());
		singleTableSubclassTarget.setBatchSize(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, singleTableSubclassFacade.getBatchSize());
		singleTableSubclassTarget.setBatchSize(Integer.MIN_VALUE);
		assertEquals(Integer.MIN_VALUE, singleTableSubclassFacade.getBatchSize());
		joinedSubclassTarget.setBatchSize(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, joinedSubclassFacade.getBatchSize());
		joinedSubclassTarget.setBatchSize(Integer.MIN_VALUE);
		assertEquals(Integer.MIN_VALUE, joinedSubclassFacade.getBatchSize());
		specialRootClassTarget.setBatchSize(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, specialRootClassFacade.getBatchSize());
		specialRootClassTarget.setBatchSize(Integer.MIN_VALUE);
		assertEquals(Integer.MIN_VALUE, specialRootClassFacade.getBatchSize());
	}
	
	@Test
	public void testGetCacheConcurrencyStrategy() {
		assertNull(rootClassFacade.getCacheConcurrencyStrategy());
		assertNull(singleTableSubclassFacade.getCacheConcurrencyStrategy());
		assertNull(joinedSubclassFacade.getCacheConcurrencyStrategy());
		((RootClass)rootClassTarget).setCacheConcurrencyStrategy("foo");
		assertEquals("foo", rootClassFacade.getCacheConcurrencyStrategy());
		assertEquals("foo", singleTableSubclassFacade.getCacheConcurrencyStrategy());
		assertEquals("foo", joinedSubclassFacade.getCacheConcurrencyStrategy());
		assertNull(specialRootClassFacade.getCacheConcurrencyStrategy());
		((RootClass)specialRootClassTarget).setCacheConcurrencyStrategy("bar");
		assertEquals("bar", specialRootClassFacade.getCacheConcurrencyStrategy());
	}
	
	@Test
	public void testGetCustomSQLDelete() {
		assertNull(rootClassFacade.getCustomSQLDelete());
		rootClassTarget.setCustomSQLDelete("foo", false, null);
		assertEquals("foo", rootClassFacade.getCustomSQLDelete());
		assertNull(singleTableSubclassFacade.getCustomSQLDelete());
		singleTableSubclassTarget.setCustomSQLDelete("bar", false, null);
		assertEquals("bar", singleTableSubclassFacade.getCustomSQLDelete());
		assertNull(joinedSubclassFacade.getCustomSQLDelete());
		joinedSubclassTarget.setCustomSQLDelete("oof", false, null);
		assertEquals("oof", joinedSubclassFacade.getCustomSQLDelete());
		assertNull(specialRootClassFacade.getCustomSQLDelete());
		specialRootClassTarget.setCustomSQLDelete("rab", false, null);
		assertEquals("rab", specialRootClassFacade.getCustomSQLDelete());
	}
	
	@Test
	public void testGetCustomSQLInsert() {
		assertNull(rootClassFacade.getCustomSQLInsert());
		rootClassTarget.setCustomSQLInsert("foo", false, null);
		assertEquals("foo", rootClassFacade.getCustomSQLInsert());
		assertNull(singleTableSubclassFacade.getCustomSQLInsert());
		singleTableSubclassTarget.setCustomSQLInsert("bar", false, null);
		assertEquals("bar", singleTableSubclassFacade.getCustomSQLInsert());
		assertNull(joinedSubclassFacade.getCustomSQLInsert());
		joinedSubclassTarget.setCustomSQLInsert("oof", false, null);
		assertEquals("oof", joinedSubclassFacade.getCustomSQLInsert());
		assertNull(specialRootClassFacade.getCustomSQLInsert());
		specialRootClassTarget.setCustomSQLInsert("rab", false, null);
		assertEquals("rab", specialRootClassFacade.getCustomSQLInsert());
	}
	
	@Test
	public void testGetCustomSQLUpdate() {
		assertNull(rootClassFacade.getCustomSQLUpdate());
		rootClassTarget.setCustomSQLUpdate("foo", false, null);
		assertEquals("foo", rootClassFacade.getCustomSQLUpdate());
		assertNull(singleTableSubclassFacade.getCustomSQLUpdate());
		singleTableSubclassTarget.setCustomSQLUpdate("bar", false, null);
		assertEquals("bar", singleTableSubclassFacade.getCustomSQLUpdate());
		assertNull(joinedSubclassFacade.getCustomSQLUpdate());
		joinedSubclassTarget.setCustomSQLUpdate("oof", false, null);
		assertEquals("oof", joinedSubclassFacade.getCustomSQLUpdate());
		assertNull(specialRootClassFacade.getCustomSQLUpdate());
		specialRootClassTarget.setCustomSQLUpdate("rab", false, null);
		assertEquals("rab", specialRootClassFacade.getCustomSQLUpdate());
	}
	
	@Test
	public void testGetDiscriminatorValue() {
		assertNull(rootClassFacade.getDiscriminatorValue());
		rootClassTarget.setDiscriminatorValue("foo");
		assertEquals("foo", rootClassFacade.getDiscriminatorValue());
		assertNull(singleTableSubclassFacade.getDiscriminatorValue());
		singleTableSubclassTarget.setDiscriminatorValue("bar");
		assertEquals("bar", singleTableSubclassFacade.getDiscriminatorValue());
		assertNull(joinedSubclassFacade.getDiscriminatorValue());
		joinedSubclassTarget.setDiscriminatorValue("oof");
		assertEquals("oof", joinedSubclassFacade.getDiscriminatorValue());
		assertNull(specialRootClassFacade.getDiscriminatorValue());
		specialRootClassTarget.setDiscriminatorValue("rab");
		assertEquals("rab", specialRootClassFacade.getDiscriminatorValue());
	}
	
	@Test
	public void testGetLoaderName() {
		assertNull(rootClassFacade.getLoaderName());
		rootClassTarget.setLoaderName("foo");
		assertEquals("foo", rootClassFacade.getLoaderName());
		assertNull(singleTableSubclassFacade.getLoaderName());
		singleTableSubclassTarget.setLoaderName("bar");
		assertEquals("bar", singleTableSubclassFacade.getLoaderName());
		assertNull(joinedSubclassFacade.getLoaderName());
		joinedSubclassTarget.setLoaderName("oof");
		assertEquals("oof", joinedSubclassFacade.getLoaderName());
		assertNull(specialRootClassFacade.getLoaderName());
		specialRootClassTarget.setLoaderName("rab");
		assertEquals("rab", specialRootClassFacade.getLoaderName());
	}
	
	@Test
	public void testGetOptimisticLockMode() {
		rootClassTarget.setOptimisticLockStyle(OptimisticLockStyle.NONE);
		assertEquals(-1, rootClassFacade.getOptimisticLockMode());
		assertEquals(-1, singleTableSubclassFacade.getOptimisticLockMode());
		assertEquals(-1, joinedSubclassFacade.getOptimisticLockMode());
		specialRootClassTarget.setOptimisticLockStyle(OptimisticLockStyle.VERSION);
		assertEquals(0, specialRootClassFacade.getOptimisticLockMode());
	}
	
	@Test
	public void testGetWhere() {
		assertNull(rootClassFacade.getWhere());
		assertNull(singleTableSubclassFacade.getWhere());
		assertNull(joinedSubclassFacade.getWhere());
		((RootClass)rootClassTarget).setWhere("foo");
		assertEquals("foo", rootClassFacade.getWhere());
		assertEquals("foo", singleTableSubclassFacade.getWhere());
		assertEquals("foo", joinedSubclassFacade.getWhere());
		assertNull(specialRootClassFacade.getWhere());
		((RootClass)specialRootClassTarget).setWhere("bar");
		assertEquals("bar", specialRootClassFacade.getWhere());
}
	
	@Test
	public void testGetRootTable() throws Exception {
		Table tableTarget = new Table("");
		assertNull(rootClassFacade.getRootTable());
		assertNull(singleTableSubclassFacade.getRootTable());
		assertNull(joinedSubclassFacade.getRootTable());
		((RootClass)rootClassTarget).setTable(tableTarget);
		assertSame(tableTarget, ((Wrapper)((IFacade)rootClassFacade.getRootTable()).getTarget()).getWrappedObject());
		assertSame(tableTarget, ((Wrapper)((IFacade)singleTableSubclassFacade.getRootTable()).getTarget()).getWrappedObject());
		assertSame(tableTarget, ((Wrapper)((IFacade)joinedSubclassFacade.getRootTable()).getTarget()).getWrappedObject());
		assertNull(specialRootClassFacade.getRootTable());
		((RootClass)specialRootClassTarget).setTable(tableTarget);
		assertSame(tableTarget, ((Wrapper)((IFacade)specialRootClassFacade.getRootTable()).getTarget()).getWrappedObject());
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
