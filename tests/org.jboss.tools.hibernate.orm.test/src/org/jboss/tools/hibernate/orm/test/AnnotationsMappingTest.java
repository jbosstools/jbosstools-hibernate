package org.jboss.tools.hibernate.orm.test;

import org.jboss.tools.hibernate.orm.test.utils.AnnotationsMappingTestHelper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TestName;

public class AnnotationsMappingTest {
	
	@ClassRule
	public static TestName testName = new TestName();
	
	private static AnnotationsMappingTestHelper mappingTestHelper = null;
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		mappingTestHelper = new AnnotationsMappingTestHelper(testName);
		mappingTestHelper.beforeClass();
	}
	
	@AfterClass
	public static void afterClass() {
		mappingTestHelper.afterClass();
		mappingTestHelper = null;
	}
	
	private static String packageName = "annotations.database.schema";
	
	@Before
	public void before() throws Exception {
		mappingTestHelper.before(packageName);
	}
	
	@After
	public void after() throws Exception {
		mappingTestHelper.after(packageName);
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
		mappingTestHelper.testOpenMappingFileTest(packageName);
	}

	@Test
	public void testOpenSourceFileTest() {
		mappingTestHelper.testOpenSourceFileTest();
	}

	@Test
	public void testHbmExportExceptionTest() throws Exception {
		mappingTestHelper.testHbmExportExceptionTest(packageName);
	}
	
}
