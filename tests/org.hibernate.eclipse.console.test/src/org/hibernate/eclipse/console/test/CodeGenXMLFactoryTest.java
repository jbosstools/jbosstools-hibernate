/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.console.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.internal.ConnectionProfile;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;
import org.hibernate.eclipse.console.ExtensionManager;
import org.hibernate.eclipse.console.model.impl.ExporterDefinition;
import org.hibernate.eclipse.console.model.impl.ExporterFactoryStrings;
import org.hibernate.eclipse.console.test.launchcfg.TestConsoleConfigurationPreferences;
import org.hibernate.eclipse.console.test.launchcfg.TestLaunchConfig;
import org.hibernate.eclipse.launch.CodeGenXMLFactory;
import org.hibernate.eclipse.launch.CodeGenerationStrings;
import org.hibernate.eclipse.launch.ExporterAttributes;
import org.hibernate.eclipse.launch.HibernateLaunchConstants;

import junit.framework.TestCase;

/**
 * @author Vitali Yemialyanchyk
 */
public class CodeGenXMLFactoryTest extends TestCase {

	public static final String LN_1 = "\n"; //$NON-NLS-1$
	public static final String LN_2 = "\r\n"; //$NON-NLS-1$

	public static final String SAMPLE_PATH = "res/sample/".replaceAll("//", File.separator); //$NON-NLS-1$ //$NON-NLS-2$
	public static final String PROJECT_LIB_PATH = "res/project/lib/".replaceAll("//", File.separator); //$NON-NLS-1$ //$NON-NLS-2$

	public static final String HBMTEMPLATE0 = "hbm2java"; //$NON-NLS-1$
	public static final String HBMTEMPLATE0_PROPERTIES = HibernateLaunchConstants.ATTR_EXPORTERS
			+ '.' + HBMTEMPLATE0 + ".properties"; //$NON-NLS-1$
	public static final String OUTDIR_PATH = "outputdir/test"; //$NON-NLS-1$

	public class TestConsoleConfigPref extends TestConsoleConfigurationPreferences {
		public File getConfigXMLFile() {
			final File xmlConfig = new File("project/src/hibernate.cfg.xml"); //$NON-NLS-1$
			return xmlConfig;
		}

		public File getPropertyFile() {
			final File propFile = new File("project/src/hibernate.properties"); //$NON-NLS-1$
			return propFile;
		}
	}

	public class TestConsoleConfigPref2 extends TestConsoleConfigurationPreferences {
		public File getConfigXMLFile() {
			return null;
		}

		public File[] getMappingFiles() {
			File[] files = new File[2];
			files[0] = new File("xxx.hbm.xml"); //$NON-NLS-1$
			files[1] = new File("yyy.hbm.xml"); //$NON-NLS-1$
			return files;
		}

		public URL[] getCustomClassPathURLS() {
			URL[] urls = new URL[4];
			try {
				urls[0] = new File("ejb3-persistence.jar").toURL(); //$NON-NLS-1$
				urls[1] = new File("hibernate3.jar").toURL(); //$NON-NLS-1$
				urls[2] = new File("hsqldb.jar").toURL(); //$NON-NLS-1$
				urls[3] = null;
			} catch (IOException e) {
			}
			return urls;
		}
		public String getEntityResolverName() {
			return ""; //$NON-NLS-1$
		}

		public ConfigurationMode getConfigurationMode() {
			return ConfigurationMode.JPA;
		}

		public String getNamingStrategy() {
			return "testNamingStrategy"; //$NON-NLS-1$
		}

		public String getPersistenceUnitName() {
			return "testPersistenceUnit"; //$NON-NLS-1$
		}

		public String getConnectionProfileName() {
			return "jdbc:mysql://localhost:3306/jpa"; //$NON-NLS-1$
		}

		public String getDialectName() {
			return "testDialect"; //$NON-NLS-1$
		}
	}

