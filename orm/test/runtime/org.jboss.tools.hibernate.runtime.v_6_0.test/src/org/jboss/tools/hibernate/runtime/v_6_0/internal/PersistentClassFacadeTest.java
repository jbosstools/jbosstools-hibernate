package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.hibernate.engine.OptimisticLockStyle;
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
import org.jboss.tools.hibernate.runtime.common.AbstractPersistentClassFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IJoin;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.util.DummyMetadataBuildingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class PersistentClassFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private IPersistentClass persistentClassFacade = null; 
	private PersistentClass persistentClassTarget = null;
	
	@BeforeEach
	public void beforeEach() {
		persistentClassTarget = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		persistentClassFacade = new PersistentClassFacadeImpl(FACADE_FACTORY, persistentClassTarget);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(persistentClassFacade);
		assertTrue(persistentClassFacade instanceof PersistentClassFacadeImpl);
		assertSame(persistentClassTarget, ((IFacade)persistentClassFacade).getTarget());
	}
	
	@Test
	public void testGetClassName() {
		assertNotEquals("Foo", persistentClassFacade.getClassName());
		persistentClassTarget.setClassName("Foo");
		assertEquals("Foo", persistentClassFacade.getClassName());
	}
	
	@Test
	public void testGetEntityName() {
		assertNotEquals("Foo", persistentClassFacade.getEntityName());
		persistentClassTarget.setEntityName("Foo");
		assertEquals("Foo", persistentClassFacade.getEntityName());
	}
	
	@Test
	public void testIsAssignableToRootClass() {
		persistentClassTarget = 
				new SingleTableSubclass(
						new RootClass(DummyMetadataBuildingContext.INSTANCE), 
						DummyMetadataBuildingContext.INSTANCE);
		persistentClassFacade = new PersistentClassFacadeImpl(FACADE_FACTORY, persistentClassTarget);
		assertFalse(persistentClassFacade.isAssignableToRootClass());
		persistentClassTarget = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		persistentClassFacade = new PersistentClassFacadeImpl(FACADE_FACTORY, persistentClassTarget);
		assertTrue(persistentClassFacade.isAssignableToRootClass());
	}
	
	@Test
	public void testIsRootClass() {
		persistentClassTarget = 
				new SingleTableSubclass(
						new RootClass(DummyMetadataBuildingContext.INSTANCE), 
						DummyMetadataBuildingContext.INSTANCE);
		persistentClassFacade = new PersistentClassFacadeImpl(FACADE_FACTORY, persistentClassTarget);
		assertFalse(persistentClassFacade.isRootClass());
		persistentClassTarget = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		persistentClassFacade = new PersistentClassFacadeImpl(FACADE_FACTORY, persistentClassTarget);
		assertTrue(persistentClassFacade.isRootClass());
	}
	
	@Test
	public void testGetIdentifierProperty() throws Exception {
		Field field = AbstractPersistentClassFacade.class.getDeclaredField("identifierProperty");
		field.setAccessible(true);
		assertNull(field.get(persistentClassFacade));
		Property propertyTarget = new Property();
		assertNull(persistentClassFacade.getIdentifierProperty());
		((RootClass)persistentClassTarget).setIdentifierProperty(propertyTarget);
		IProperty propertyFacade = persistentClassFacade.getIdentifierProperty();
		assertNotNull(propertyFacade);
		assertSame(propertyTarget, ((IFacade)propertyFacade).getTarget());
		assertSame(propertyFacade, field.get(persistentClassFacade));
	}
	
	@Test
	public void testHasIdentifierProperty() {
		assertFalse(persistentClassFacade.hasIdentifierProperty());
		((RootClass)persistentClassTarget).setIdentifierProperty(new Property());
		assertTrue(persistentClassFacade.hasIdentifierProperty());
	}
	
	@Test
	public void testIsInstanceOfRootClass() {
		assertTrue(persistentClassFacade.isInstanceOfRootClass());
		PersistentClass subClassTarget = new Subclass(persistentClassTarget, DummyMetadataBuildingContext.INSTANCE);
		IPersistentClass subClassFacade = new PersistentClassFacadeImpl(FACADE_FACTORY, subClassTarget);
		assertFalse(subClassFacade.isInstanceOfRootClass());
	}
	
	@Test
	public void testIsInstanceOfSubclass() {
		assertFalse(persistentClassFacade.isInstanceOfSubclass());
		PersistentClass subClassTarget = new Subclass(persistentClassTarget, DummyMetadataBuildingContext.INSTANCE);
		IPersistentClass subClassFacade = new PersistentClassFacadeImpl(FACADE_FACTORY, subClassTarget);
		assertTrue(subClassFacade.isInstanceOfSubclass());
	}
	
	@Test
	public void testGetRootClass() throws Exception {
		Field field = AbstractPersistentClassFacade.class.getDeclaredField("rootClass");
		field.setAccessible(true);
		assertNull(field.get(persistentClassFacade));
		IPersistentClass rootFacade = persistentClassFacade.getRootClass();
		assertNotNull(rootFacade);
		assertSame(rootFacade, field.get(persistentClassFacade));
		assertSame(((IFacade)rootFacade).getTarget(), persistentClassTarget);
	}
	
	@Test
	public void testGetPropertyClosureIterator() throws Exception {
		Property propertyTarget = new Property();
		PersistentClass persistentClass = new RootClass(DummyMetadataBuildingContext.INSTANCE) {
			private static final long serialVersionUID = 1L;
			@Override
			public Iterator<Property> getPropertyClosureIterator() {
				HashSet<Property> set = new HashSet<Property>();
				set.add(propertyTarget);
				return set.iterator();
			}
		};
		persistentClassFacade = new PersistentClassFacadeImpl(FACADE_FACTORY, persistentClass);
		Field field = AbstractPersistentClassFacade.class.getDeclaredField("propertyClosures");
		field.setAccessible(true);
		assertNull(field.get(persistentClassFacade));
		Iterator<IProperty> iterator = persistentClassFacade.getPropertyClosureIterator();
		assertNotNull(field.get(persistentClassFacade));
		assertTrue(iterator.hasNext());
		assertSame(propertyTarget, ((IFacade)iterator.next()).getTarget());
	}
	
	@Test
	public void testGetSuperclass() throws Exception {
		Field field = AbstractPersistentClassFacade.class.getDeclaredField("superClass");
		field.setAccessible(true);
		assertNull(field.get(persistentClassFacade));
		IPersistentClass superclassFacade = persistentClassFacade.getSuperclass();
		assertNull(field.get(persistentClassFacade));
		assertNull(superclassFacade);
		Subclass subclassTarget = new Subclass(persistentClassTarget, DummyMetadataBuildingContext.INSTANCE);
		IPersistentClass subclassFacade = new PersistentClassFacadeImpl(FACADE_FACTORY, subclassTarget);
		assertNull(field.get(subclassFacade));
		superclassFacade = subclassFacade.getSuperclass();
		assertNotNull(superclassFacade);
		assertSame(superclassFacade, field.get(subclassFacade));
		assertSame(persistentClassTarget, ((IFacade)superclassFacade).getTarget());
	}
	
	@Test
	public void testGetPropertyIterator() throws Exception {
		Property propertyTarget = new Property();
		propertyTarget.setName("foo");
		persistentClassTarget.addProperty(propertyTarget);
		Field field = AbstractPersistentClassFacade.class.getDeclaredField("properties");
		field.setAccessible(true);
		assertNull(field.get(persistentClassFacade));
		Iterator<IProperty> propertyIterator = persistentClassFacade.getPropertyIterator();
		assertNotNull(field.get(persistentClassFacade));
		assertTrue(propertyIterator.hasNext());
		assertSame(propertyTarget, ((IFacade)propertyIterator.next()).getTarget());
	}
	
	@Test
	public void testGetProperty() throws Exception {
		Property propertyTarget = new Property();
		propertyTarget.setName("foo");
		persistentClassTarget.addProperty(propertyTarget);
		Field field = AbstractPersistentClassFacade.class.getDeclaredField("properties");
		field.setAccessible(true);
		assertNull(field.get(persistentClassFacade));
		assertNull(persistentClassFacade.getProperty("bar"));
		assertNotNull(field.get(persistentClassFacade));
		assertSame(propertyTarget, ((IFacade)persistentClassFacade.getProperty("foo")).getTarget());
		try {
			persistentClassFacade.getProperty();
			fail();
		} catch (RuntimeException e) {
			assertEquals("getProperty() is only allowed on SpecialRootClass", e.getMessage());
		}
	}
	
	@Test
	public void testGetTable() throws Exception {
		Table table = new Table();
		((RootClass)persistentClassTarget).setTable(table);
		Field field = AbstractPersistentClassFacade.class.getDeclaredField("table");
		field.setAccessible(true);
		assertNull(field.get(persistentClassFacade));
		assertSame(table, ((IFacade)persistentClassFacade.getTable()).getTarget());
		assertNotNull(field.get(persistentClassFacade));
	}
	
	@Test 
	public void testIsAbstract() {
		persistentClassTarget.setAbstract(true);
		assertTrue(persistentClassFacade.isAbstract());
		persistentClassTarget.setAbstract(false);
		assertFalse(persistentClassFacade.isAbstract());
	}
	
	@Test
	public void testGetDiscriminator() throws Exception {
		Value valueTarget = createValue();
		Field field = AbstractPersistentClassFacade.class.getDeclaredField("discriminator");
		field.setAccessible(true);
		assertNull(field.get(persistentClassFacade));
		assertNull(persistentClassFacade.getDiscriminator());
		assertNull(field.get(persistentClassFacade));
		((RootClass)persistentClassTarget).setDiscriminator(valueTarget);
		IValue valueFacade = persistentClassFacade.getDiscriminator();
		assertNotNull(valueFacade);
		assertSame(valueFacade, field.get(persistentClassFacade));
		assertSame(valueTarget, ((IFacade)valueFacade).getTarget());
	}
	
	@Test
	public void testGetIdentifier() throws Exception {
		KeyValue valueTarget = createValue();
		Field field = AbstractPersistentClassFacade.class.getDeclaredField("identifier");
		field.setAccessible(true);
		assertNull(field.get(persistentClassFacade));
		assertNull(persistentClassFacade.getIdentifier());
		assertNull(field.get(persistentClassFacade));
		((RootClass)persistentClassTarget).setIdentifier(valueTarget);
		IValue valueFacade = persistentClassFacade.getIdentifier();
		assertNotNull(valueFacade);
		assertSame(valueFacade, field.get(persistentClassFacade));
		assertSame(valueTarget, ((IFacade)valueFacade).getTarget());
	}
	
	@Test
	public void testGetJoinIterator() throws Exception {
		Join join = new Join();
		Field field = AbstractPersistentClassFacade.class.getDeclaredField("joins");
		field.setAccessible(true);
		assertNull(field.get(persistentClassFacade));
		Iterator<IJoin> joinIterator = persistentClassFacade.getJoinIterator();
		assertNotNull(field.get(persistentClassFacade));
		assertTrue(((HashSet<?>)field.get(persistentClassFacade)).isEmpty());
		assertFalse(joinIterator.hasNext());
		field.set(persistentClassFacade, null);
		((RootClass)persistentClassTarget).addJoin(join);
		joinIterator = persistentClassFacade.getJoinIterator();
		assertTrue(joinIterator.hasNext());
		assertSame(join, ((IFacade)joinIterator.next()).getTarget());
		assertFalse(joinIterator.hasNext());
	}
	
	@Test
	public void testGetVersion() throws Exception {
		assertNull(persistentClassFacade.getVersion());
		Property versionTarget = new Property();
		Field field = AbstractPersistentClassFacade.class.getDeclaredField("version");
		field.setAccessible(true);
		assertNull(field.get(persistentClassFacade));
		((RootClass)persistentClassTarget).setVersion(versionTarget);
		IProperty versionFacade = persistentClassFacade.getVersion();
		assertNotNull(versionFacade);
		assertSame(versionFacade, field.get(persistentClassFacade));
		assertSame(versionTarget, ((IFacade)versionFacade).getTarget());
	}
	
	@Test
	public void testSetClassName() {
		assertNull(persistentClassTarget.getClassName());
		persistentClassFacade.setClassName("foo");
		assertEquals("foo", persistentClassTarget.getClassName());
	}
	
	@Test
	public void testSetEntityName() {
		assertNull(persistentClassTarget.getEntityName());
		persistentClassFacade.setEntityName("bar");
		assertEquals("bar", persistentClassTarget.getEntityName());
	}
	
	@Test
	public void testSetDiscriminatorValue() {
		assertNull(persistentClassTarget.getDiscriminatorValue());
		persistentClassFacade.setDiscriminatorValue("foo");
		assertEquals("foo", persistentClassTarget.getDiscriminatorValue());
	}
	
	@Test
	public void testSetAbstract() {
		persistentClassFacade.setAbstract(true);
		assertTrue(persistentClassTarget.isAbstract());
		persistentClassFacade.setAbstract(false);
		assertFalse(persistentClassTarget.isAbstract());
	}
	
	@Test
	public void testAddProperty() throws Exception {
		Field propertiesField = AbstractPersistentClassFacade.class.getDeclaredField("properties");
		propertiesField.setAccessible(true);
		propertiesField.set(persistentClassFacade, new HashMap<String, IProperty>());
		Field propertyClosuresField = AbstractPersistentClassFacade.class.getDeclaredField("propertyClosures");
		propertyClosuresField.setAccessible(true);
		propertyClosuresField.set(persistentClassFacade, new HashSet<IProperty>());
		Property propertyTarget = new Property();
		IProperty propertyFacade = FACADE_FACTORY.createProperty(propertyTarget);
		assertFalse(persistentClassTarget.getPropertyIterator().hasNext());
		assertNull(propertyTarget.getPersistentClass());
		persistentClassFacade.addProperty(propertyFacade);
		assertNull(propertiesField.get(persistentClassFacade));
		assertNull(propertyClosuresField.get(persistentClassFacade));
		assertTrue(persistentClassTarget.getPropertyIterator().hasNext());
		assertSame(propertyTarget, persistentClassTarget.getPropertyIterator().next());
		assertSame(persistentClassTarget, propertyTarget.getPersistentClass());
	}
	
	@Test
	public void testIsInstanceOfJoinedSubclass() {
		assertFalse(persistentClassFacade.isInstanceOfJoinedSubclass());
		JoinedSubclass joinedSubclassTarget = 
				new JoinedSubclass(persistentClassTarget, DummyMetadataBuildingContext.INSTANCE);
		IPersistentClass joinedSubclassFacade = new PersistentClassFacadeImpl(FACADE_FACTORY, joinedSubclassTarget);
		assertTrue(joinedSubclassFacade.isInstanceOfJoinedSubclass());
	}
	
	@Test
	public void testSetTable() {
		Table tableTarget = new Table();
		ITable tableFacade = FACADE_FACTORY.createTable(tableTarget);
		assertNull(persistentClassTarget.getTable());
		persistentClassFacade.setTable(tableFacade);
		assertSame(tableTarget, persistentClassTarget.getTable());
	}
	
	@Test
	public void testSetKey() {
		Value valueTarget = createValue();
		IValue valueFacade = FACADE_FACTORY.createValue(valueTarget);
		assertNull(persistentClassTarget.getKey());
		persistentClassFacade.setKey(valueFacade);
		assertSame(valueTarget, persistentClassTarget.getKey());
	}
	
	@Test
	public void testIsInstanceOfSpecialRootClass() {
		assertFalse(persistentClassFacade.isInstanceOfSpecialRootClass());
	}
	
	@Test
	public void testGetParentProperty() {
		try {
			persistentClassFacade.getParentProperty();
			fail();
		} catch (RuntimeException e) {
			assertEquals("getParentProperty() is only allowed on SpecialRootClass", e.getMessage());
		}
	}
	
	@Test
	public void testSetIdentifierProperty() throws Exception {
		Field field = AbstractPersistentClassFacade.class.getDeclaredField("identifierProperty");
		field.setAccessible(true);
		assertNull(field.get(persistentClassFacade));
		Property propertyTarget = new Property();
		IProperty propertyFacade = FACADE_FACTORY.createProperty(propertyTarget);
		assertNull(persistentClassTarget.getIdentifierProperty());
		persistentClassFacade.setIdentifierProperty(propertyFacade);
		assertSame(propertyTarget, persistentClassTarget.getIdentifierProperty());
		assertSame(propertyFacade, field.get(persistentClassFacade));
	}
	
	@Test
	public void testSetIdentifier() throws Exception {
		Field field = AbstractPersistentClassFacade.class.getDeclaredField("identifier");
		field.setAccessible(true);
		assertNull(field.get(persistentClassFacade));
		Value valueTarget = createValue();
		IValue valueFacade = FACADE_FACTORY.createValue(valueTarget);
		persistentClassFacade.setIdentifier(valueFacade);
		assertSame(valueTarget, persistentClassTarget.getIdentifier());
		assertSame(valueFacade, field.get(persistentClassFacade));
	}
	
	@Test
	public void testSetDiscriminator() throws Exception {
		Field field = AbstractPersistentClassFacade.class.getDeclaredField("discriminator");
		field.setAccessible(true);
		assertNull(field.get(persistentClassFacade));
		Value valueTarget = createValue();
		IValue valueFacade = FACADE_FACTORY.createValue(valueTarget);
		persistentClassFacade.setDiscriminator(valueFacade);
		assertSame(valueTarget, persistentClassTarget.getDiscriminator());
		assertSame(valueFacade, field.get(persistentClassFacade));
	}
	
	@Test
	public void testSetProxyInterfaceName() {
		assertNull(persistentClassTarget.getProxyInterfaceName());
		persistentClassFacade.setProxyInterfaceName("foo");
		assertEquals("foo", persistentClassTarget.getProxyInterfaceName());
	}
	
	@Test
	public void testSetLazy() {
		persistentClassFacade.setLazy(true);
		assertTrue(persistentClassTarget.isLazy());
		persistentClassFacade.setLazy(false);
		assertFalse(persistentClassTarget.isLazy());
	}
	
	@Test
	public void testGetSubclassIterator() throws Exception {
		Field field = AbstractPersistentClassFacade.class.getDeclaredField("subclasses");
		field.setAccessible(true);
		assertNull(field.get(persistentClassFacade));
		Iterator<?> iterator = persistentClassFacade.getSubclassIterator();
		assertFalse(iterator.hasNext());
		HashSet<?> subclasses = (HashSet<?>)field.get(persistentClassFacade);
		assertTrue(subclasses.isEmpty());
		field.set(persistentClassFacade, null);
		Subclass subclassTarget = new Subclass(persistentClassTarget, DummyMetadataBuildingContext.INSTANCE);
		persistentClassTarget.addSubclass(subclassTarget);
		iterator = persistentClassFacade.getSubclassIterator();
		assertTrue(iterator.hasNext());
		subclasses = (HashSet<?>)field.get(persistentClassFacade);
		assertEquals(1, subclasses.size());
		assertSame(subclassTarget, ((IFacade)iterator.next()).getTarget());
	}
	
	@Disabled //TODO: JBIDE-27958
	@Test
	public void testIsCustomDeleteCallable() {
		persistentClassTarget.setCustomSQLDelete("foo", false, null);
		assertFalse(persistentClassFacade.isCustomDeleteCallable());
		persistentClassTarget.setCustomSQLDelete("bar", true, null);
		assertTrue(persistentClassFacade.isCustomDeleteCallable());
	}
	
	@Disabled //TODO: JBIDE-27958
	@Test
	public void testIsCustomInsertCallable() {
		persistentClassTarget.setCustomSQLInsert("bar", false, null);
		assertFalse(persistentClassFacade.isCustomInsertCallable());
		persistentClassTarget.setCustomSQLInsert("foo", true, null);
		assertTrue(persistentClassFacade.isCustomInsertCallable());
	}
	
	@Disabled //TODO: JBIDE-27958
	@Test
	public void testIsCustomUpdateCallable() {
		persistentClassTarget.setCustomSQLUpdate("foo", false, null);
		assertFalse(persistentClassFacade.isCustomUpdateCallable());
		persistentClassTarget.setCustomSQLUpdate("bar", true, null);
		assertTrue(persistentClassFacade.isCustomUpdateCallable());
	}
	
	@Disabled //TODO: JBIDE-27958
	@Test
	public void testIsDiscriminatorValueInsertable() {
		((RootClass)persistentClassTarget).setDiscriminatorInsertable(true);
		assertTrue(persistentClassFacade.isDiscriminatorInsertable());
		((RootClass)persistentClassTarget).setDiscriminatorInsertable(false);
		assertFalse(persistentClassFacade.isDiscriminatorInsertable());
	}
	
	@Disabled //TODO: JBIDE-27958
	@Test
	public void testIsDiscriminatorValueNotNull() {
		persistentClassTarget.setDiscriminatorValue("null");
		assertFalse(persistentClassFacade.isDiscriminatorValueNotNull());
		persistentClassTarget.setDiscriminatorValue("not null");
		assertTrue(persistentClassFacade.isDiscriminatorValueNotNull());
	}
	
	@Disabled //TODO: JBIDE-27958
	@Test
	public void testIsDiscriminatorValueNull() {
		persistentClassTarget.setDiscriminatorValue("not null");
		assertFalse(persistentClassFacade.isDiscriminatorValueNull());
		persistentClassTarget.setDiscriminatorValue("null");
		assertTrue(persistentClassFacade.isDiscriminatorValueNull());
	}
	
	@Disabled //TODO: JBIDE-27958
	@Test
	public void testIsExplicitPolymorphism() {
		((RootClass)persistentClassTarget).setExplicitPolymorphism(true);
		assertTrue(persistentClassFacade.isExplicitPolymorphism());
		((RootClass)persistentClassTarget).setExplicitPolymorphism(false);
		assertFalse(persistentClassFacade.isExplicitPolymorphism());
	}
	
	@Disabled //TODO: JBIDE-27958
	@Test
	public void testIsForceDiscriminator() {
		((RootClass)persistentClassTarget).setForceDiscriminator(true);
		assertTrue(persistentClassFacade.isForceDiscriminator());
		((RootClass)persistentClassTarget).setForceDiscriminator(false);
		assertFalse(persistentClassFacade.isForceDiscriminator());
	}
	
	@Disabled //TODO: JBIDE-27958
	@Test
	public void testIsInherited() {
		persistentClassFacade = new AbstractPersistentClassFacade(FACADE_FACTORY, new RootClass(null)) {};
		assertFalse(persistentClassFacade.isInherited());
		persistentClassFacade = new AbstractPersistentClassFacade(FACADE_FACTORY, new Subclass(new RootClass(null), null)) {};
		assertTrue(persistentClassFacade.isInherited());
	}
	
	@Disabled //TODO: JBIDE-27958
	@Test
	public void testIsJoinedSubclass() {
		persistentClassFacade = new AbstractPersistentClassFacade(FACADE_FACTORY, new RootClass(null)) {};
		assertFalse(persistentClassFacade.isJoinedSubclass());
		Table rootTable = new Table();
		Table subTable = new Table();
		RootClass rootClass = new RootClass(null);
		rootClass.setTable(rootTable);
		JoinedSubclass subclass = new JoinedSubclass(rootClass, null);
		subclass.setTable(subTable);
		persistentClassFacade = new AbstractPersistentClassFacade(FACADE_FACTORY, subclass) {};
		assertTrue(persistentClassFacade.isJoinedSubclass());
		subclass.setTable(rootTable);
		assertFalse(persistentClassFacade.isJoinedSubclass());
	}
	
	@Disabled //TODO: JBIDE-27958
	@Test
	public void testIsLazy() {
		persistentClassTarget.setLazy(true);
		assertTrue(persistentClassFacade.isLazy());
		persistentClassTarget.setLazy(false);
		assertFalse(persistentClassFacade.isLazy());
	}
	
	@Disabled //TODO: JBIDE-27958
	@Test
	public void testIsLazyPropertiesCacheable() {
		((RootClass)persistentClassTarget).setLazyPropertiesCacheable(true);
		assertTrue(persistentClassFacade.isLazyPropertiesCacheable());
		((RootClass)persistentClassTarget).setLazyPropertiesCacheable(false);
		assertFalse(persistentClassFacade.isLazyPropertiesCacheable());
	}
	
	@Disabled //TODO: JBIDE-27958
	@Test
	public void testIsMutable() {
		((RootClass)persistentClassTarget).setMutable(false);
		assertFalse(persistentClassFacade.isMutable());
		((RootClass)persistentClassTarget).setMutable(true);
		assertTrue(persistentClassFacade.isMutable());
	}
	
	@Disabled //TODO: JBIDE-27958
	@Test
	public void testIsPolymorphic() {
		((RootClass)persistentClassTarget).setPolymorphic(true);
		assertTrue(persistentClassFacade.isPolymorphic());
		((RootClass)persistentClassTarget).setPolymorphic(false);
		assertFalse(persistentClassFacade.isPolymorphic());
	}
	
	@Disabled //TODO: JBIDE-27958
	@Test
	public void testIsVersioned() {
		((RootClass)persistentClassTarget).setVersion(new Property());
		assertTrue(persistentClassFacade.isVersioned());
		((RootClass)persistentClassTarget).setVersion(null);
		assertFalse(persistentClassFacade.isVersioned());
	}
	
	@Disabled //TODO: JBIDE-27958
	@Test
	public void testGetBatchSize() {
		persistentClassTarget.setBatchSize(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, persistentClassFacade.getBatchSize());
		persistentClassTarget.setBatchSize(Integer.MIN_VALUE);
		assertEquals(Integer.MIN_VALUE, persistentClassFacade.getBatchSize());
	}
	
	@Disabled //TODO: JBIDE-27958
	@Test
	public void testGetCacheConcurrencyStrategy() {
		assertNotEquals("foo", persistentClassFacade.getCacheConcurrencyStrategy());
		((RootClass)persistentClassTarget).setCacheConcurrencyStrategy("foo");
		assertEquals("foo", persistentClassFacade.getCacheConcurrencyStrategy());
	}
	
	@Disabled //TODO: JBIDE-27958
	@Test
	public void testGetCustomSQLDelete() {
		assertNotEquals("foo", persistentClassFacade.getCustomSQLDelete());
		((RootClass)persistentClassTarget).setCustomSQLDelete("foo", false, null);
		assertEquals("foo", persistentClassFacade.getCustomSQLDelete());
	}
	
	@Disabled //TODO: JBIDE-27958
	@Test
	public void testGetCustomSQLInsert() {
		assertNotEquals("bar", persistentClassFacade.getCustomSQLInsert());
		((RootClass)persistentClassTarget).setCustomSQLInsert("bar", false, null);
		assertEquals("bar", persistentClassFacade.getCustomSQLInsert());
	}
	
	@Disabled //TODO: JBIDE-27958
	@Test
	public void testGetCustomSQLUpdate() {
		assertNotEquals("foo", persistentClassFacade.getCustomSQLUpdate());
		((RootClass)persistentClassTarget).setCustomSQLUpdate("foo", false, null);
		assertEquals("foo", persistentClassFacade.getCustomSQLUpdate());
	}
	
	@Disabled //TODO: JBIDE-27958
	@Test
	public void testGetDiscriminatorValue() {
		assertNotEquals("bar", persistentClassFacade.getDiscriminatorValue());
		((RootClass)persistentClassTarget).setDiscriminatorValue("bar");
		assertEquals("bar", persistentClassFacade.getDiscriminatorValue());
	}
	
	@Disabled //TODO: JBIDE-27958
	@Test
	public void testGetLoaderName() {
		assertNotEquals("foo",persistentClassFacade.getLoaderName());
		persistentClassTarget.setLoaderName("foo");
		assertEquals("foo", persistentClassFacade.getLoaderName());
	}
	
	@Disabled //TODO: JBIDE-27958
	@Test
	public void testGetOptimisticLockMode() {
		persistentClassTarget.setOptimisticLockStyle(OptimisticLockStyle.NONE);
		assertEquals(-1, persistentClassFacade.getOptimisticLockMode());
	}
	
	@Disabled //TODO: JBIDE-27958
	@Test
	public void testGetWhere() {
		assertNotEquals("foo", persistentClassFacade.getWhere());
		((RootClass)persistentClassTarget).setWhere("foo");
		assertEquals("foo", persistentClassFacade.getWhere());
	}
	
	@Disabled //TODO: JBIDE-27958
	@Test
	public void testGetRootTable() throws Exception {
		Field field = AbstractPersistentClassFacade.class.getDeclaredField("rootTable");
		field.setAccessible(true);
		assertNull(field.get(persistentClassFacade));
		Table tableTarget = new Table();
		((RootClass)persistentClassTarget).setTable(tableTarget);
		ITable tableFacade = persistentClassFacade.getRootTable();
		assertSame(tableTarget, ((IFacade)tableFacade).getTarget());
		assertSame(tableFacade, field.get(persistentClassFacade));
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
