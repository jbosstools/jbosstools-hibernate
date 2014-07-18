/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.tools.hibernate4_3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.DOMWriter;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.osgi.util.NLS;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.annotations.common.util.StandardClassLoaderDelegateImpl;
import org.hibernate.annotations.common.util.StringHelper;
import org.hibernate.cfg.Environment;
import org.hibernate.console.ConnectionProfileUtil;
import org.hibernate.console.ConsoleMessages;
import org.hibernate.console.HibernateConsoleRuntimeException;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences.ConfigurationMode;
import org.hibernate.internal.util.ConfigHelper;
import org.hibernate.internal.util.xml.XMLHelper;
import org.jboss.tools.hibernate.spi.IConfiguration;
import org.jboss.tools.hibernate.spi.INamingStrategy;
import org.jboss.tools.hibernate.spi.IService;
import org.jboss.tools.hibernate.util.HibernateHelper;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ConfigurationFactory {

	public static final String FAKE_TM_LOOKUP = "org.jboss.tools.hibernate4_3.FakeTransactionManagerLookup"; //$NON-NLS-1$
	
	private ConsoleConfigurationPreferences prefs;
	private Map<String, FakeDelegatingDriver> fakeDrivers;

	public ConfigurationFactory(ConsoleConfigurationPreferences prefs,
			Map<String, FakeDelegatingDriver> fakeDrivers) {
		this.prefs = prefs;
		this.fakeDrivers = fakeDrivers;
	}

	public ConsoleConfigurationPreferences getPreferences() {
		return prefs;
	}

	public IConfiguration createConfiguration(IConfiguration localCfg, boolean includeMappings) {
		Properties properties = prefs.getProperties();

		if (properties != null) {
			// in case the transaction manager is empty then we need to inject a faketm since
			// hibernate will still try and instantiate it.
			String str = properties.getProperty(Environment.TRANSACTION_STRATEGY);
			if (str != null && StringHelper.isEmpty(str)) {
				properties.setProperty(Environment.TRANSACTION_STRATEGY, FAKE_TM_LOOKUP);
				// properties.setProperty( "hibernate.transaction.factory_class", "");
			}
		}
		if (localCfg == null) {
			localCfg = buildConfiguration(properties, includeMappings);
		} else {
			// Properties origProperties = cfg.getProperties();
			// origProperties.putAll(properties);
			// cfg.setProperties(origProperties);
			// TODO: this is actually only for jdbc reveng...
			// localCfg = configureStandardConfiguration( includeMappings, localCfg, properties );
		}

		// here both setProperties and configxml have had their chance to tell which databasedriver
		// is needed.
		registerFakeDriver(localCfg.getProperty(Environment.DRIVER));
		// autoConfigureDialect(localCfg); Disabled for now since it causes very looong timeouts for
		// non-running databases + i havent been needed until now...

		// TODO: jpa configuration ?
		if (includeMappings) {
			File[] mappingFiles = prefs.getMappingFiles();

			for (int i = 0; i < mappingFiles.length; i++) {
				File hbm = mappingFiles[i];
				localCfg = localCfg.addFile(hbm);
			}
		}
		// TODO: HBX-
		localCfg.setProperty("hibernate.temp.use_jdbc_metadata_defaults", "false"); //$NON-NLS-1$//$NON-NLS-2$
		localCfg.setProperty(Environment.HBM2DDL_AUTO, "false"); //$NON-NLS-1$
		// to fix: JBIDE-5839 & JBIDE-5997 - setup this property: false is default value
		// to make hibernate tools diff hibernate versions compatible:
		// if the property not set get NoSuchMethodError with FullTextIndexEventListener
		if (localCfg.getProperty("hibernate.search.autoregister_listeners") == null) { //$NON-NLS-1$
			localCfg.setProperty("hibernate.search.autoregister_listeners", "false"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		return localCfg;
	}

	// TODO: delegate to some extension point
	private IConfiguration buildConfiguration(Properties properties, boolean includeMappings) {
		IConfiguration localCfg = null;
		if (prefs.getConfigurationMode().equals(ConfigurationMode.ANNOTATIONS)) {
			try {
				localCfg = buildAnnotationConfiguration();
				localCfg = configureStandardConfiguration(includeMappings, localCfg, properties);
			} catch (HibernateConsoleRuntimeException he) {
				throw he;
			} catch (Exception e) {
				throw new HibernateConsoleRuntimeException(
						ConsoleMessages.ConsoleConfiguration_could_not_load_annotationconfiguration,
						e);
			}
		} else if (prefs.getConfigurationMode().equals(ConfigurationMode.JPA)) {
			try {
				localCfg = buildJPAConfiguration(getPreferences().getPersistenceUnitName(),
						properties, prefs.getEntityResolverName(), includeMappings);
			} catch (HibernateConsoleRuntimeException he) {
				throw he;
			} catch (Exception e) {
				throw new HibernateConsoleRuntimeException(
						ConsoleMessages.ConsoleConfiguration_could_not_load_jpa_configuration, e);
			}
		} else {
			localCfg = HibernateHelper.INSTANCE.getHibernateService().newDefaultConfiguration();
			localCfg = configureStandardConfiguration(includeMappings, localCfg, properties);
		}
		return localCfg;
	}

	private IConfiguration buildAnnotationConfiguration() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		return HibernateHelper.INSTANCE.getHibernateService().newAnnotationConfiguration();
	}

	private IConfiguration buildJPAConfiguration(String persistenceUnit, Properties properties,
			String entityResolver, boolean includeMappings) {
		if (StringHelper.isEmpty(persistenceUnit)) {
			persistenceUnit = null;
		}
		try {
			Map<Object, Object> overrides = new HashMap<Object, Object>();
			if (properties != null) {
				overrides.putAll(properties);
			}
			if (StringHelper.isNotEmpty(prefs.getNamingStrategy())) {
				overrides.put("hibernate.ejb.naming_strategy", prefs.getNamingStrategy()); //$NON-NLS-1$
			}
			if (StringHelper.isNotEmpty(prefs.getDialectName())) {
				overrides.put(Environment.DIALECT, prefs.getDialectName());
			}
			if (!includeMappings) {
				overrides.put("hibernate.archive.autodetection", "none"); //$NON-NLS-1$//$NON-NLS-2$
			}
			if (StringHelper.isEmpty((String) overrides.get("javax.persistence.validation.mode"))) {//$NON-NLS-1$
				overrides.put("javax.persistence.validation.mode", "none"); //$NON-NLS-1$//$NON-NLS-2$
			}
			IConfiguration configuration = HibernateHelper.INSTANCE.getHibernateService().newJpaConfiguration(entityResolver, persistenceUnit, overrides);
			changeDatasourceProperties(configuration);
			configuration = configureConnectionProfile(configuration);
			return configuration;
		} catch (HibernateConsoleRuntimeException he) {
			throw he;
		} catch (Exception e) {
			throw new HibernateConsoleRuntimeException(
					ConsoleMessages.ConsoleConfiguration_could_not_create_jpa_based_configuration,
					e);
		}
	}

	private IConfiguration configureStandardConfiguration(final boolean includeMappings,
			IConfiguration localCfg, Properties properties) {
		if (properties != null) {
			localCfg = localCfg.setProperties(properties);
		}
		EntityResolver entityResolver = XMLHelper.DEFAULT_DTD_RESOLVER;
		if (StringHelper.isNotEmpty(prefs.getEntityResolverName())) {
			try {
				entityResolver = (EntityResolver) StandardClassLoaderDelegateImpl.INSTANCE.classForName(
						prefs.getEntityResolverName()).newInstance();
			} catch (Exception c) {
				throw new HibernateConsoleRuntimeException(
						ConsoleMessages.ConsoleConfiguration_could_not_configure_entity_resolver
								+ prefs.getEntityResolverName(), c);
			}
		}
		localCfg.setEntityResolver(entityResolver);
		if (StringHelper.isNotEmpty(prefs.getNamingStrategy())) {
			try {
				INamingStrategy ns = HibernateHelper.INSTANCE.getHibernateService().newNamingStrategy(
						prefs.getNamingStrategy());
				localCfg.setNamingStrategy(ns);
			} catch (Exception c) {
				throw new HibernateConsoleRuntimeException(
						ConsoleMessages.ConsoleConfiguration_could_not_configure_naming_strategy
								+ prefs.getNamingStrategy(), c);
			}
		}
		localCfg = loadConfigurationXML(localCfg, includeMappings, entityResolver);
		changeDatasourceProperties(localCfg);
		localCfg = configureConnectionProfile(localCfg);
		// replace dialect if it is set in preferences
		if (StringHelper.isNotEmpty(prefs.getDialectName())) {
			localCfg.setProperty(Environment.DIALECT, prefs.getDialectName());
		}
		if (StringHelper.isEmpty(localCfg.getProperty("javax.persistence.validation.mode"))) {//$NON-NLS-1$
			localCfg.setProperty("javax.persistence.validation.mode", "none"); //$NON-NLS-1$//$NON-NLS-2$
		}
		return localCfg;
	}

	@SuppressWarnings("unchecked")
	private IConfiguration loadConfigurationXML(IConfiguration localCfg, boolean includeMappings,
			EntityResolver entityResolver) {
		File configXMLFile = prefs.getConfigXMLFile();
		if (!includeMappings) {
			org.dom4j.Document doc;
			XMLHelper xmlHelper = new XMLHelper();
			InputStream stream = null;
			String resourceName = "<unknown>"; //$NON-NLS-1$
			if (configXMLFile != null) {
				resourceName = configXMLFile.toString();
				try {
					stream = new FileInputStream(configXMLFile);
				} catch (FileNotFoundException e1) {
					throw new HibernateConsoleRuntimeException(
							ConsoleMessages.ConsoleConfiguration_could_not_access + configXMLFile,
							e1);
				}
			} else {
				resourceName = "/hibernate.cfg.xml"; //$NON-NLS-1$
				if (checkHibernateResoureExistence(resourceName)) {
					stream = ConfigHelper.getResourceAsStream(resourceName); // simulate hibernate's
																				// default look up
				} else {
					return localCfg;
				}
			}
			try {
				final List<Throwable> errors = new ArrayList<Throwable>();
				ErrorHandler errorHandler = new ErrorHandler() {
										@Override
					public void warning(SAXParseException exception) throws SAXException {
						// ignore						
					}					
					@Override
					public void fatalError(SAXParseException exception) throws SAXException {
						error(exception);
					}					
					@Override
					public void error(SAXParseException exception) throws SAXException {
						errors.add(exception);
					}
				};
				doc = xmlHelper.createSAXReader(errorHandler, entityResolver).read(
						new InputSource(stream));
				if (errors.size() != 0) {
					throw new MappingException(
							ConsoleMessages.ConsoleConfiguration_invalid_configuration, errors
									.get(0));
				}
				List<Node> list = doc.getRootElement()
						.element("session-factory").elements("mapping"); //$NON-NLS-1$ //$NON-NLS-2$
				for (Node element : list) {
					element.getParent().remove(element);
				}
				DOMWriter dw = new DOMWriter();
				Document document = dw.write(doc);
				return localCfg.configure(document);

			} catch (DocumentException e) {
				throw new HibernateException(
						ConsoleMessages.ConsoleConfiguration_could_not_parse_configuration
								+ resourceName, e);
			} finally {
				try {
					if (stream != null)
						stream.close();
				} catch (IOException ioe) {
					// log.warn( "could not close input stream for: " + resourceName, ioe );
				}
			}
		} else {
			if (configXMLFile != null) {
				return localCfg.configure(configXMLFile);
			} else {
				IConfiguration resultCfg = localCfg;
				if (checkHibernateResoureExistence("/hibernate.cfg.xml")) { //$NON-NLS-1$
					resultCfg = localCfg.configure();
				}
				return resultCfg;
			}
		}
	}

	private boolean checkHibernateResoureExistence(String resource) {
		InputStream is = null;
		try {
			is = ConfigHelper.getResourceAsStream(resource);
		} catch (HibernateException e) {
			// just ignore
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				// ignore
			}
		}
		return (is != null);
	}
	
	private void changeDatasourceProperties(IConfiguration localCfg){
		final Properties invokeProperties = localCfg.getProperties();
		// set this property to null!
		if (invokeProperties.containsKey(Environment.DATASOURCE)){
			invokeProperties.setProperty(Environment.TRANSACTION_STRATEGY, FAKE_TM_LOOKUP);
			//invokeProperties.put(Environment.CONNECTION_PROVIDER, DriverManagerConnectionProvider.class.getName());
			invokeProperties.remove(Environment.DATASOURCE);
			localCfg.setProperties(invokeProperties);
		}
	}

	private IConfiguration configureConnectionProfile(IConfiguration localCfg) {
		String connProfileName = prefs.getConnectionProfileName();
		if (connProfileName == null) {
			return localCfg;
		}
		IConnectionProfile profile = ProfileManager.getInstance().getProfileByName(
				connProfileName);
		if (profile != null) {
			IService service = HibernateHelper.INSTANCE.getHibernateService();
			localCfg.addProperties(ConnectionProfileUtil.getHibernateConnectionProperties(service, profile));
		} else {
			String out = NLS.bind(
					ConsoleMessages.ConsoleConfiguration_connection_profile_not_found,
					connProfileName);
			throw new HibernateConsoleRuntimeException(out);
		}
		return localCfg;
	}

	/**
	 * DriverManager checks what classloader a class is loaded from thus we register a FakeDriver
	 * that we know is loaded "properly" which delegates all it class to the real driver. By doing
	 * so we can convince DriverManager that we can use any dynamically loaded driver.
	 * 
	 * @param driverClassName
	 */
	private void registerFakeDriver(String driverClassName) {
		if (driverClassName != null) {
			try {
				Class<Driver> driverClass = StandardClassLoaderDelegateImpl.INSTANCE.classForName(driverClassName);
				if (!fakeDrivers.containsKey(driverClassName)) { // To avoid "double registration"
					FakeDelegatingDriver fakeDelegatingDriver = new FakeDelegatingDriver(
							driverClass.newInstance());
					DriverManager.registerDriver(fakeDelegatingDriver);
					fakeDrivers.put(driverClassName, fakeDelegatingDriver);
				}
			} catch (InstantiationException e) {
				String out = 
					NLS.bind(ConsoleMessages.ConsoleConfiguration_problems_while_loading_database_driverclass, driverClassName);
				throw new HibernateConsoleRuntimeException(out, e);
			} catch (IllegalAccessException e) {
				String out = 
					NLS.bind(ConsoleMessages.ConsoleConfiguration_problems_while_loading_database_driverclass, driverClassName);
				throw new HibernateConsoleRuntimeException(out, e);
			} catch (SQLException e) {
				String out = 
					NLS.bind(ConsoleMessages.ConsoleConfiguration_problems_while_loading_database_driverclass, driverClassName);
				throw new HibernateConsoleRuntimeException(out, e);
			}
		}
	}

}
