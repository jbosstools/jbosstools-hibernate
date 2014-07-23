package org.jboss.tools.hibernate.spi;

import java.io.File;

public interface IHibernateMappingExporter {

	void setGlobalSettings(IHibernateMappingGlobalSettings hmgs);
	void start();
	File getOutputDirectory();
	void setOutputDirectory(File directory);

}
