package org.jboss.tools.hibernate.orm.test.mapping;

import org.jboss.tools.hibernate.orm.test.utils.MappingTestHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TestName;

public class ValueTypeCollectionMapMappingTest {
	
	private static final String CFG_XML = 
		"<!DOCTYPE hibernate-configuration PUBLIC                                                                     \n" +
		"	'-//Hibernate/Hibernate Configuration DTD 3.0//EN'                                                        \n" +
		"	'http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd'>                                       \n" +
		"                                                                                                             \n" +
		"<hibernate-configuration>                                                                                    \n" +
		"	<session-factory>                                                                                         \n" +
		"       <property name='dialect'>org.hibernate.dialect.HSQLDialect</property>                                \n" +
		"       <mapping resource='/mapping/value/type/collection/map/CollectionMap.hbm.xml' /> \n" +
		"       <mapping resource='/mapping/value/type/collection/map/CollectionListMap.hbm.xml' /> \n" +
		"       <mapping resource='/mapping/value/type/collection/map/CollectionMapMany.hbm.xml' /> \n" +
		"	</session-factory>                                                                                        \n" +
		"</hibernate-configuration>                                                                                    " ;
	
	private static final String packageName = "mapping.value.type.collection.map";
	
	@ClassRule
	public static TestName testName = new TestName();
	
	private static MappingTestHelper mappingTestHelper = null;
	
	@BeforeClass
	public static void setUp() throws Exception {
		mappingTestHelper = new MappingTestHelper(CFG_XML, packageName, testName);
		mappingTestHelper.setUp();
	}
	
	@AfterClass
	public static void tearDown() {
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
