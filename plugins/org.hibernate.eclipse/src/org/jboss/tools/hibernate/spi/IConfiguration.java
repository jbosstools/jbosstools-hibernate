package org.jboss.tools.hibernate.spi;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;

import org.hibernate.cfg.Settings;
import org.hibernate.mapping.PersistentClass;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;

public interface IConfiguration {

	String getProperty(String driver);
	IConfiguration addFile(File file);
	void setProperty(String name, String value);
	IConfiguration setProperties(Properties properties);
	void setEntityResolver(EntityResolver entityResolver);
	void setNamingStrategy(INamingStrategy namingStrategy);
	Properties getProperties();
	void addProperties(Properties properties);
	IConfiguration configure(Document document);
	IConfiguration configure(File file);
	IConfiguration configure();
	void buildMappings();
	ISessionFactory buildSessionFactory();
	Settings buildSettings();
	IMappings createMappings();
	Iterator<? extends PersistentClass> getClassMappings();
	void setPreferBasicCompositeIds(boolean preferBasicCompositeids);
	void setReverseEngineeringStrategy(IReverseEngineeringStrategy res);
	void readFromJDBC();
	Object buildMapping();
	PersistentClass getClassMapping(String string);
	INamingStrategy getNamingStrategy();
	EntityResolver getEntityResolver();
	ISessionFactory buildSessionFactory(Object serviceRegistry);
	Object buildSettings(Object serviceRegistry);

}
