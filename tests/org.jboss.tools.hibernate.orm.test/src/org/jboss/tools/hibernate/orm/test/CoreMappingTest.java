package org.jboss.tools.hibernate.orm.test;

import java.util.Arrays;
import java.util.Collection;

import org.jboss.tools.hibernate.orm.test.utils.CoreMappingTestHelper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class CoreMappingTest {
	
    @Parameters(name = "{0}")
    public static Collection<Object[]> packages() {
        return Arrays.asList(new Object[][]{
    			{"core.abstractembeddedcomponents.cid"},
    			{"core.abstractembeddedcomponents.propertyref"},
    			{"core.any"},
    			{"core.array"},
    			{"core.batch"},
    			{"core.batchfetch"},
    			{"core.bytecode"},
    			{"core.cache"},
    			{"core.cascade"},
    			{"core.cid"},
        		{"core.collection.bag"},
			{"core.collection.idbag"},
			{"core.collection.list"},
			{"core.collection.map"},
			{"core.collection.original"},
			{"core.collection.set"},
			{"core.component.basic"},
			{"core.component.cascading.collection"},
			{"core.component.cascading.toone"},
			{"core.compositeelement"},
    			{"core.connections"},
        		{"core.criteria"},
        		{"core.cuk"},
        		{"core.cut"},
        		{"core.deletetransient"},
        		{"core.dialect.functional.cache"},
        		{"core.discriminator"},
        		{"core.dynamicentity.interceptor"},
        		{"core.dynamicentity.tuplizer"},
        		{"core.ecid"},
        		{"core.entitymode.dom4j.many2one"},
        		{"core.entitymode.multi"},
        		{"core.exception"},
        		{"core.extralazy"},
        		{"core.filter"},
        		{"core.generatedkeys.identity"},
        		{"core.generatedkeys.select"},
        		{"core.generatedkeys.seqidentity"},
        		{"core.id"},
        		{"core.idbag"},
        		{"core.idclass"},
        		{"core.idprops"},
        		{"core.immutable"},
        		{"core.insertordering"},
        		{"core.instrument.domain"},
        		{"core.interceptor"},
        		{"core.interfaceproxy"},
        		{"core.iterate"},
        		{"core.joinedsubclass"},
        		{"core.joinfetch"},
        		{"core.jpa"},
        		{"core.jpa.cascade"},
        		{"core.jpa.fetch"},
        		{"core.lazycache"},
        		{"core.lazyonetoone"},
        		{"core.lob"},
        		{"core.manytomany"},
            {"core.manytomany.ordered"},
            {"core.map"},
            {"core.mapcompelem"},
           	{"core.mapelemformula"},
        		{"core.mixed"},
        		{"core.naturalid"},
        		{"core.ondelete"},
        		{"core.onetoone.formula"},
        		{"core.onetoone.joined"},
        		{"core.onetoone.optional"},
        		{"core.onetoone.singletable"},
        		{"core.ops"},
        		{"core.optlock"},
        		{"core.ordered"},
        		{"core.orphan"},
        		{"core.pagination"},
        		{"core.propertyref.basic"},
       		{"core.propertyref.component.complete"},
        		{"core.propertyref.component.partial"},
        		{"core.propertyref.inheritence.discrim"},
        		{"core.propertyref.inheritence.joined"},
        		{"core.propertyref.inheritence.union"},
        		{"core.proxy"},
        		{"core.querycache"},
        		{"core.readonly"},
        		{"core.reattachment"},
        		{"core.rowid"},
        		{"core.sorted"},
        		{"core.stateless"},
        		{"core.subselect"},
        		{"core.subselectfetch"},
        		{"core.ternary"},
        		{"core.timestamp"},
        		{"core.tool"},
        		{"core.typedmanytoone"},
        		{"core.typedonetoone"},
        		{"core.typeparameters"},
        		{"core.unconstrained"},
        		{"core.unidir"},
        		{"core.unionsubclass"},
        		{"core.unionsubclass2"},
        		{"core.usercollection.basic"},
        		{"core.usercollection.parameterized"},
        		{"core.value.type.basic"},
        		{"core.value.type.collection.list"},
        		{"core.value.type.collection.map"},
        		{"core.value.type.collection.set"},
        		{"core.version.db"},
        		{"core.version.nodb"},
        		{"core.where"} 
        });
    }

	@ClassRule
	public static TestName testName = new TestName();
	
	private static CoreMappingTestHelper mappingTestHelper = null;
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		mappingTestHelper = new CoreMappingTestHelper(testName);
		mappingTestHelper.beforeClass();
	}
	
	@AfterClass
	public static void afterClass() {
		mappingTestHelper.afterClass();
		mappingTestHelper = null;
	}
	
	private String packageName = null;
	
	public CoreMappingTest(String packageName) {
		this.packageName = packageName;
	}
	
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
