package org.jboss.tools.hibernate.orm.test.utils;

import java.io.File;
import java.net.URL;
import java.util.Properties;

import org.hibernate.console.preferences.ConsoleConfigurationPreferences;
import org.jboss.tools.hibernate.runtime.spi.IEnvironment;
import org.jboss.tools.hibernate.runtime.spi.IService;
import org.jboss.tools.hibernate.runtime.spi.RuntimeServiceManager;
import org.w3c.dom.Element;

public class TestConsoleConfigurationPreferences implements ConsoleConfigurationPreferences {

	private File cfgXmlFile = null;
	
	public TestConsoleConfigurationPreferences(File file) {
		cfgXmlFile = file;
	}
	
	public void setName(String name) {
		throw new RuntimeException("not supported");
	}

	public void readStateFrom(Element element) {
		throw new RuntimeException("not supported");
	}

	public void writeStateTo(Element node) {
		throw new RuntimeException("not supported");
	}

	public File getPropertyFile() {
		return null;
	}

	public File getConfigXMLFile() {
		return cfgXmlFile;
	}

	public Properties getProperties() {
		IEnvironment environment = getService().getEnvironment();
		Properties p = new Properties();
		p.setProperty(environment.getDialect(), "org.hibernate.dialect.HSQLDialect"); //$NON-NLS-1$
		return p;
	}

	public File[] getMappingFiles() {
		return new File[0];
	}

	public URL[] getCustomClassPathURLS() {
		return new URL[0];
	}

	public String getName() {
		return TestConsoleMessages.ConsoleConfigurationTest_fake_prefs;
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

	/* (non-Javadoc)
	 * @see org.hibernate.console.preferences.ConsoleConfigurationPreferences#getHibernateVersion()
	 */
	@Override
	public String getHibernateVersion() {
		String[] versions = RuntimeServiceManager.getInstance().getAllVersions();
		return versions[versions.length - 1];
	}
	
	private IService getService() {
		return RuntimeServiceManager.findService(getHibernateVersion());
	}

}
