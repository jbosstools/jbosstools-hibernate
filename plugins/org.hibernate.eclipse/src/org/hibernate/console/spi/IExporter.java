package org.hibernate.console.spi;

import java.io.File;
import java.util.Properties;

import org.hibernate.tool.hbm2x.ArtifactCollector;
import org.hibernate.tool.hbm2x.GenericExporter;


public interface IExporter {

	void setConfiguration(IConfiguration configuration);
	void setProperties(Properties properties);
	void setArtifactCollector(ArtifactCollector collector);
	void setOutputDirectory(File file);
	void setTemplatePath(String[] strings);
	void start();
	Properties getProperties();
	GenericExporter getGenericExporter();

}
