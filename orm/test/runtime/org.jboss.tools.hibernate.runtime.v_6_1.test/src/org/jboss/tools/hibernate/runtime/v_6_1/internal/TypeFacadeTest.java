package org.jboss.tools.hibernate.runtime.v_6_1.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.boot.internal.BootstrapContextImpl;
import org.hibernate.boot.internal.InFlightMetadataCollectorImpl;
import org.hibernate.boot.internal.MetadataBuilderImpl;
import org.hibernate.boot.internal.MetadataBuildingContextRootImpl;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.boot.spi.InFlightMetadataCollector;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.MetadataBuildingOptions;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.RootClass;
import org.hibernate.type.AnyType;
import org.hibernate.type.ArrayType;
import org.hibernate.type.BagType;
import org.hibernate.type.ComponentType;
import org.hibernate.type.EntityType;
import org.hibernate.type.ManyToOneType;
import org.hibernate.type.OneToOneType;
import org.hibernate.type.spi.TypeConfiguration;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.jboss.tools.hibernate.runtime.v_6_1.internal.legacy.ClassType;
import org.jboss.tools.hibernate.runtime.v_6_1.internal.legacy.IntegerType;
import org.jboss.tools.hibernate.runtime.v_6_1.internal.legacy.StringType;
import org.jboss.tools.hibernate.runtime.v_6_1.internal.util.MockConnectionProvider;
import org.jboss.tools.hibernate.runtime.v_6_1.internal.util.MockDialect;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class TypeFacadeTest {
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	@Test
	public void testToString() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = new TypeFacadeImpl(FACADE_FACTORY, classType){};
		assertEquals(
				TypeFacadeTest.class.getName(), 
				typeFacade.toString(TypeFacadeTest.class));
		ArrayType arrayType = new ArrayType("foo", "bar", String.class);
		typeFacade = new TypeFacadeImpl(FACADE_FACTORY, arrayType){};
		assertNull(typeFacade.toString(new String[] { "foo", "bar" }));
	}
	
	@Test
	public void testGetName() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = new TypeFacadeImpl(FACADE_FACTORY, classType){};
		assertEquals("class", typeFacade.getName());
		ArrayType arrayType = new ArrayType("foo", "bar", String.class);
		typeFacade = new TypeFacadeImpl(FACADE_FACTORY, arrayType){};
		assertEquals("[Ljava.lang.String;(foo)", typeFacade.getName());
	}
	
	// TODO JBIDE-28154: Investigate failure 
	@Disabled
	@Test
	public void testFromStringValue() {
		IType typeFacade = null;
		// first try type that is string representable
		ClassType classType = new ClassType();
		typeFacade = new TypeFacadeImpl(FACADE_FACTORY, classType){};
		assertEquals(
				TypeFacadeTest.class, 
				typeFacade.fromStringValue(TypeFacadeTest.class.getName()));
		// next try type that is not string representable
		ArrayType arrayType = new ArrayType("foo", "bar", String.class);
		typeFacade = new TypeFacadeImpl(FACADE_FACTORY, arrayType){};
		assertNull(typeFacade.fromStringValue("just a random string"));
	}
	
	@Test
	public void testIsEntityType() {
		IType typeFacade = null;
		// first try type that is not an entity type
		ClassType classType = new ClassType();
		typeFacade =  new TypeFacadeImpl(FACADE_FACTORY, classType){};
		assertFalse(typeFacade.isEntityType());
		// next try type that is an entity type
		EntityType entityType = new ManyToOneType((TypeConfiguration)null, null);
		typeFacade = new TypeFacadeImpl(FACADE_FACTORY, entityType){};
		assertTrue(entityType.isEntityType());
	}
	
	@Test
	public void testIsOneToOne() {
		IType typeFacade = null;
		// first try type that is not a one to one type
		ClassType classType = new ClassType();
		typeFacade = new TypeFacadeImpl(FACADE_FACTORY, classType){};
		assertFalse(typeFacade.isOneToOne());
		// next try another type that is not a one to one type
		EntityType entityType = new ManyToOneType((TypeConfiguration)null, null);
		typeFacade = new TypeFacadeImpl(FACADE_FACTORY, entityType){};
		assertFalse(entityType.isOneToOne());
		// finally try a type that is a one to one type
		OneToOneType oneToOneType = new OneToOneType(
				null, null, null, false, null, false, false, null, null, false);
		typeFacade =  new TypeFacadeImpl(FACADE_FACTORY, oneToOneType){};
		assertTrue(oneToOneType.isOneToOne());
	}
	
	@Test
	public void testIsAnyType() {
		IType typeFacade = null;
		// first try type that is not a any type
		ClassType classType = new ClassType();
		typeFacade = new TypeFacadeImpl(FACADE_FACTORY, classType){};
		assertFalse(typeFacade.isAnyType());
		// next try a any type
		AnyType anyType = new AnyType(null, null, null, true);
		typeFacade = new TypeFacadeImpl(FACADE_FACTORY, anyType){};
		assertTrue(typeFacade.isAnyType());
	}
	
	// TODO JBIDE-28154: Investigate failure 
	@Disabled
	@Test
	public void testIsComponentType() {
		IType typeFacade = null;
		// first try type that is not a component type
		ClassType classType = new ClassType();
		typeFacade = new TypeFacadeImpl(FACADE_FACTORY, classType){};
		assertFalse(typeFacade.isComponentType());
		// next try a component type
		StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder();
		ssrb.applySetting(AvailableSettings.DIALECT, MockDialect.class.getName());
		ssrb.applySetting(AvailableSettings.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
		StandardServiceRegistry ssr = ssrb.build();
		MetadataBuildingOptions mdbo = 
				new MetadataBuilderImpl.MetadataBuildingOptionsImpl(ssr);
		BootstrapContext btc = new BootstrapContextImpl(ssr, mdbo);
		InFlightMetadataCollector ifmdc = new InFlightMetadataCollectorImpl(btc, mdbo);
		MetadataBuildingContext mdbc = new MetadataBuildingContextRootImpl("JBoss Tools", btc, mdbo, ifmdc);
		ComponentType componentType = 
				new ComponentType(
						new Component(mdbc, new RootClass(mdbc)),
						new int[] {},
						mdbc);
		typeFacade = new TypeFacadeImpl(FACADE_FACTORY, componentType){};
		assertTrue(typeFacade.isComponentType());
	}
	
	@Test
	public void testIsCollectionType() {
		IType typeFacade = null;
		// first try type that is not a collection type
		ClassType classType = new ClassType();
		typeFacade = new TypeFacadeImpl(FACADE_FACTORY, classType){};
		assertFalse(typeFacade.isCollectionType());
		// next try a collection type
		ArrayType arrayType = new ArrayType(null, null, String.class);
		typeFacade = new TypeFacadeImpl(FACADE_FACTORY, arrayType){};
		assertTrue(typeFacade.isCollectionType());
	}
	
	@Test
	public void testGetReturnedClass() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = new TypeFacadeImpl(FACADE_FACTORY, classType){};
		assertEquals(Class.class.getName(), typeFacade.getReturnedClassName());
		ArrayType arrayType = new ArrayType(null, null, String.class);
		typeFacade = new TypeFacadeImpl(FACADE_FACTORY, arrayType){};
		assertEquals(String[].class.getName(), typeFacade.getReturnedClassName());
		EntityType entityType = new ManyToOneType((TypeConfiguration)null, "org.foo.bar");
		typeFacade = new TypeFacadeImpl(FACADE_FACTORY, entityType);
		assertEquals("org.foo.bar", typeFacade.getReturnedClassName());
	}
	
	@Test
	public void testGetAssociatedEntityName() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = new TypeFacadeImpl(FACADE_FACTORY, classType){};
		assertNull(typeFacade.getAssociatedEntityName());
		EntityType entityType = new ManyToOneType((TypeConfiguration)null, "foo");
		typeFacade = new TypeFacadeImpl(FACADE_FACTORY, entityType){};
		assertEquals("foo", typeFacade.getAssociatedEntityName());
	}
	
	@Test
	public void testIsIntegerType() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade =  new TypeFacadeImpl(FACADE_FACTORY, classType){};
		assertFalse(typeFacade.isIntegerType());
		IntegerType integerType = new IntegerType();
		typeFacade = new TypeFacadeImpl(FACADE_FACTORY, integerType){};
		assertTrue(typeFacade.isIntegerType());
	}
	
	@Test
	public void testIsArrayType() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = new TypeFacadeImpl(FACADE_FACTORY, classType){};
		assertFalse(typeFacade.isArrayType());
		BagType bagType = new BagType(null, null);
		typeFacade = new TypeFacadeImpl(FACADE_FACTORY, bagType){};
		assertFalse(typeFacade.isArrayType());
		ArrayType arrayType = new ArrayType(null, null, String.class);
		typeFacade = new TypeFacadeImpl(FACADE_FACTORY, arrayType){};
		assertTrue(typeFacade.isArrayType());
	}
	
	// TODO JBIDE-28154: Investigate failure 
	@Disabled
	@Test
	public void testIsInstanceOfPrimitiveType() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = new TypeFacadeImpl(FACADE_FACTORY, classType){};
		assertFalse(typeFacade.isInstanceOfPrimitiveType());
		StringType stringType = new StringType();
		typeFacade = new TypeFacadeImpl(FACADE_FACTORY, stringType){};
		assertFalse(typeFacade.isInstanceOfPrimitiveType());
		IntegerType integerType = new IntegerType();
		typeFacade = new TypeFacadeImpl(FACADE_FACTORY, integerType){};
		assertTrue(typeFacade.isInstanceOfPrimitiveType());
	}
	
	// TODO JBIDE-28154: Investigate failure 
	@Disabled
	@Test
	public void testGetPrimitiveClass() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = new TypeFacadeImpl(FACADE_FACTORY, classType){};
		assertNull(typeFacade.getPrimitiveClass());
		IntegerType integerType = new IntegerType();
		typeFacade = new TypeFacadeImpl(FACADE_FACTORY, integerType){};
		assertEquals(int.class, typeFacade.getPrimitiveClass());
	}
	
	@Test
	public void testGetRole() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = new TypeFacadeImpl(FACADE_FACTORY, classType){};
		assertNull(typeFacade.getRole());
		ArrayType arrayType = new ArrayType("foo", null, String.class);
		typeFacade = new TypeFacadeImpl(FACADE_FACTORY, arrayType){};
		assertEquals("foo", typeFacade.getRole());
	}
	
}
