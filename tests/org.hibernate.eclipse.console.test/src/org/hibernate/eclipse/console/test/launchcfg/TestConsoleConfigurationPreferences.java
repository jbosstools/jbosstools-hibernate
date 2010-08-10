package org.hibernate.eclipse.console.test.launchcfg;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import junit.framework.Assert;

import org.eclipse.core.runtime.FileLocator;
import org.hibernate.cfg.Environment;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;
import org.hibernate.eclipse.console.test.ConsoleTestMessages;
import org.hibernate.eclipse.console.test.HibernateConsoleTestPlugin;
import org.osgi.framework.Bundle;
import org.w3c.dom.Element;

public class TestConsoleConfigurationPreferences implements ConsoleConfigurationPreferences {

	public static final String HIBERNATE_CFG_XML_PATH = "/res/project/src/hibernate.cfg.xml".replaceAll("//", File.separator); //$NON-NLS-1$ //$NON-NLS-2$

	public static final String HIBERNATE_PROPERTIES_PATH = "/res/project/src/hibernate.properties".replaceAll("//", File.separator); //$NON-NLS-1$ //$NON-NLS-2$

	public void setName(String name) {
		Assert.fail();
	}

	public void readStateFrom(Element element) {
		Assert.fail();
	}

	public void writeStateTo(Element node) {
		Assert.fail();
	}

	public File getPropertyFile() {
		return null;
	}

	public File getConfigXMLFile() {
		File xmlConfig = null;
		Bundle bundle = HibernateConsoleTestPlugin.getDefault().getBundle();
		try {
			URL url = FileLocator.resolve(bundle.getEntry(HIBERNATE_CFG_XML_PATH));
			xmlConfig = new File(url.getFile());
		} catch (IOException e) {
			Assert.fail("Cannot find file: " + HIBERNATE_CFG_XML_PATH); //$NON-NLS-1$
		}
		return xmlConfig;
	}

	public Properties getProperties() {
		Properties p = new Properties();
		p.setProperty(Environment.DIALECT, "org.hibernate.dialect.HSQLDialect"); //$NON-NLS-1$
		return p;
	}

	public File[] getMappingFiles() {
		return new File[0];
	}

	public URL[] getCustomClassPathURLS() {
		return new URL[0];
	}

	public String getName() {
		return ConsoleTestMessages.ConsoleConfigurationTest_fake_prefs;
	}

	public String getEntityResolverName() {
		return ""; //$NON-NLS-1$
	}

	public ConfigurationMode getConfigurationMode() {
		return ConfigurationMode.CORE;
	}

	public String getNamingStrategy() {
		return null;
	}

	public String getPersistenceUnitName() {
		return null;
	}

	public String getConnectionProfileName() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hibernate.console.preferences.ConsoleConfigurationPreferences#getDialectName()
	 */
	public String getDialectName() {
		return null;
	}

}