	public class TestConsoleConfigPref3 extends TestConsoleConfigPref {
		public String getConnectionProfileName() {
			return "connectionProfileName"; //$NON-NLS-1$
		}

		public String getDialectName() {
			return "testDialect"; //$NON-NLS-1$
		}
	}

	public enum ETestCase {
		simple,
		jpa,
		properties,
		nullable,
	}
	
	public class CodeGenXMLFactory4Test extends CodeGenXMLFactory {
		
		protected ETestCase testCase = ETestCase.simple;

		public CodeGenXMLFactory4Test(ILaunchConfiguration lc, ETestCase testCase) {
			super(lc);
			this.testCase = testCase;
		}

		public ConsoleConfigurationPreferences getConsoleConfigPreferences(String consoleConfigName) {
			ConsoleConfigurationPreferences pref = null;
			if (testCase == ETestCase.simple) {
				pref = new TestConsoleConfigPref();
			} else if (testCase == ETestCase.jpa) {
				pref = new TestConsoleConfigPref2();
			} else if (testCase == ETestCase.properties) {
				pref = new TestConsoleConfigPref3();
			}
			return pref;
		}

		public String getConnectionProfileDriverURL(String connectionProfile) {
			return "test-driver-path.jar"; //$NON-NLS-1$
		}

		public String getResLocation(String path) {
			return "reslocation/test"; //$NON-NLS-1$
		}

		public IConnectionProfile getConnectionProfile(String connProfileName) {
			IConnectionProfile profile = null;
			if (testCase == ETestCase.properties) {
				profile = new ConnectionProfile("testName", null, null) { //$NON-NLS-1$
					public Properties getProperties(String type) {
						Properties res = new Properties();
						res.setProperty("org.eclipse.datatools.connectivity.db.URL", "url"); //$NON-NLS-1$ //$NON-NLS-2$
						res.setProperty("org.eclipse.datatools.connectivity.db.username", "username"); //$NON-NLS-1$ //$NON-NLS-2$
						res.setProperty("org.eclipse.datatools.connectivity.db.password", "passw"); //$NON-NLS-1$ //$NON-NLS-2$
						return res;
					}
				};
			}
			return profile;
		}
		
		public String getDriverClass(String connProfileName) {
			String driverClass = ""; //$NON-NLS-1$
			if (testCase == ETestCase.properties) {
				driverClass = "driverClass"; //$NON-NLS-1$
			}
			return driverClass;
		}
	}
	
	public class CodeGenXMLFactory4Test2 extends CodeGenXMLFactory4Test {
		public CodeGenXMLFactory4Test2(ILaunchConfiguration lc, ETestCase testCase) {
			super(lc, testCase);
		}

		public ConsoleConfigurationPreferences getConsoleConfigPreferences(String consoleConfigName) {
			return new TestConsoleConfigPref2();
		}
	}

	public void testCodeGenXMLFactoryRevengAll() {
		CodeGenXMLFactory codeGenFactory = codeGenXMLFactory(true, true, ETestCase.simple);
		String codeGen = codeGenFactory.createCodeGenXML();
		String codeGenProperties = codeGenFactory.getPropFileContentPreSave();
		String specimen = getSample("AntCodeGenReveng_test1.xml"); //$NON-NLS-1$
		assertEquals(specimen.trim(), codeGen.replaceAll(LN_1, LN_2));
		assertEquals(codeGenProperties.length(), 0);
	}

	public void testCodeGenXMLFactoryRevengOne() {
		CodeGenXMLFactory codeGenFactory = codeGenXMLFactory(true, false, ETestCase.simple);
		String codeGen = codeGenFactory.createCodeGenXML();
		String codeGenProperties = codeGenFactory.getPropFileContentPreSave();
		String sample = getSample("AntCodeGenReveng_test2.xml"); //$NON-NLS-1$
		assertEquals(sample.trim(), codeGen.replaceAll(LN_1, LN_2));
		assertEquals(codeGenProperties.length(), 0);
	}

