package org.jboss.tools.hibernate.runtime.v_5_2.internal;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.internal.ClassLoaderAccessImpl;
import org.hibernate.boot.internal.InFlightMetadataCollectorImpl;
import org.hibernate.boot.internal.MetadataBuilderImpl.MetadataBuildingOptionsImpl;
import org.hibernate.boot.internal.MetadataBuildingContextRootImpl;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.spi.ClassLoaderAccess;
import org.hibernate.boot.spi.InFlightMetadataCollector;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.engine.OptimisticLockStyle;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.tuple.entity.EntityTuplizer;
import org.hibernate.type.TypeResolver;
import org.jboss.tools.hibernate.runtime.common.AbstractEntityMetamodelFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IEntityMetamodel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EntityMetamodelFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	private static final Object OBJECT = new Object();
	private static final Integer INDEX = Integer.MAX_VALUE;
	
	private IEntityMetamodel entityMetamodelFacade = null; 
	private EntityMetamodel entityMetamodel = null;

	private String methodName = null;
	private Object[] arguments = null;
	
	@BeforeEach
	public void beforeEach() throws Exception {
		entityMetamodel = createFooBarModel();
		entityMetamodelFacade = new AbstractEntityMetamodelFacade(FACADE_FACTORY, entityMetamodel) {};
	}
	
	@Test
	public void testGetTuplizerPropertyValue() {
		assertSame(OBJECT, entityMetamodelFacade.getTuplizerPropertyValue(OBJECT, Integer.MAX_VALUE));
		assertEquals("getPropertyValue", methodName);
		assertArrayEquals(new Object[] { OBJECT,  Integer.MAX_VALUE }, arguments);
	}
	
	@Test
	public void testGetPropertyIndexOrNull() {
		assertSame(INDEX, entityMetamodelFacade.getPropertyIndexOrNull("foobar"));
		assertEquals("getPropertyIndexOrNull", methodName);
		assertArrayEquals(arguments, new Object[] { "foobar" });
	}
	
	private PersistentClass createPersistentClass(
			MetadataBuildingContext metadataBuildingContext,
			MetadataImplementor metadataImplementor) {
		RootClass rc = new RootClass(metadataBuildingContext);
		Table t = new Table("foobar");
		rc.setTable(t);
		Column c = new Column("foo");
		t.addColumn(c);
		ArrayList<Column> keyList = new ArrayList<>();
		keyList.add(c);
		t.createUniqueKey(keyList);
		SimpleValue sv = new SimpleValue(metadataImplementor, t);
		sv.setNullValue("null");
		sv.setTypeName(Integer.class.getName());
		sv.addColumn(c);
		rc.setEntityName("foobar");
		rc.setIdentifier(sv);
		rc.setClassName(FooBar.class.getName());
		rc.setOptimisticLockStyle(OptimisticLockStyle.NONE);
		return rc;
	}
	
	private EntityMetamodel createFooBarModel() {
		StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().build();		
		MetadataBuildingOptionsImpl metadataBuildingOptions = 
				new MetadataBuildingOptionsImpl(serviceRegistry);	
		InFlightMetadataCollector inFlightMetadataCollector = 
				new InFlightMetadataCollectorImpl(
						metadataBuildingOptions, new TypeResolver());
		ClassLoaderService classLoaderService = metadataBuildingOptions.getServiceRegistry().getService( ClassLoaderService.class );
		ClassLoaderAccess classLoaderAccess = new ClassLoaderAccessImpl(
				metadataBuildingOptions.getTempClassLoader(),
				classLoaderService
		);
		MetadataBuildingContext metadataBuildingContext = 
				new MetadataBuildingContextRootImpl(
						metadataBuildingOptions, 
						classLoaderAccess,
						inFlightMetadataCollector);
		MetadataSources metadataSources = new MetadataSources(serviceRegistry);
		PersistentClass persistentClass = createPersistentClass(
				metadataBuildingContext, 
				(MetadataImplementor)metadataSources.buildMetadata());
		SessionFactoryImplementor sessionFactoryImplementor = 
				(SessionFactoryImplementor)metadataSources
					.buildMetadata()
					.buildSessionFactory();
		return  new EntityMetamodel(persistentClass, null, sessionFactoryImplementor) {
			private static final long serialVersionUID = 1L;
			@Override public EntityTuplizer getTuplizer() {
				return (EntityTuplizer)Proxy.newProxyInstance(
						FACADE_FACTORY.getClassLoader(), 
						new Class[] { EntityTuplizer.class }, 
						new TestInvocationHandler());
			}
			@Override public Integer getPropertyIndexOrNull(String id) {
				methodName = "getPropertyIndexOrNull";
				arguments = new Object[] { id };
				return INDEX;
			}
		};
	}
		
	public class FooBar {
		public int id = 1967;
	}
	
	private class TestInvocationHandler implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			methodName = method.getName();
			arguments = args;
			return OBJECT;
		}
	}
}
