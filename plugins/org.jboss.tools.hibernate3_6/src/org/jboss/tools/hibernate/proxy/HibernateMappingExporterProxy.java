package org.jboss.tools.hibernate.proxy;

import java.io.File;

import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.jboss.tools.hibernate.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.spi.IHibernateMappingGlobalSettings;

public class HibernateMappingExporterProxy implements IHibernateMappingExporter {
	
	private HibernateMappingExporter target = null;

	public HibernateMappingExporterProxy(
			HibernateMappingExporter hibernateMappingExporter) {
		target = hibernateMappingExporter;
	}

	@Override
	public void setGlobalSettings(IHibernateMappingGlobalSettings hmgs) {
		assert hmgs instanceof HibernateMappingGlobalSettingsProxy;
		target.setGlobalSettings(((HibernateMappingGlobalSettingsProxy)hmgs).getTarget());
	}

	@Override
	public void start() {
		target.start();
	}

	@Override
	public File getOutputDirectory() {
		return target.getOutputDirectory();
	}

	@Override
	public void setOutputDirectory(File directory) {
		target.setOutputDirectory(directory);
	}

}
