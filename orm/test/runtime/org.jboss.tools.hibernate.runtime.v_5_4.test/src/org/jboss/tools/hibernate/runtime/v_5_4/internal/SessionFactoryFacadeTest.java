package org.jboss.tools.hibernate.runtime.v_5_4.internal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.Metamodel;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.ICollectionMetadata;
import org.jboss.tools.hibernate.runtime.spi.ISession;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.runtime.v_5_4.internal.test.Foo;
import org.junit.jupiter.api.Test;

public class SessionFactoryFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	@Test
	public void testClose() {
		Configuration configuration = new Configuration();
		SessionFactory sessionFactory = 
				configuration.buildSessionFactory(
						new StandardServiceRegistryBuilder().build());
		sessionFactory.openSession();
		ISessionFactory sessionFactoryFacade = 
				FACADE_FACTORY.createSessionFactory(sessionFactory);
		assertFalse(sessionFactory.isClosed());
		sessionFactoryFacade.close();
		assertTrue(sessionFactory.isClosed());
	}
	
	@Test
	public void testGetAllClassMetadata() throws Exception {
		Configuration configuration = new Configuration();
		SessionFactory sessionFactory = 
				configuration.buildSessionFactory(
						new StandardServiceRegistryBuilder().build());
		ISessionFactory sessionFactoryFacade = 
				FACADE_FACTORY.createSessionFactory(sessionFactory);
		assertTrue(sessionFactoryFacade.getAllClassMetadata().isEmpty());
		sessionFactory.close();
		configuration.addClass(Foo.class);
		sessionFactory = 
				configuration.buildSessionFactory(
						new StandardServiceRegistryBuilder().build());
		sessionFactoryFacade = 
				FACADE_FACTORY.createSessionFactory(sessionFactory);
		Map<String, IClassMetadata> allClassMetaData = 
				sessionFactoryFacade.getAllClassMetadata();
		assertNotNull(
				allClassMetaData.get(
						"org.jboss.tools.hibernate.runtime.v_5_4.internal.test.Foo"));
	}
	
	@Test
	public void testGetAllCollectionMetadata() {
		Configuration configuration = new Configuration();
		SessionFactory sessionFactory = 
				configuration.buildSessionFactory(
						new StandardServiceRegistryBuilder().build());
		ISessionFactory sessionFactoryFacade = 
				FACADE_FACTORY.createSessionFactory(sessionFactory);
		assertTrue(sessionFactoryFacade.getAllCollectionMetadata().isEmpty());
		sessionFactory.close();
		configuration.addClass(Foo.class);
		sessionFactory = 
				configuration.buildSessionFactory(
						new StandardServiceRegistryBuilder().build());
		sessionFactoryFacade = 
				FACADE_FACTORY.createSessionFactory(sessionFactory);
		Map<String, ICollectionMetadata> allCollectionMetaData = 
				sessionFactoryFacade.getAllCollectionMetadata();
		assertNotNull(
				allCollectionMetaData.get(
						"org.jboss.tools.hibernate.runtime.v_5_4.internal.test.Foo.bars"));
	}
	
	@Test
	public void testOpenSession() {
		Configuration configuration = new Configuration();
		SessionFactory sessionFactory = 
				configuration.buildSessionFactory(
						new StandardServiceRegistryBuilder().build());
		ISessionFactory sessionFactoryFacade = 
				FACADE_FACTORY.createSessionFactory(sessionFactory);
		ISession sessionFacade = sessionFactoryFacade.openSession();
		Session session = (Session)((IFacade)sessionFacade).getTarget();
		assertSame(sessionFactory, session.getSessionFactory());
	}
	
	@Test
	public void testGetClassMetadata() {
		Configuration configuration = new Configuration();
		configuration.addClass(Foo.class);
		SessionFactory sessionFactory = 
				configuration.buildSessionFactory(
						new StandardServiceRegistryBuilder().build());
		Metamodel metamodel = ((EntityManagerFactory)sessionFactory).getMetamodel();	
		ClassMetadata classMetadata = (ClassMetadata)((MetamodelImplementor)metamodel).entityPersister(Foo.class);
		ISessionFactory sessionFactoryFacade = 
				FACADE_FACTORY.createSessionFactory(sessionFactory);
		assertSame(
				classMetadata, 
				((IFacade)sessionFactoryFacade.getClassMetadata(Foo.class)).getTarget());
		assertSame(
				classMetadata, 
				((IFacade)sessionFactoryFacade.getClassMetadata(
						"org.jboss.tools.hibernate.runtime.v_5_4.internal.test.Foo"))
					.getTarget());
	}
	
	@Test
	public void testGetCollectionMetadata() {
		Configuration configuration = new Configuration();
		configuration.addClass(Foo.class);
		SessionFactory sessionFactory = 
				configuration.buildSessionFactory(
						new StandardServiceRegistryBuilder().build());
		ISessionFactory sessionFactoryFacade = 
				FACADE_FACTORY.createSessionFactory(sessionFactory);
		Metamodel metamodel = ((EntityManagerFactory)sessionFactory).getMetamodel();	
		CollectionMetadata collectionMetadata = (CollectionMetadata)((MetamodelImplementor)metamodel)
				.collectionPersister("org.jboss.tools.hibernate.runtime.v_5_4.internal.test.Foo.bars");
		assertSame(
				collectionMetadata, 
				((IFacade)sessionFactoryFacade.getCollectionMetadata(
						"org.jboss.tools.hibernate.runtime.v_5_4.internal.test.Foo.bars"))
					.getTarget());
	}
	
}
