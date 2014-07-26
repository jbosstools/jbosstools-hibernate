package org.jboss.tools.hibernate.proxy;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.jboss.tools.hibernate.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.spi.IHibernateMappingGlobalSettings;
import org.jboss.tools.hibernate.spi.IPOJOClass;

public class HibernateMappingExporterProxy implements IHibernateMappingExporter {

	private HibernateMappingExporter target = null;

	public HibernateMappingExporterProxy(
			HibernateMappingExporter hibernateMappingExporter) {
		target = hibernateMappingExporter;
	}

	@Override
	public void setGlobalSettings(IHibernateMappingGlobalSettings hmgs) {
		assert hmgs instanceof HibernateMappingGlobalSettingsProxy;
		target.setGlobalSettings(((HibernateMappingGlobalSettingsProxy) hmgs)
				.getTarget());
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
		try {
			Method exportPOJO = target.getClass().getMethod(
					"exportPOJO", new Class[] { Map.class, POJOClass.class });
			exportPOJO.setAccessible(true);
			exportPOJO.invoke(target, new Object[] { map, ((POJOClassProxy)pojoClass).getTarget() });
		} catch (NoSuchMethodException | SecurityException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

}
