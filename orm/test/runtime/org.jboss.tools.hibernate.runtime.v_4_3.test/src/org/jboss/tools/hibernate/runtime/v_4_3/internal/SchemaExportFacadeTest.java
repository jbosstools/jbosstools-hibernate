package org.jboss.tools.hibernate.runtime.v_4_3.internal;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class SchemaExportFacadeTest {

	private static final String FOO_HBM_XML_STRING =
			"<!DOCTYPE hibernate-mapping PUBLIC" +
			"		'-//Hibernate/Hibernate Mapping DTD 3.0//EN'" +
			"		'http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd'>" +
			"<hibernate-mapping package='org.jboss.tools.hibernate.runtime.v_4_3.internal'>" +
			"  <class name='ConfigurationFacadeTest$Foo' table='FOO'>" + 
			"    <id name='fooId' column='ID'/>" +
			"  </class>" +
			"</hibernate-mapping>";
	
	static class Foo {
		public String fooId;
	}
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	private File fooFile;
	private Configuration configuration;
	
	@Before
	public void before() throws Exception {
		File folder = temporaryFolder.getRoot();
		fooFile = new File(folder, "foo.hbm.xml");
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
		ISchemaExport schemaExportFacade = FACADE_FACTORY
				.createSchemaExport(new SchemaExport(configuration));
		Assert.assertFalse(connection.getMetaData().getTables(null, null, "FOO", null).next());
		schemaExportFacade.create();
		Assert.assertTrue(connection.getMetaData().getTables(null, null, "FOO", null).next());
	}
	
	@Test
	public void testGetExceptions() throws Throwable {
		String urlString = "jdbc:h2:tcp://localhost/~/some_weird_unexistant_location";
		configuration.setProperty(Environment.URL, urlString);
		SchemaExport schemaExport = new SchemaExport(configuration);
		ISchemaExport schemaExportFacade = FACADE_FACTORY
				.createSchemaExport(schemaExport);
		Assert.assertTrue(schemaExportFacade.getExceptions().isEmpty());
		schemaExport.create(false, true);
		Assert.assertFalse(schemaExportFacade.getExceptions().isEmpty());
	}

}
