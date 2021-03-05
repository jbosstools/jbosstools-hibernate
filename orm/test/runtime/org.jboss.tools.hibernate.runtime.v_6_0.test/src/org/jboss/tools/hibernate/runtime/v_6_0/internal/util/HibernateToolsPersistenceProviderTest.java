package org.jboss.tools.hibernate.runtime.v_6_0.internal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class HibernateToolsPersistenceProviderTest {
	
	private static final String PERSISTENCE_XML = 
			"<persistence version='2.2'" +
	        "  xmlns='http://xmlns.jcp.org/xml/ns/persistence'" +
		    "  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'" +
	        "  xsi:schemaLocation='http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd'>" +
	        "  <persistence-unit name='foobar'/>" +
			"</persistence>";
	
	private ClassLoader original = null;
	
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	
	@Before
	public void before() throws Exception {
		tempFolder.create();
		File tempRoot = tempFolder.getRoot();
		File metaInf = new File(tempRoot, "META-INF");
		metaInf.mkdirs();
		File persistenceXml = new File(metaInf, "persistence.xml");
		persistenceXml.createNewFile();
		FileWriter fileWriter = new FileWriter(persistenceXml);
		fileWriter.write(PERSISTENCE_XML);
		fileWriter.close();
		original = Thread.currentThread().getContextClassLoader();
		ClassLoader urlCl = URLClassLoader.newInstance(
				new URL[] { new URL(tempRoot.toURI().toURL().toString())} , 
				original);
		Thread.currentThread().setContextClassLoader(urlCl);
	}
	
	@After
	public void after() {
		Thread.currentThread().setContextClassLoader(original);
	}
	
	@Test
	public void testCreateEntityManagerFactoryBuilder() {
		Properties properties = new Properties();
		properties.put("foo", "bar");
		assertNull(
				HibernateToolsPersistenceProvider.createEntityManagerFactoryBuilder(
						"barfoo", properties));
		EntityManagerFactoryBuilderImpl entityManagerFactoryBuilder = 
				HibernateToolsPersistenceProvider.createEntityManagerFactoryBuilder(
						"foobar", properties);
		assertNotNull(entityManagerFactoryBuilder);
		assertEquals("bar", entityManagerFactoryBuilder.getConfigurationValues().get("foo"));
	}

}
