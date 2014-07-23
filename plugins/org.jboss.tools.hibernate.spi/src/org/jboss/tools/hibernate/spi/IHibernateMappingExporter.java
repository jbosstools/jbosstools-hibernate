package org.jboss.tools.hibernate.spi;

import java.io.File;

import org.hibernate.tool.hbm2x.HibernateMappingGlobalSettings;

public interface IHibernateMappingExporter {

	void setGlobalSettings(HibernateMappingGlobalSettings hmgs);
	void start();
	File getOutputDirectory();
	void setOutputDirectory(File directory);

}
