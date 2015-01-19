package org.jboss.tools.hibernate.spi;

import java.io.File;
import java.util.Map;

public interface IHibernateMappingExporter {

	void setGlobalSettings(IHibernateMappingGlobalSettings hmgs);
	void start();
	File getOutputDirectory();
	void setOutputDirectory(File directory);
	void exportPOJO(Map<Object, Object> map, IPOJOClass pojoClass);
	void setExportPOJODelegate(IHibernateMappingExporter delegate);

}