	public void testCodeGenXMLFactoryAll() {
		CodeGenXMLFactory codeGenFactory = codeGenXMLFactory(false, true, ETestCase.simple);
		String codeGen = codeGenFactory.createCodeGenXML();
		String codeGenProperties = codeGenFactory.getPropFileContentPreSave();
		String sample = getSample("AntCodeGen_test1.xml"); //$NON-NLS-1$
		assertEquals(sample.trim(), codeGen.replaceAll(LN_1, LN_2));
		assertEquals(codeGenProperties.length(), 0);
	}

	public void testCodeGenXMLFactoryOne() {
		CodeGenXMLFactory codeGenFactory = codeGenXMLFactory(false, false, ETestCase.simple);
		String codeGen = codeGenFactory.createCodeGenXML();
		String codeGenProperties = codeGenFactory.getPropFileContentPreSave();
		String sample = getSample("AntCodeGen_test2.xml"); //$NON-NLS-1$
		assertEquals(sample.trim(), codeGen.replaceAll(LN_1, LN_2));
		assertEquals(codeGenProperties.length(), 0);
	}

	public void testCodeGenXMLFactoryJpaAll() {
		CodeGenXMLFactory codeGenFactory = codeGenXMLFactory(false, true, ETestCase.jpa);
		String codeGen = codeGenFactory.createCodeGenXML();
		String codeGenProperties = codeGenFactory.getPropFileContentPreSave();
		codeGen = updatePaths(codeGen);
		String sample = getSample("AntCodeGenJpa_test1.xml"); //$NON-NLS-1$
		assertEquals(sample.trim(), codeGen.replaceAll(LN_1, LN_2));
		assertEquals(codeGenProperties.length(), 0);
	}

	public void testCodeGenXMLFactoryJpaOne() {
		CodeGenXMLFactory codeGenFactory = codeGenXMLFactory(false, false, ETestCase.jpa);
		String codeGen = codeGenFactory.createCodeGenXML();
		String codeGenProperties = codeGenFactory.getPropFileContentPreSave();
		codeGen = updatePaths(codeGen);
		String sample = getSample("AntCodeGenJpa_test2.xml"); //$NON-NLS-1$
		assertEquals(sample.trim(), codeGen.replaceAll(LN_1, LN_2));
		assertEquals(codeGenProperties.length(), 0);
	}

	public void testCodeGenXMLFactoryNullableAll() {
		CodeGenXMLFactory codeGenFactory = codeGenXMLFactory(false, true, ETestCase.nullable);
		String codeGen = codeGenFactory.createCodeGenXML();
		String codeGenProperties = codeGenFactory.getPropFileContentPreSave();
		codeGen = updatePaths(codeGen);
		String sample = getSample("AntCodeGenNullable_test1.xml"); //$NON-NLS-1$
		assertEquals(sample.trim(), codeGen.replaceAll(LN_1, LN_2));
		assertEquals(codeGenProperties.length(), 0);
	}

	public void testCodeGenXMLFactoryNullableOne() {
		CodeGenXMLFactory codeGenFactory = codeGenXMLFactory(false, false, ETestCase.nullable);
		String codeGen = codeGenFactory.createCodeGenXML();
		String codeGenProperties = codeGenFactory.getPropFileContentPreSave();
		codeGen = updatePaths(codeGen);
		String sample = getSample("AntCodeGenNullable_test2.xml"); //$NON-NLS-1$
		assertEquals(sample.trim(), codeGen.replaceAll(LN_1, LN_2));
		assertEquals(codeGenProperties.length(), 0);
	}

