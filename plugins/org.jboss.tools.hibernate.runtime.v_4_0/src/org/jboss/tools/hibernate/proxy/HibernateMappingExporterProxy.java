package org.jboss.tools.hibernate.proxy;

import java.io.File;
import java.util.Map;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbm2x.HibernateMappingGlobalSettings;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingGlobalSettings;
import org.jboss.tools.hibernate.runtime.spi.IPOJOClass;

public class HibernateMappingExporterProxy implements IHibernateMappingExporter {
	
	private HibernateMappingExporterWrapper target = null;
	private IHibernateMappingExporter exportPOJODelegate = null;

	public HibernateMappingExporterProxy(IConfiguration configuration, File file) {
		assert configuration instanceof ConfigurationProxy;
		target = new HibernateMappingExporterWrapper(
				((ConfigurationProxy)configuration).getTarget(),
				file);
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
		assert pojoClass instanceof POJOClassProxy;
		target.superExportPOJO(map, ((POJOClassProxy)pojoClass).getTarget());
	}
	
	@Override
	public void setExportPOJODelegate(IHibernateMappingExporter delegate) {
		exportPOJODelegate = delegate;
	}
	
	private class HibernateMappingExporterWrapper extends HibernateMappingExporter {
		HibernateMappingExporterWrapper(Configuration cfg, File file) {
			super(cfg, file);
		}
		void superExportPOJO(Map<Object, Object> map, POJOClass pojoClass) {
			super.exportPOJO(map, pojoClass);
		}
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		protected void exportPOJO(Map map, POJOClass pojoClass) {
			if (exportPOJODelegate == null) {
				super.exportPOJO(map, pojoClass);
			} else {
				exportPOJODelegate.exportPOJO(
						(Map<Object, Object>)map, 
						new POJOClassProxy(pojoClass));
			}
		}
	}

}
