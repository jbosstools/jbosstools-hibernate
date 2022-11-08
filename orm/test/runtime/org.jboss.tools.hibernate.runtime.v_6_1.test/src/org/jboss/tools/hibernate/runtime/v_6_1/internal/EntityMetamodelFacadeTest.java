package org.jboss.tools.hibernate.runtime.v_6_1.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.OptimisticLockStyle;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.metamodel.MappingMetamodel;
import org.hibernate.metamodel.spi.RuntimeModelCreationContext;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.v_6_1.internal.util.DummyMetadataBuildingContext;
import org.jboss.tools.hibernate.runtime.v_6_1.internal.util.MockConnectionProvider;
import org.jboss.tools.hibernate.runtime.v_6_1.internal.util.MockDialect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EntityMetamodelFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	final static Object PROPERTY_VALUE = new Object();
	final static Integer PROPERTY_INDEX = Integer.MAX_VALUE;
	
	private EntityMetamodelFacadeImpl entityMetamodelFacade = null;
	
	@BeforeEach
	public void beforeEach() {
		entityMetamodelFacade = new EntityMetamodelFacadeImpl(
				FACADE_FACTORY, 
				createFooBarPersister());
	}
	
	@Test
	public void testInstance() {
		assertNotNull(entityMetamodelFacade);
	}
	
	@Test
	public void testGetTuplizerPropertyValue() {
		assertSame(PROPERTY_VALUE, entityMetamodelFacade.getTuplizerPropertyValue(null, 0));
	}
	
	@Test
	public void testGetPropertyIndexOrNull() {
		assertSame(PROPERTY_INDEX, entityMetamodelFacade.getPropertyIndexOrNull("foo"));
	}
	
	
	private EntityPersister createFooBarPersister() {
		return (EntityPersister)Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[] { EntityPersister.class }, 
				new TestInvocationHandler());
	}
	
	private static class TestInvocationHandler implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			String methodName = method.getName();
			if (methodName.equals("getEntityMetamodel")) {
				return FooBarMetamodel.INSTANCE;
			} else if (methodName.equals("getPropertyValue")) {
				return PROPERTY_VALUE;
			}
			return null;
		}
		
	}

	private static class FooBarMetamodel extends EntityMetamodel {

		private static final long serialVersionUID = 1L;

		private static FooBarMetamodel create() {
			StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
			builder.applySetting(AvailableSettings.DIALECT, MockDialect.class.getName());
			builder.applySetting(AvailableSettings.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
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
			MetadataSources metadataSources = new MetadataSources(serviceRegistry);
			RuntimeModelCreationContext runtimeModelCreationContext = 
					new TestCreationContext(
							bootstrapContext, 
							(MetadataImplementor)metadataSources.buildMetadata());
			RootClass rootClass = new RootClass(DummyMetadataBuildingContext.INSTANCE);
			SimpleValue basicValue = new BasicValue(metadataBuildingContext);
			basicValue.setTypeName(Integer.class.getName());
			rootClass.setIdentifier(basicValue);
			rootClass.setOptimisticLockStyle(OptimisticLockStyle.NONE);
			rootClass.setEntityName("foo");
			return new FooBarMetamodel(rootClass, runtimeModelCreationContext);
		}
		
		private static FooBarMetamodel INSTANCE = create();
		
		private FooBarMetamodel(
				PersistentClass persistentClass, 
				RuntimeModelCreationContext runtimeModelCreationContext) {
			super(persistentClass, null, runtimeModelCreationContext);
		}
		
		@Override
		public Integer getPropertyIndexOrNull(String id) {
			return PROPERTY_INDEX;
		}
		
	}
		
	private static class TestCreationContext implements RuntimeModelCreationContext {
		
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
	
	
}