	public void testCodeGenXMLFactoryPropertiesAll() {
		CodeGenXMLFactory codeGenFactory = codeGenXMLFactory(false, true, ETestCase.properties);
		String codeGen = codeGenFactory.createCodeGenXML();
		String codeGenProperties = codeGenFactory.getPropFileContentPreSave();
		codeGen = updatePaths(codeGen);
		String sample = getSample("AntCodeGenProps_test1.xml"); //$NON-NLS-1$
		String sampleProperties = getSample("AntCodeGenProps.hibernate.properties"); //$NON-NLS-1$
		assertEquals(sample.trim(), codeGen.replaceAll(LN_1, LN_2));
		assertEquals(sampleProperties.trim(), codeGenProperties);
	}

	public void testCodeGenXMLFactoryPropertiesOne() {
		CodeGenXMLFactory codeGenFactory = codeGenXMLFactory(false, false, ETestCase.properties);
		String codeGen = codeGenFactory.createCodeGenXML();
		String codeGenProperties = codeGenFactory.getPropFileContentPreSave();
		codeGen = updatePaths(codeGen);
		String sample = getSample("AntCodeGenProps_test2.xml"); //$NON-NLS-1$
		String sampleProperties = getSample("AntCodeGenProps.hibernate.properties"); //$NON-NLS-1$
		assertEquals(sample.trim(), codeGen.replaceAll(LN_1, LN_2));
		assertEquals(sampleProperties.trim(), codeGenProperties);
	}

	public void testCodeGenXMLFactoryInternalPropertiesAll() {
		CodeGenXMLFactory codeGenFactory = codeGenXMLFactory(false, true, ETestCase.properties);
		codeGenFactory.setExternalPropFile(false);
		String codeGen = codeGenFactory.createCodeGenXML();
		String codeGenProperties = codeGenFactory.getPropFileContentPreSave();
		codeGen = updatePaths(codeGen);
		String sample = getSample("AntCodeGenInternalProps_test1.xml"); //$NON-NLS-1$
		String sampleProperties = getSample("AntCodeGenProps.hibernate.properties"); //$NON-NLS-1$
		assertEquals(sample.trim(), codeGen.replaceAll(LN_2, LN_1).replaceAll(LN_1, LN_2));
		assertEquals(sampleProperties.trim(), codeGenProperties);
	}

	public void testCodeGenXMLFactoryInternalPropertiesOne() {
		CodeGenXMLFactory codeGenFactory = codeGenXMLFactory(false, false, ETestCase.properties);
		codeGenFactory.setExternalPropFile(false);
		String codeGen = codeGenFactory.createCodeGenXML();
		String codeGenProperties = codeGenFactory.getPropFileContentPreSave();
		codeGen = updatePaths(codeGen);
		String sample = getSample("AntCodeGenInternalProps_test2.xml"); //$NON-NLS-1$
		String sampleProperties = getSample("AntCodeGenProps.hibernate.properties"); //$NON-NLS-1$
		assertEquals(sample.trim(), codeGen.replaceAll(LN_2, LN_1).replaceAll(LN_1, LN_2));
		assertEquals(sampleProperties.trim(), codeGenProperties);
	}
	
	public String updatePaths(String codeGen) {
		String repl = ""; //$NON-NLS-1$
		try {
			URI uri = new File("").toURL().toURI(); //$NON-NLS-1$
			repl = (new File(uri)).getPath();
		} catch (MalformedURLException e) {
		} catch (URISyntaxException e) {
		}
		return codeGen.replace(repl + File.separator, ""); //$NON-NLS-1$
	}

