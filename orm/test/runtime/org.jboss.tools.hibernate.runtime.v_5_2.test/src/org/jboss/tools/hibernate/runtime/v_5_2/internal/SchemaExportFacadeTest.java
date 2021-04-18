package org.jboss.tools.hibernate.runtime.v_5_2.internal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.HibernateException;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class SchemaExportFacadeTest {

	private static final String FOO_HBM_XML_STRING =
			"<!DOCTYPE hibernate-mapping PUBLIC" +
			"		'-//Hibernate/Hibernate Mapping DTD 3.0//EN'" +
			"		'http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd'>" +
			"<hibernate-mapping package='org.jboss.tools.hibernate.runtime.v_5_2.internal'>" +
			"  <class name='ConfigurationFacadeTest$Foo' table='FOO' >" + 
			"    <id name='fooId' type='string' column='ID' />" +
			"  </class>" +
			"</hibernate-mapping>";
	
	static class Foo {
		public String fooId;
	}
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	@TempDir
	public File tempDir = new File("tempDir");
	
	private File fooFile;
	private Configuration configuration;
	
	@BeforeEach
	public void beforeEach() throws Exception {
		fooFile = new File(tempDir, "foo.hbm.xml");
		PrintWriter fooWriter = new PrintWriter(fooFile);
		fooWriter.write(FOO_HBM_XML_STRING);
		fooWriter.close();
		configuration = new Configuration();
		configuration.addFile(fooFile);
	}
	
	@Test
	public void testCreate() throws Exception {
		String urlString = "jdbc:h2:mem:create_test";
		Connection connection = DriverManager.getConnection(urlString);
		configuration.setProperty(Environment.URL, urlString);
		IConfiguration configurationFacade = 
				FACADE_FACTORY.createConfiguration(configuration);
		SchemaExport schemaExport = new SchemaExport();
		SchemaExportFacadeImpl schemaExportFacade = 
				new SchemaExportFacadeImpl(FACADE_FACTORY, schemaExport);
		schemaExportFacade.setConfiguration(configurationFacade);
		assertFalse(connection.getMetaData().getTables(null, null, "FOO", null).next());
		schemaExportFacade.create();
		assertTrue(connection.getMetaData().getTables(null, null, "FOO", null).next());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetExceptions() throws Throwable {
		SchemaExport schemaExport = new SchemaExport();
		ISchemaExport schemaExportFacade = 
				new SchemaExportFacadeImpl(FACADE_FACTORY, schemaExport);
		assertTrue(schemaExportFacade.getExceptions().isEmpty());
		schemaExport.getExceptions().add(new HibernateException("blah"));
		assertFalse(schemaExportFacade.getExceptions().isEmpty());
	}

}
