package org.hibernate.console.spi;

import java.io.File;
import java.util.Properties;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;

public interface HibernateConfiguration {
	
	String getProperty(String name);
	void setProperty(String name, String value);
	HibernateConfiguration addFile(File file);
	Properties getProperties();
	HibernateConfiguration setProperties(Properties properties);
	void addProperties(Properties properties);
	HibernateConfiguration configure();
	HibernateConfiguration configure(File file);
	HibernateConfiguration configure(Document document);
	void setEntityResolver(EntityResolver entityResolver);
	void setNamingStrategy(String strategyName);
	void buildMappings();
	HibernateSessionFactory buildSessionFactory();
	HibernateSettings buildSettings();

}
