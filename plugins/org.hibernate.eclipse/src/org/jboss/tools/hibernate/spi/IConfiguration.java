package org.jboss.tools.hibernate.spi;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Mappings;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.cfg.Settings;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.mapping.PersistentClass;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;

public interface IConfiguration {

	String getProperty(String driver);
	IConfiguration addFile(File file);
	void setProperty(String name, String value);
	IConfiguration setProperties(Properties properties);
	void setEntityResolver(EntityResolver entityResolver);
	void setNamingStrategy(NamingStrategy namingStrategy);
	Properties getProperties();
	void addProperties(Properties properties);
	IConfiguration configure(Document document);
	IConfiguration configure(File file);
	IConfiguration configure();
	void buildMappings();
	SessionFactory buildSessionFactory();
	Settings buildSettings();
	Mappings createMappings();
	Iterator<? extends PersistentClass> getClassMappings();
	void setPreferBasicCompositeIds(boolean preferBasicCompositeids);
	void setReverseEngineeringStrategy(ReverseEngineeringStrategy res);
	void readFromJDBC();
	Object buildMapping();
	PersistentClass getClassMapping(String string);
	NamingStrategy getNamingStrategy();
	EntityResolver getEntityResolver();
	SessionFactory buildSessionFactory(Object serviceRegistry);
	Object buildSettings(Object serviceRegisrty);

}
