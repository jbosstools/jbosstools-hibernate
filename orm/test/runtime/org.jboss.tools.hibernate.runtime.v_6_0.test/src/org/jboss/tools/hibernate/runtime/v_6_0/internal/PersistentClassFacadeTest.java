package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Iterator;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Subclass;
import org.jboss.tools.hibernate.runtime.common.AbstractPersistentClassFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.junit.Before;
import org.junit.Test;

public class PersistentClassFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private IPersistentClass persistentClassFacade = null; 
	private PersistentClass persistentClassTarget = null;
	
	@Before
	public void before() {
		persistentClassTarget = new RootClass(null);
		persistentClassFacade = new AbstractPersistentClassFacade(FACADE_FACTORY, persistentClassTarget) {};
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(persistentClassFacade);
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
		persistentClassTarget = new SingleTableSubclass(new RootClass(null), null);
		persistentClassFacade = new AbstractPersistentClassFacade(FACADE_FACTORY, persistentClassTarget) {};
		assertFalse(persistentClassFacade.isAssignableToRootClass());
		persistentClassTarget = new RootClass(null);
		persistentClassFacade = new AbstractPersistentClassFacade(FACADE_FACTORY, persistentClassTarget) {};
		assertTrue(persistentClassFacade.isAssignableToRootClass());
	}
	
	@Test
	public void testIsRootClass() {
		persistentClassTarget = new SingleTableSubclass(new RootClass(null), null);
		persistentClassFacade = new AbstractPersistentClassFacade(FACADE_FACTORY, persistentClassTarget) {};
		assertFalse(persistentClassFacade.isRootClass());
		persistentClassTarget = new RootClass(null);
		persistentClassFacade = new AbstractPersistentClassFacade(FACADE_FACTORY, persistentClassTarget) {};
		assertTrue(persistentClassFacade.isRootClass());
	}
	
	@Test
	public void testGetIdentifierProperty() {
		Property propertyTarget = new Property();
		assertNull(persistentClassFacade.getIdentifierProperty());
		((RootClass)persistentClassTarget).setIdentifierProperty(propertyTarget);
		IProperty propertyFacade = persistentClassFacade.getIdentifierProperty();
		assertNotNull(propertyFacade);
		assertSame(propertyTarget, ((IFacade)propertyFacade).getTarget());
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
		PersistentClass subClassTarget = new Subclass(persistentClassTarget, null);
		IPersistentClass subClassFacade = new AbstractPersistentClassFacade(FACADE_FACTORY, subClassTarget) {};
		assertFalse(subClassFacade.isInstanceOfRootClass());
	}
	
	@Test
	public void testIsInstanceOfSubclass() {
		assertFalse(persistentClassFacade.isInstanceOfSubclass());
		PersistentClass subClassTarget = new Subclass(persistentClassTarget, null);
		IPersistentClass subClassFacade = new AbstractPersistentClassFacade(FACADE_FACTORY, subClassTarget) {};
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
		PersistentClass persistentClass = new RootClass(null) {
			private static final long serialVersionUID = 1L;
			@Override
			public Iterator<?> getPropertyClosureIterator() {
				HashSet<Property> set = new HashSet<Property>();
				set.add(propertyTarget);
				return set.iterator();
			}
		};
		persistentClassFacade = new AbstractPersistentClassFacade(FACADE_FACTORY, persistentClass) {};
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
		Subclass subclassTarget = new Subclass(persistentClassTarget, null);
		IPersistentClass subclassFacade = new AbstractPersistentClassFacade(FACADE_FACTORY, subclassTarget) {};
		assertNull(field.get(subclassFacade));
		superclassFacade = subclassFacade.getSuperclass();
		assertNotNull(superclassFacade);
		assertSame(superclassFacade, field.get(subclassFacade));
		assertSame(persistentClassTarget, ((IFacade)superclassFacade).getTarget());
	}
	
}
