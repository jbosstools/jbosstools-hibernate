package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

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
import org.hibernate.engine.spi.SessionFactoryImplementor;
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
import org.junit.Before;
import org.junit.Test;

public class ClassMetadataFacadeTest {
	
	private static final FacadeFactoryImpl FACADE_FACTORY = new FacadeFactoryImpl();
	
	private ClassMetadata classMetadataTarget;
	private ClassMetadataFacadeImpl classMetadataFacade;
	
	@Before
	public void before() throws Exception {
		classMetadataTarget = setupFooBarPersister();
		classMetadataFacade = new ClassMetadataFacadeImpl(FACADE_FACTORY, classMetadataTarget);
	}
	
	@Test
	public void testGetMappedClass() {
		assertSame(FooBar.class, classMetadataFacade.getMappedClass());
	}
	
	@Test
	public void testGetPropertyValue() {
		assertSame(PROPERTY_VALUE, classMetadataFacade.getPropertyValue(null, null));
	}
	
	@Test
	public void testGetIdentifierPropertyName() {
		assertEquals("foo", classMetadataFacade.getIdentifierPropertyName());
	}
	
	@Test
	public void testGetEntityName() {
		assertEquals("foobar", classMetadataFacade.getEntityName());
	}
	
	@Test
	public void testGetPropertyNames() {
		assertSame(PROPERTY_NAMES, classMetadataFacade.getPropertyNames());
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
		BasicValue sv = new BasicValue(metadataBuildingContext, t);
		sv.setNullValue("null");
		sv.setTypeName(Integer.class.getName());
		sv.addColumn(c);
		rc.setEntityName("foobar");
		rc.setIdentifier(sv);
		rc.setClassName(FooBar.class.getName());
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
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public MappingMetamodel getDomainModel() {
			// TODO Auto-generated method stub
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

	private static class TestEntityPersister extends SingleTableEntityPersister {
		
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
		
	}
	
	public class FooBar {
		public int id = 1967;
	}
	
}
