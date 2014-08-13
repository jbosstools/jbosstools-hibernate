package org.jboss.tools.hibernate.spi;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;

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
	ISettings buildSettings();
	IMappings createMappings();
	Iterator<IPersistentClass> getClassMappings();
	void setPreferBasicCompositeIds(boolean preferBasicCompositeids);
	void setReverseEngineeringStrategy(IReverseEngineeringStrategy res);
	void readFromJDBC();
	IMapping buildMapping();
	IPersistentClass getClassMapping(String string);
	INamingStrategy getNamingStrategy();
	EntityResolver getEntityResolver();
	Object buildSettings(Object serviceRegistry);
	Iterator<ITable> getTableMappings();
	IDialect getDialect();

}
