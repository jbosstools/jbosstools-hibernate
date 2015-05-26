package org.jboss.tools.hibernate.runtime.common;

import java.io.File;
import java.util.Map;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingGlobalSettings;
import org.jboss.tools.hibernate.runtime.spi.IPOJOClass;

public abstract class AbstractHibernateMappingExporterFacade 
extends AbstractFacade 
implements IHibernateMappingExporter {

	public AbstractHibernateMappingExporterFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	public void setGlobalSettings(IHibernateMappingGlobalSettings hmgs) {
		Object hmgsTarget = Util.invokeMethod(
				hmgs, 
				"getTarget", 
				new Class[] {}, 
				new Object[] {});
		Util.invokeMethod(
				getTarget(), 
				"setGlobalSettings", 
				new Class[] { getHibernateMappingGlobalSettingsClass() }, 
				new Object[] { hmgsTarget });
	}
	
	@Override
	public void start() {
		Util.invokeMethod(
				getTarget(), 
				"start", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public File getOutputDirectory() {
		return (File)Util.invokeMethod(
				getTarget(), 
				"getOutputDirectory", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public void setOutputDirectory(File directory) {
		Util.invokeMethod(
				getTarget(), 
				"setOutputDirectory", 
				new Class[] { File.class }, 
				new Object[] { directory });
	}

	@Override
	public void exportPOJO(Map<Object, Object> map, IPOJOClass pojoClass) {
		Object pojoClassTarget = Util.invokeMethod(
				pojoClass, 
				"getTarget", 
				new Class[] {}, 
				new Object[] {});
		Util.invokeMethod(
				getTarget(), 
				"superExportPOJO", 
				new Class[] { getPOJOClassClass() }, 
				new Object[] { pojoClassTarget });
	}

	protected Class<?> getHibernateMappingGlobalSettingsClass() {
		return Util.getClass(
				getHibernateMappingGlobalSettingsClassName(), 
				getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getPOJOClassClass() {
		return Util.getClass(
				getPOJOClassClassName(), 
				getFacadeFactoryClassLoader());
	}
	
	protected String getHibernateMappingGlobalSettingsClassName() {
		return "org.hibernate.tool.hbm2x.HibernateMappingGlobalSettings";
	}
	
	protected String getPOJOClassClassName() {
		return "org.hibernate.tool.hbm2x.pojo.POJOClass";
	}

}
