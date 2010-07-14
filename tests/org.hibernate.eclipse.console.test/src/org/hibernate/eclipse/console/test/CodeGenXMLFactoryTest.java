/*******************************************************************************
 * Copyright (c) 2007-2010 Red Hat, Inc.
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
import java.io.ByteArrayOutputStream;
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

import org.dom4j.Element;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.hibernate.console.ConfigurationXMLFactory;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;
import org.hibernate.eclipse.console.ExtensionManager;
import org.hibernate.eclipse.console.model.impl.ExporterDefinition;
import org.hibernate.eclipse.console.test.launchcfg.TestConsoleConfigurationPreferences;
import org.hibernate.eclipse.console.test.launchcfg.TestLaunchConfig;
import org.hibernate.eclipse.launch.CodeGenXMLFactory;
import org.hibernate.eclipse.launch.ExporterAttributes;
import org.hibernate.eclipse.launch.HibernateLaunchConstants;

import junit.framework.TestCase;

/**
 * @author Vitali Yemialyanchyk
 */
public class CodeGenXMLFactoryTest extends TestCase {

	public static final String SPECIMEN_PATH = "res/specimen/".replaceAll("//", File.separator); //$NON-NLS-1$ //$NON-NLS-2$
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

	public class CodeGenXMLFactory4Test extends CodeGenXMLFactory {
		
		protected boolean jpa = false;

		public CodeGenXMLFactory4Test(ILaunchConfiguration lc, boolean jpa) {
			super(lc);
			this.jpa = jpa;
		}

		public ConsoleConfigurationPreferences getConsoleConfigPreferences(String consoleConfigName) {
			ConsoleConfigurationPreferences pref;
			if (jpa) {
				pref = new TestConsoleConfigPref2();
			} else {
				pref = new TestConsoleConfigPref();
			}
			return pref;
		}

		public String getConnectionProfileDriverURL(String connectionProfile) {
			return "TestDriverPath.jar"; //$NON-NLS-1$
		}

		public String getResLocation(String path) {
			return "ResLocation/test"; //$NON-NLS-1$
		}
	}

	public void testCodeGenXMLFactoryRevengAll() {
		String codeGen = codeGenXMLFactory(true, true, false);
		String specimen = getSpecimen("AntCodeGenReveng_test1.xml"); //$NON-NLS-1$
		assertEquals(specimen.trim(), codeGen.trim().replaceAll("\n", "\r\n")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testCodeGenXMLFactoryRevengOne() {
		String codeGen = codeGenXMLFactory(true, false, false);
		String specimen = getSpecimen("AntCodeGenReveng_test2.xml"); //$NON-NLS-1$
		assertEquals(specimen.trim(), codeGen.trim().replaceAll("\n", "\r\n")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testCodeGenXMLFactoryAll() {
		String codeGen = codeGenXMLFactory(false, true, false);
		String specimen = getSpecimen("AntCodeGen_test1.xml"); //$NON-NLS-1$
		assertEquals(specimen.trim(), codeGen.trim().replaceAll("\n", "\r\n")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testCodeGenXMLFactoryOne() {
		String codeGen = codeGenXMLFactory(false, false, false);
		String specimen = getSpecimen("AntCodeGen_test2.xml"); //$NON-NLS-1$
		assertEquals(specimen.trim(), codeGen.trim().replaceAll("\n", "\r\n")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testCodeGenXMLFactoryJpaAll() {
		String codeGen = codeGenXMLFactory(false, true, true);
		codeGen = updatePaths(codeGen);
		String specimen = getSpecimen("AntCodeGenJpa_test1.xml"); //$NON-NLS-1$
		assertEquals(specimen.trim(), codeGen.trim().replaceAll("\n", "\r\n")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testCodeGenXMLFactoryJpaOne() {
		String codeGen = codeGenXMLFactory(false, false, true);
		codeGen = updatePaths(codeGen);
		String specimen = getSpecimen("AntCodeGenJpa_test2.xml"); //$NON-NLS-1$
		assertEquals(specimen.trim(), codeGen.trim().replaceAll("\n", "\r\n")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public String updatePaths(String codeGen) {
		String repl = ""; //$NON-NLS-1$
		try {
			URI uri = new File("").toURL().toURI(); //$NON-NLS-1$
			repl = (new File(uri)).getPath();
		} catch (MalformedURLException e) {
		} catch (URISyntaxException e) {
		}
		codeGen = CodeGenXMLFactory.replaceString(codeGen, repl + File.separator, ""); //$NON-NLS-1$
		return codeGen;
	}

	public String codeGenXMLFactory(boolean reveng, boolean exportersAll, boolean jpa) {
		Map<String, ExporterDefinition> exDefinitions = ExtensionManager.findExporterDefinitionsAsMap();
		Map<String, Object> testLCAttr = new HashMap<String, Object>();
		String tmp = "12345678901234567890"; //$NON-NLS-1$
		testLCAttr.put(HibernateLaunchConstants.ATTR_TEMPLATE_DIR, tmp);
		testLCAttr.put(HibernateLaunchConstants.ATTR_OUTPUT_DIR, tmp);
		testLCAttr.put(HibernateLaunchConstants.ATTR_REVERSE_ENGINEER_SETTINGS, tmp);
		if (jpa) {
			testLCAttr.put(HibernateLaunchConstants.ATTR_ENABLE_EJB3_ANNOTATIONS, true);
			testLCAttr.put(HibernateLaunchConstants.ATTR_ENABLE_JDK5, true);
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
			testLCAttr.put(tmp1 + ".extension_id", //$NON-NLS-1$ 
				HibernateLaunchConstants.ATTR_PREFIX + tmp0);
			testLCAttr.put(tmp1, Boolean.TRUE);
			if (exportersAll) {
				exportersList.add(tmp0);
			}
		}
		testLCAttr.put(HibernateLaunchConstants.ATTR_EXPORTERS, exportersList);
		Map<String, String> expProps2 = new HashMap<String, String>();
		expProps2.put(HibernateLaunchConstants.ATTR_OUTPUT_DIR, OUTDIR_PATH);
		testLCAttr.put(HBMTEMPLATE0_PROPERTIES, expProps2);
		testLCAttr.put(HibernateLaunchConstants.ATTR_REVERSE_ENGINEER, reveng);
		TestLaunchConfig testLC = new TestLaunchConfig(testLCAttr);
		CodeGenXMLFactory cgfXML = new CodeGenXMLFactory4Test(testLC, jpa);
		Element rootBuildXml = cgfXML.createRoot();
		ConfigurationXMLFactory.dump(System.out, rootBuildXml);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ConfigurationXMLFactory.dump(baos, rootBuildXml);
		return baos.toString();
	}

	public String getSpecimen(String fileName) {
		File resourceFile = null;
		try {
			resourceFile = getResourceItem(SPECIMEN_PATH + fileName);
		} catch (IOException e1) {
		}
		if (resourceFile == null || !resourceFile.exists()) {
			return null;
		}
		StringBuffer cbuf = new StringBuffer((int) resourceFile.length());
		try {
			String ls = System.getProperties().getProperty("line.separator", "\n");  //$NON-NLS-1$//$NON-NLS-2$
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
