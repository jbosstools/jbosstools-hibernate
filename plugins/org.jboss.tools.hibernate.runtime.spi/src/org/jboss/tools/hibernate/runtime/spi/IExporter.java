package org.jboss.tools.hibernate.runtime.spi;

import java.io.File;
import java.io.StringWriter;
import java.util.Properties;


public interface IExporter {

	void setConfiguration(IConfiguration configuration);
	void setProperties(Properties properties);
	void setArtifactCollector(IArtifactCollector collector);
	void setOutputDirectory(File file);
	void setTemplatePath(String[] strings);
	void start() throws HibernateException;
	Properties getProperties();
	IGenericExporter getGenericExporter();
	IHbm2DDLExporter getHbm2DDLExporter();
	IQueryExporter getQueryExporter();
	void setCustomProperties(Properties props);
	void setOutput(StringWriter stringWriter);

}
