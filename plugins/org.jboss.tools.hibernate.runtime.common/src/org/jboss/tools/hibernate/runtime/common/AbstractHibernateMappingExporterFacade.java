package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingGlobalSettings;

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
	
	protected Class<?> getHibernateMappingGlobalSettingsClass() {
		return Util.getClass(
				getHibernateMappingGlobalSettingsClassName(), 
				getFacadeFactoryClassLoader());
	}
	
	protected String getHibernateMappingGlobalSettingsClassName() {
		return "org.hibernate.tool.hbm2x.HibernateMappingGlobalSettings";
	}

}
