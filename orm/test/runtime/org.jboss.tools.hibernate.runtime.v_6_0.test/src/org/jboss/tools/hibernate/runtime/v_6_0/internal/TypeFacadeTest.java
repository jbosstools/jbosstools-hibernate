package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.RootClass;
import org.hibernate.tuple.component.ComponentMetamodel;
import org.hibernate.type.AnyType;
import org.hibernate.type.ArrayType;
import org.hibernate.type.BagType;
import org.hibernate.type.ClassType;
import org.hibernate.type.ComponentType;
import org.hibernate.type.EntityType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.ManyToOneType;
import org.hibernate.type.OneToOneType;
import org.hibernate.type.spi.TypeConfiguration;
import org.jboss.tools.hibernate.runtime.common.AbstractTypeFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.junit.Test;

public class TypeFacadeTest {
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	@Test
	public void testToString() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = new AbstractTypeFacade(FACADE_FACTORY, classType){};
		assertEquals(
				TypeFacadeTest.class.getName(), 
				typeFacade.toString(TypeFacadeTest.class));
		ArrayType arrayType = new ArrayType(null, "foo", "bar", String.class);
		typeFacade = new AbstractTypeFacade(FACADE_FACTORY, arrayType){};
		assertNull(typeFacade.toString(new String[] { "foo", "bar" }));
	}
	
	@Test
	public void testGetName() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = new AbstractTypeFacade(FACADE_FACTORY, classType){};
		assertEquals("class", typeFacade.getName());
		ArrayType arrayType = new ArrayType(null, "foo", "bar", String.class);
		typeFacade = new AbstractTypeFacade(FACADE_FACTORY, arrayType){};
		assertEquals("[Ljava.lang.String;(foo)", typeFacade.getName());
	}
	
	@Test
	public void testFromStringValue() {
		IType typeFacade = null;
		// first try type that is string representable
		ClassType classType = new ClassType();
		typeFacade = new AbstractTypeFacade(FACADE_FACTORY, classType){};
		assertEquals(
				TypeFacadeTest.class, 
				typeFacade.fromStringValue(TypeFacadeTest.class.getName()));
		// next try type that is not string representable
		ArrayType arrayType = new ArrayType(null, "foo", "bar", String.class);
		typeFacade = new AbstractTypeFacade(FACADE_FACTORY, arrayType){};
		assertNull(typeFacade.fromStringValue("just a random string"));
	}
	
	@Test
	public void testIsEntityType() {
		IType typeFacade = null;
		// first try type that is not an entity type
		ClassType classType = new ClassType();
		typeFacade =  new AbstractTypeFacade(FACADE_FACTORY, classType){};
		assertFalse(typeFacade.isEntityType());
		// next try type that is an entity type
		EntityType entityType = new ManyToOneType((TypeConfiguration)null, null);
		typeFacade = new AbstractTypeFacade(FACADE_FACTORY, entityType){};
		assertTrue(entityType.isEntityType());
	}
	
	@Test
	public void testIsOneToOne() {
		IType typeFacade = null;
		// first try type that is not a one to one type
		ClassType classType = new ClassType();
		typeFacade = new AbstractTypeFacade(FACADE_FACTORY, classType){};
		assertFalse(typeFacade.isOneToOne());
		// next try another type that is not a one to one type
		EntityType entityType = new ManyToOneType((TypeConfiguration)null, null);
		typeFacade = new AbstractTypeFacade(FACADE_FACTORY, entityType){};
		assertFalse(entityType.isOneToOne());
		// finally try a type that is a one to one type
		OneToOneType oneToOneType = new OneToOneType(
				null, null, null, false, null, false, false, null, null, false);
		typeFacade =  new AbstractTypeFacade(FACADE_FACTORY, oneToOneType){};
		assertTrue(oneToOneType.isOneToOne());
	}
	
	@Test
	public void testIsAnyType() {
		IType typeFacade = null;
		// first try type that is not a any type
		ClassType classType = new ClassType();
		typeFacade = new AbstractTypeFacade(FACADE_FACTORY, classType){};
		assertFalse(typeFacade.isAnyType());
		// next try a any type
		AnyType anyType = new AnyType(null, null, null, true);
		typeFacade = new AbstractTypeFacade(FACADE_FACTORY, anyType){};
		assertTrue(typeFacade.isAnyType());
	}
	
	@Test
	public void testIsComponentType() {
		IType typeFacade = null;
		// first try type that is not a component type
		ClassType classType = new ClassType();
		typeFacade = new AbstractTypeFacade(FACADE_FACTORY, classType){};
		assertFalse(typeFacade.isComponentType());
		// next try a component type
		StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder();
		ssrb.applySetting("hibernate.dialect", TestDialect.class.getName());
		StandardServiceRegistry ssr = ssrb.build();
		MetadataBuildingOptions mdbo = 
				new MetadataBuilderImpl.MetadataBuildingOptionsImpl(ssr);
		BootstrapContext btc = new BootstrapContextImpl(ssr, mdbo);
		InFlightMetadataCollector ifmdc = new InFlightMetadataCollectorImpl(btc, mdbo);
		MetadataBuildingContext mdbc = new MetadataBuildingContextRootImpl(btc, mdbo, ifmdc);
		ComponentType componentType = 
				new ComponentType(
						null,
						new ComponentMetamodel(
								new Component(mdbc, new RootClass(null)),
								btc));
		typeFacade = new AbstractTypeFacade(FACADE_FACTORY, componentType){};
		assertTrue(typeFacade.isComponentType());
	}
	
	@Test
	public void testIsCollectionType() {
		IType typeFacade = null;
		// first try type that is not a collection type
		ClassType classType = new ClassType();
		typeFacade = new AbstractTypeFacade(FACADE_FACTORY, classType){};
		assertFalse(typeFacade.isCollectionType());
		// next try a collection type
		ArrayType arrayType = new ArrayType(null, null, null, String.class);
		typeFacade = new AbstractTypeFacade(FACADE_FACTORY, arrayType){};
		assertTrue(typeFacade.isCollectionType());
	}
	
	@Test
	public void testGetReturnedClass() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = new AbstractTypeFacade(FACADE_FACTORY, classType){};
		assertEquals(Class.class, typeFacade.getReturnedClass());
		ArrayType arrayType = new ArrayType(null, null, null, String.class);
		typeFacade = new AbstractTypeFacade(FACADE_FACTORY, arrayType){};
		assertEquals(String[].class, typeFacade.getReturnedClass());
	}
	
	@Test
	public void testGetAssociatedEntityName() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = new AbstractTypeFacade(FACADE_FACTORY, classType){};
		assertNull(typeFacade.getAssociatedEntityName());
		EntityType entityType = new ManyToOneType((TypeConfiguration)null, "foo");
		typeFacade = new AbstractTypeFacade(FACADE_FACTORY, entityType){};
		assertEquals("foo", typeFacade.getAssociatedEntityName());
	}
	
	@Test
	public void testIsIntegerType() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade =  new AbstractTypeFacade(FACADE_FACTORY, classType){};
		assertFalse(typeFacade.isIntegerType());
		IntegerType integerType = new IntegerType();
		typeFacade = new AbstractTypeFacade(FACADE_FACTORY, integerType){};
		assertTrue(typeFacade.isIntegerType());
	}
	
	@Test
	public void testIsArrayType() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = new AbstractTypeFacade(FACADE_FACTORY, classType){};
		assertFalse(typeFacade.isArrayType());
		BagType bagType = new BagType(null, null, null);
		typeFacade = new AbstractTypeFacade(FACADE_FACTORY, bagType){};
		assertFalse(typeFacade.isArrayType());
		ArrayType arrayType = new ArrayType(null, null, null, String.class);
		typeFacade = new AbstractTypeFacade(FACADE_FACTORY, arrayType){};
		assertTrue(typeFacade.isArrayType());
	}
	
	public static class TestDialect extends Dialect {
		@Override
		public int getVersion() { return 0; }	
	}

}
