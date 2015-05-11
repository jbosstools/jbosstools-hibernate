package org.jboss.tools.hibernate.proxy;

import java.io.File;
import java.util.Map;

import org.hibernate.tool.hbm2x.HibernateMappingGlobalSettings;
import org.jboss.tools.hibernate.runtime.common.AbstractHibernateMappingExporterFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IExportPOJODelegate;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingGlobalSettings;
import org.jboss.tools.hibernate.runtime.spi.IPOJOClass;

public class HibernateMappingExporterProxy extends AbstractHibernateMappingExporterFacade {
	
	private HibernateMappingExporterExtension target = null;

	public HibernateMappingExporterProxy(
			IFacadeFactory facadeFactory, 
			HibernateMappingExporterExtension hibernateMappingExporter) {
		super(facadeFactory, hibernateMappingExporter);
		target = hibernateMappingExporter;
	}

	@Override
	public void setGlobalSettings(IHibernateMappingGlobalSettings hmgs) {
		assert hmgs instanceof IFacade;
		target.setGlobalSettings((HibernateMappingGlobalSettings)((IFacade)hmgs).getTarget());
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

	@Override
	public void exportPOJO(Map<Object, Object> map, IPOJOClass pojoClass) {
		assert pojoClass instanceof POJOClassFacadeImpl;
		target.superExportPOJO(map, ((POJOClassFacadeImpl)pojoClass).getTarget());
	}
	
	@Override
	public void setExportPOJODelegate(IExportPOJODelegate delegate) {
		target.setDelegate(delegate);
	}
	
}
