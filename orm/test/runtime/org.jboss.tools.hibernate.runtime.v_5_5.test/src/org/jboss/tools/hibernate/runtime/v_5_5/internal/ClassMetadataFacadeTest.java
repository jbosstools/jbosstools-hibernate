package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.internal.BootstrapContextImpl;
import org.hibernate.boot.internal.InFlightMetadataCollectorImpl;
import org.hibernate.boot.internal.MetadataBuilderImpl.MetadataBuildingOptionsImpl;
import org.hibernate.boot.internal.MetadataBuildingContextRootImpl;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.boot.spi.InFlightMetadataCollector;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.OptimisticLockStyle;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.persister.spi.PersisterCreationContext;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClassMetadataFacadeTest {

	private static final FacadeFactoryImpl FACADE_FACTORY = new FacadeFactoryImpl();
	
	private ClassMetadata classMetadataTarget;
	private ClassMetadataFacadeImpl classMetadataFacade;
	
	@BeforeEach
	public void beforeEach() throws Exception {
		classMetadataTarget = setupFooBarPersister();
		classMetadataFacade = new ClassMetadataFacadeImpl(FACADE_FACTORY, classMetadataTarget);
	}
	
	@Test
	public void testGetEntityName() {
		assertEquals("foobar", classMetadataFacade.getEntityName());
	}
	
	@Test
	public void testGetIdentifierPropertyName() {
		assertEquals("foo", classMetadataFacade.getIdentifierPropertyName());
	}
	
	@Test
	public void testGetPropertyNames() {
		assertSame(PROPERTY_NAMES, classMetadataFacade.getPropertyNames());
	}
	
	@Test
	public void testGetPropertyTypes() {
		IType[] typeFacades = classMetadataFacade.getPropertyTypes();
		assertSame(TYPE_INSTANCE, ((IFacade)typeFacades[0]).getTarget());
 	}
	
	private ClassMetadata setupFooBarPersister() {
		StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
		builder.applySetting("hibernate.dialect", TestDialect.class.getName());
		StandardServiceRegistry serviceRegistry = builder.build();		
		MetadataBuildingOptionsImpl metadataBuildingOptions = 
				new MetadataBuildingOptionsImpl(serviceRegistry);	
		BootstrapContextImpl bootstrapContext = new BootstrapContextImpl(
				serviceRegistry, 
				metadataBuildingOptions);
		metadataBuildingOptions.setBootstrapContext(bootstrapContext);
		InFlightMetadataCollector inFlightMetadataCollector = 
				new InFlightMetadataCollectorImpl(
						bootstrapContext,
						metadataBuildingOptions);
		MetadataBuildingContext metadataBuildingContext = 
				new MetadataBuildingContextRootImpl(
						bootstrapContext, 
						metadataBuildingOptions, 
						inFlightMetadataCollector);
		AbstractEntityPersister result = new TestEntityPersister(
				createPersistentClass(metadataBuildingContext), 
				createPersisterCreationContext(
						serviceRegistry,
						bootstrapContext));
		return result;
	}
	
	private PersisterCreationContext createPersisterCreationContext(
			StandardServiceRegistry serviceRegisty,
			BootstrapContext bootstrapContext) {
		MetadataSources metadataSources = new MetadataSources(serviceRegisty);
		return new TestCreationContext(
				bootstrapContext, 
				(MetadataImplementor)metadataSources.buildMetadata());
	}
	
	private PersistentClass createPersistentClass(
			MetadataBuildingContext metadataBuildingContext) {
		RootClass rc = new RootClass(metadataBuildingContext);
		Table t = new Table("foobar");
		rc.setTable(t);
		Column c = new Column("foo");
		t.addColumn(c);
		ArrayList<Column> keyList = new ArrayList<>();
		keyList.add(c);
		t.createUniqueKey(keyList);
		SimpleValue sv = new SimpleValue(metadataBuildingContext, t);
		sv.setNullValue("null");
		sv.setTypeName(Integer.class.getName());
		sv.addColumn(c);
		rc.setEntityName("foobar");
		rc.setIdentifier(sv);
		rc.setClassName(FooBar.class.getName());
		rc.setOptimisticLockStyle(OptimisticLockStyle.NONE);
		return rc;
	}
	
	private class TestCreationContext implements PersisterCreationContext {
		
		private final MetadataImplementor metadataImplementor;
		private final SessionFactoryImplementor sessionFactoryImplementor;
		
		TestCreationContext(
				BootstrapContext bootstrapContext,
				MetadataImplementor metadataImplementor) {
			this.metadataImplementor = metadataImplementor;
			this.sessionFactoryImplementor = 
					(SessionFactoryImplementor)metadataImplementor.buildSessionFactory();
		}

		@Override
		public SessionFactoryImplementor getSessionFactory() {
			return sessionFactoryImplementor;
		}

		@Override
		public MetadataImplementor getMetadata() {
			return metadataImplementor;
		}
		
	}
		
	private static final Object PROPERTY_VALUE = new Object();
	private static final String[] PROPERTY_NAMES = new String[] {};
	private static final Type TYPE_INSTANCE = new StringType();
	
	private static class TestEntityPersister extends SingleTableEntityPersister {
		
		private boolean hasIdentifierProperty = false;
		
		public TestEntityPersister(
				PersistentClass persistentClass, 
				PersisterCreationContext creationContext) {
			super(persistentClass, null, null, creationContext);
		}
		
		@Override
		public Object getPropertyValue(Object object, String propertyName) {
			return PROPERTY_VALUE;
		}
		
		@Override
		public String getIdentifierPropertyName() {
			return "foo";
		}
		
		@Override
		public String[] getPropertyNames() {
			return PROPERTY_NAMES;
		}
		
		@Override
		public Type[] getPropertyTypes() {
			return new Type[] { TYPE_INSTANCE };
		}
		
		@Override
		public Type getIdentifierType() {
			return TYPE_INSTANCE;
 		}
		
		@Override
		public boolean hasIdentifierProperty() {
			return hasIdentifierProperty;
		}
		
		@Override
		public Serializable getIdentifier(Object object, SharedSessionContractImplementor s) {
			return (Serializable)object;
		}
		
	}
	
	public static SharedSessionContractImplementor createSession() {
		return (SharedSessionContractImplementor)Proxy.newProxyInstance(
				ClassMetadataFacadeTest.class.getClassLoader(), 
				new Class[] { SharedSessionContractImplementor.class },
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						return null;
					}
		});
	}
	
	public class FooBar {
		public int id = 1967;
	}
	
	public static class TestDialect extends Dialect {}
	
}
