package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.hibernate.engine.OptimisticLockStyle;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Table;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metamodel.MappingMetamodel;
import org.hibernate.metamodel.spi.RuntimeModelCreationContext;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.persister.spi.PersisterCreationContext;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IEntityMetamodel;
import org.jboss.tools.hibernate.runtime.spi.ISession;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
	
	@Disabled //TODO: JBIDE-27958
	@Test
	public void testGetPropertyTypes() {
		IType[] typeFacades = classMetadataFacade.getPropertyTypes();
		assertSame(TYPE_INSTANCE, ((IFacade)typeFacades[0]).getTarget());
 	}
	
	@Disabled //TODO: JBIDE-27958
	@Test
	public void testGetMappedClass() {
		assertSame(FooBar.class, classMetadataFacade.getMappedClass());
	}
	
	@Disabled //TODO: JBIDE-27958
	@Test
	public void testGetIdentifierType() {
		assertSame(TYPE_INSTANCE, ((IFacade)classMetadataFacade.getIdentifierType()).getTarget());
	}
	
	@Disabled //TODO: JBIDE-27958
	@Test
	public void testGetPropertyValue() {
		assertSame(PROPERTY_VALUE, classMetadataFacade.getPropertyValue(null, null));
	}
	
	@Disabled //TODO: JBIDE-27958
	@Test
	public void testHasIdentifierProperty() {
		assertFalse(classMetadataFacade.hasIdentifierProperty());
		((TestEntityPersister)classMetadataTarget).hasIdentifierProperty = true;
		assertTrue(classMetadataFacade.hasIdentifierProperty());
	}
	
	@Disabled //TODO: JBIDE-27958
	@Test 
	public void testGetIdentifier() {
		assertNull(((TestEntityPersister)classMetadataTarget).session);
		final SharedSessionContractImplementor sessionTarget = createSession();
		ISession sessionFacade = FACADE_FACTORY.createSession(sessionTarget);
		Object theObject = new Object();
		Object anotherObject = classMetadataFacade.getIdentifier(theObject, sessionFacade);
		assertSame(theObject, anotherObject);
		assertSame(sessionTarget, ((TestEntityPersister)classMetadataTarget).session);
	}
	
	@Disabled //TODO: JBIDE-27958
	@Test
	public void testIsInstanceOfAbstractEntityPersister() {
		assertTrue(classMetadataFacade.isInstanceOfAbstractEntityPersister());
		classMetadataTarget = (ClassMetadata)Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[] { ClassMetadata.class }, 
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						return null;
					}
				});	
		classMetadataFacade = new ClassMetadataFacadeImpl(FACADE_FACTORY, classMetadataTarget);
		assertFalse(classMetadataFacade.isInstanceOfAbstractEntityPersister());
	}
	
	@Disabled //TODO: JBIDE-27958
	@Test
	public void testGetEntityMetamodel() {
		IEntityMetamodel entityMetamodel = classMetadataFacade.getEntityMetamodel();
		assertSame(classMetadataTarget, ((IFacade)entityMetamodel).getTarget());
	}

	private ClassMetadata setupFooBarPersister() {
		StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
		builder.applySetting("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
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
						"JBoss Tools",
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
		Table t = new Table("tools", "foobar");
		rc.setTable(t);
		Column c = new Column("foo");
		t.addColumn(c);
		ArrayList<Column> keyList = new ArrayList<>();
		keyList.add(c);
		t.createUniqueKey(keyList);
		BasicValue sv = new BasicValue(metadataBuildingContext, t);
		sv.setNullValue("null");
		sv.setTypeName(Integer.class.getName());
		sv.addColumn(c);
		rc.setEntityName("foobar");
		rc.setIdentifier(sv);
		rc.setClassName(FooBar.class.getName());
		rc.setOptimisticLockStyle(OptimisticLockStyle.NONE);
		return rc;
	}
	
	private class TestCreationContext implements PersisterCreationContext, RuntimeModelCreationContext {
		
		private final BootstrapContext bootstrapContext;
		private final MetadataImplementor metadataImplementor;
		private final SessionFactoryImplementor sessionFactoryImplementor;
		
		TestCreationContext(
				BootstrapContext bootstrapContext,
				MetadataImplementor metadataImplementor) {
			this.bootstrapContext = bootstrapContext;
			this.metadataImplementor = metadataImplementor;
			this.sessionFactoryImplementor = 
					(SessionFactoryImplementor)metadataImplementor.buildSessionFactory();
		}

		@Override
		public MetadataImplementor getBootModel() {
			return null;
		}

		@Override
		public MappingMetamodel getDomainModel() {
			return null;
		}

		@Override
		public SessionFactoryImplementor getSessionFactory() {
			return sessionFactoryImplementor;
		}

		@Override
		public BootstrapContext getBootstrapContext() {
			return bootstrapContext;
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
		private SharedSessionContractImplementor session = null;
		
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
		public Object getIdentifier(Object object, SharedSessionContractImplementor s) {
			session = s;
			return object;
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
	
}
