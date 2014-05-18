package org.hibernate.console.proxy;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.console.ConsoleMessages;
import org.hibernate.console.HibernateConsoleRuntimeException;
import org.hibernate.console.spi.HibernateConfiguration;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.util.xpl.ReflectHelper;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;

public class ConfigurationDefaultProxy implements HibernateConfiguration {
	
	public static HibernateConfiguration newStandardInstance() {
		return new ConfigurationDefaultProxy(new Configuration());
	}
	
	public static HibernateConfiguration newAnnotationInstance() {
		return new ConfigurationDefaultProxy(new AnnotationConfiguration());
	}
	
	public static HibernateConfiguration newJpaInstance(
			EntityResolver entityResolver,
			String persistenceUnit,
			Map<Object, Object> overrides) {
		Ejb3Configuration ejb3Configuraton = new Ejb3Configuration();
		ejb3Configuraton.setEntityResolver(entityResolver);
		ejb3Configuraton.configure(persistenceUnit, overrides);
		return new ConfigurationDefaultProxy(ejb3Configuraton.getHibernateConfiguration());
	}
	
	private Configuration configuration;
	
	private ConfigurationDefaultProxy(Configuration configuration) {
		this.configuration = configuration;
	}
	
	public String getProperty(String name) {
		return configuration.getProperty(name);
	}
	
	public void setProperty(String name, String value) {
		configuration.setProperty(name, value);
	}
	
	public HibernateConfiguration addFile(File file) {
		configuration = configuration.addFile(file);
		return this;
	}
	
	public Properties getProperties() {
		return configuration.getProperties();
	}
	
	public HibernateConfiguration setProperties(Properties properties) {
		configuration.setProperties(properties);
		return this;
	}
	
	public void addProperties(Properties properties) {
		configuration.addProperties(properties);
	}
	
	public HibernateConfiguration configure() {
		configuration = configuration.configure();
		return this;
	}
	
	public HibernateConfiguration configure(File file) {
		configuration = configuration.configure(file);
		return this;
	}
	
	public HibernateConfiguration configure(Document document) {
		configuration = configuration.configure(document);
		return this;
	}
	
	public void setEntityResolver(EntityResolver entityResolver) {
		configuration.setEntityResolver(entityResolver);
	}
	
	public void setNamingStrategy(String strategyName) {
		try {
			Class<?> namingStrategyClass = ReflectHelper.classForName(strategyName);
			configuration.setNamingStrategy((NamingStrategy)namingStrategyClass.newInstance());
		} catch (Exception c) {
			throw new HibernateConsoleRuntimeException(
				ConsoleMessages.ConsoleConfiguration_could_not_configure_naming_strategy
				+ strategyName, c);
			}
	}

}
