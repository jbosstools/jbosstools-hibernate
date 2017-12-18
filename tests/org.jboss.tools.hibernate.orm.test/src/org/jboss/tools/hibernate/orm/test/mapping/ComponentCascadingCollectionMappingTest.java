package org.jboss.tools.hibernate.orm.test.mapping;

import org.jboss.tools.hibernate.orm.test.utils.MappingTestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class ComponentCascadingCollectionMappingTest {
	
	private static final String CFG_XML = 
		"<!DOCTYPE hibernate-configuration PUBLIC                                                                     \n" +
		"	'-//Hibernate/Hibernate Configuration DTD 3.0//EN'                                                        \n" +
		"	'http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd'>                                       \n" +
		"                                                                                                             \n" +
		"<hibernate-configuration>                                                                                    \n" +
		"	<session-factory>                                                                                         \n" +
		"       <property name='dialect'>org.hibernate.dialect.HSQLDialect</property>                                \n" +
		"       <mapping resource='/mapping/component/cascading/collection/Mappings.hbm.xml' /> \n" +
		"	</session-factory>                                                                                        \n" +
		"</hibernate-configuration>                                                                                    " ;
	
	private static final String packageName = "mapping.component.cascading.collection";
	
	@Rule
	public TestName testName = new TestName();
	
	private MappingTestHelper mappingTestHelper = null;
	
	@Before
	public void setUp() throws Exception {
		mappingTestHelper = new MappingTestHelper(CFG_XML, packageName, testName);
		mappingTestHelper.setUp();
	}
	
	@After
	public void tearDown() {
		mappingTestHelper.tearDown();
		mappingTestHelper = null;
	}
	
	@Test
	public void testCheckConsoleConfiguration() {
		mappingTestHelper.testCheckConsoleConfiguration();
	}

	@Test
	public void testOpenMappingDiagram() {
		mappingTestHelper.testOpenMappingDiagram();
	}
	
	@Test
	public void testOpenMappingFileTest() {
		mappingTestHelper.testOpenMappingFileTest();
	}

	@Test
	public void testOpenSourceFileTest() {
		mappingTestHelper.testOpenSourceFileTest();
	}

	@Test
	public void testHbmExportExceptionTest() throws Exception {
		mappingTestHelper.testHbmExportExceptionTest();
	}
	
}