	public CodeGenXMLFactory codeGenXMLFactory(boolean reveng, boolean exportersAll, ETestCase testCase) {
		Map<String, ExporterDefinition> exDefinitions = ExtensionManager.findExporterDefinitionsAsMap();
		Map<String, Object> testLaunchConfigAttr = new HashMap<String, Object>();
		String tmp = "12345678901234567890"; //$NON-NLS-1$
		testLaunchConfigAttr.put(HibernateLaunchConstants.ATTR_TEMPLATE_DIR, tmp);
		testLaunchConfigAttr.put(HibernateLaunchConstants.ATTR_OUTPUT_DIR, tmp);
		testLaunchConfigAttr.put(HibernateLaunchConstants.ATTR_REVERSE_ENGINEER_SETTINGS, tmp);
		if (testCase == ETestCase.jpa) {
			testLaunchConfigAttr.put(HibernateLaunchConstants.ATTR_ENABLE_EJB3_ANNOTATIONS, true);
			testLaunchConfigAttr.put(HibernateLaunchConstants.ATTR_ENABLE_JDK5, true);
		}
		List<String> exportersList = new ArrayList<String>();
		if (exportersAll) {
			exportersList.clear();
		} else {
			exportersList.add(HBMTEMPLATE0);
		}
		for (Map.Entry<String, ExporterDefinition> exDef : exDefinitions.entrySet()) {
			String tmp0 = exDef.getValue().getExporterTag();
			String tmp1 = ExporterAttributes.getLaunchAttributePrefix(tmp0);
			testLaunchConfigAttr.put(tmp1 + ".extension_id", //$NON-NLS-1$ 
				HibernateLaunchConstants.ATTR_PREFIX + tmp0);
			testLaunchConfigAttr.put(tmp1, Boolean.TRUE);
			if (exportersAll) {
				exportersList.add(tmp0);
			}
		}
		testLaunchConfigAttr.put(HibernateLaunchConstants.ATTR_EXPORTERS, exportersList);
		Map<String, String> expProps2 = new HashMap<String, String>();
		// test properties overlap case: 
		// ExporterFactoryStrings.OUTPUTDIR & CodeGenerationStrings.DESTDIR - is a same property
		// ExporterFactoryStrings.OUTPUTDIR - is a GUI name for the property
		// CodeGenerationStrings.DESTDIR - is Ant script name for the property
		// GUI name is more preferable, i.e. "_test_suffix" should not be in generated file
		expProps2.put(ExporterFactoryStrings.OUTPUTDIR, OUTDIR_PATH);
		expProps2.put(CodeGenerationStrings.DESTDIR, OUTDIR_PATH + "_test_suffix"); //$NON-NLS-1$
		expProps2.put("keyXXX", "valueYYY"); //$NON-NLS-1$ //$NON-NLS-2$
		testLaunchConfigAttr.put(HBMTEMPLATE0_PROPERTIES, expProps2);
		testLaunchConfigAttr.put(HibernateLaunchConstants.ATTR_REVERSE_ENGINEER, reveng);
		TestLaunchConfig testLaunchConfig = new TestLaunchConfig(testLaunchConfigAttr);
		CodeGenXMLFactory codeGenFactory = new CodeGenXMLFactory4Test(testLaunchConfig, testCase);
		return codeGenFactory;
	}

	public String getSample(String fileName) {
		File resourceFile = null;
		try {
			resourceFile = getResourceItem(SAMPLE_PATH + fileName);
		} catch (IOException e1) {
		}
		if (resourceFile == null || !resourceFile.exists()) {
			return null;
		}
		StringBuffer cbuf = new StringBuffer((int) resourceFile.length());
		try {
			String ls = System.getProperties().getProperty("line.separator", LN_1);  //$NON-NLS-1$
			BufferedReader in = new BufferedReader(new FileReader(resourceFile));
			String str;
			while ((str = in.readLine()) != null) {
				cbuf.append(str + ls);
			}
			in.close();
		} catch (IOException e) {
		}
		return cbuf.toString();
	}

	protected File getResourceItem(String strResPath) throws IOException {
		IPath resourcePath = new Path(strResPath);
		File resourceFolder = resourcePath.toFile();
		URL entry = HibernateConsoleTestPlugin.getDefault().getBundle().getEntry(
				strResPath);
		String tplPrjLcStr = strResPath;
		if (entry != null) {
			URL resProject = FileLocator.resolve(entry);
			tplPrjLcStr = FileLocator.resolve(resProject).getFile();
		}
		resourceFolder = new File(tplPrjLcStr);
		return resourceFolder;
	}
}
