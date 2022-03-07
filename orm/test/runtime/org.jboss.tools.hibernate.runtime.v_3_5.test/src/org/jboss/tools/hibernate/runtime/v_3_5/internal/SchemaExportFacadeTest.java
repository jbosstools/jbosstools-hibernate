package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;

import org.h2.Driver;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.HibernateException;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class SchemaExportFacadeTest {

	private static final String FOO_HBM_XML_STRING =
			"<!DOCTYPE hibernate-mapping PUBLIC" +
			"		'-//Hibernate/Hibernate Mapping DTD 3.0//EN'" +
			"		'http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd'>" +
			"<hibernate-mapping package='org.jboss.tools.hibernate.runtime.v_3_5.internal'>" +
			"  <class name='SchemaExportFacadeTest$Foo' table='FOO'>" + 
			"    <id name='fooId' column='ID'/>" +
			"  </class>" +
			"</hibernate-mapping>";
	
	static class Foo {
		public String fooId;
	}
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	@TempDir
	public File output = new File("output");
	
	private File fooFile;
	private Configuration configuration;
	
	@BeforeAll
	public static void beforeAll() throws Exception {
		DriverManager.registerDriver(new Driver());		
	}

	@BeforeEach
	public void before() throws Exception {
		fooFile = new File(output, "foo.hbm.xml");
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
		configuration.setProperty(Environment.DIALECT, H2Dialect.class.getName());
		ISchemaExport schemaExportFacade = FACADE_FACTORY
				.createSchemaExport(new SchemaExport(configuration));
		assertFalse(connection.getMetaData().getTables(null, null, "FOO", null).next());
		schemaExportFacade.create();
		assertTrue(connection.getMetaData().getTables(null, null, "FOO", null).next());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetExceptions() throws Throwable {
		String urlString = "jdbc:h2:mem:create_test";
		configuration.setProperty(Environment.URL, urlString);
		configuration.setProperty(Environment.DIALECT, H2Dialect.class.getName());
		SchemaExport schemaExport = new SchemaExport(configuration);
		ISchemaExport schemaExportFacade = FACADE_FACTORY
				.createSchemaExport(schemaExport);
		assertTrue(schemaExportFacade.getExceptions().isEmpty());
		schemaExport.getExceptions().add(new HibernateException("blah"));
		assertFalse(schemaExportFacade.getExceptions().isEmpty());
	}

}
