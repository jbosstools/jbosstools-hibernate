package org.jboss.tools.hibernate.spi;

import java.io.File;
import java.util.Properties;


public interface IExporter {

	void setConfiguration(IConfiguration configuration);
	void setProperties(Properties properties);
	void setArtifactCollector(IArtifactCollector collector);
	void setOutputDirectory(File file);
	void setTemplatePath(String[] strings);
	void start();
	Properties getProperties();
	IGenericExporter getGenericExporter();
	IHbm2DDLExporter getHbm2DDLExporter();
	IQueryExporter getQueryExporter();

}
