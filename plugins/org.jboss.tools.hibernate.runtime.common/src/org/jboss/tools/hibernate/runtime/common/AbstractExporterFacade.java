package org.jboss.tools.hibernate.runtime.common;

import java.io.File;
import java.util.Properties;

import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IGenericExporter;
import org.jboss.tools.hibernate.runtime.spi.IHbm2DDLExporter;

public abstract class AbstractExporterFacade 
extends AbstractFacade 
implements IExporter {

	public AbstractExporterFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	public void setConfiguration(IConfiguration configuration) {
		if (configuration instanceof IFacade) {
			Util.invokeMethod(
					getTarget(), 
					"setConfiguration", 
					new Class[] { getConfigurationClass() }, 
					new Object[] { ((IFacade)configuration).getTarget() });
		}
	}
	
	protected Class<?> getConfigurationClass() {
		return Util.getClass(
				getConfigurationClassName(), 
				getFacadeFactoryClassLoader());
	}
	
	protected String getConfigurationClassName() {
		return "org.hibernate.cfg.Configuration";
	}
	
	@Override
	public void setProperties(Properties properties) {
		Util.invokeMethod(
				getTarget(), 
				"setProperties", 
				new Class[] { Properties.class }, 
				new Object[] { properties });
	}

	@Override
	public void setArtifactCollector(IArtifactCollector collector) {
		if (collector instanceof IFacade) {
			Util.invokeMethod(
					getTarget(), 
					"setArtifactCollector", 
					new Class[] { getArtifactCollectorClass() }, 
					new Object[] { ((IFacade)collector).getTarget() });
		}
	}
	
	protected Class<?> getArtifactCollectorClass() {
		return Util.getClass(
				getArtifactCollectorClassName(), 
				getFacadeFactoryClassLoader());
	}
	
	protected String getArtifactCollectorClassName() {
		return "org.hibernate.tool.hbm2x.ArtifactCollector";
	}

	@Override
	public void setOutputDirectory(File file) {
		Util.invokeMethod(
				getTarget(), 
				"setOutputDirectory", 
				new Class[] { File.class }, 
				new Object[] { file });
	}

	@Override
	public void setTemplatePath(String[] strings) {
		Util.invokeMethod(
				getTarget(), 
				"setTemplatePath", 
				new Class[] { String[].class }, 
				new Object[] { strings });
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
	public Properties getProperties() {
		return (Properties)Util.invokeMethod(
				getTarget(), 
				"getProperties", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public IGenericExporter getGenericExporter() {
		IGenericExporter result = null;
		if (getGenericExporterClass().isAssignableFrom(getTarget().getClass())) {
			result = getFacadeFactory().createGenericExporter(getTarget());
		}
		return result;
	}
	
	protected Class<?> getGenericExporterClass() {
		return Util.getClass(
				getGenericExporterClassName(), 
				getFacadeFactoryClassLoader());
	}

	protected String getGenericExporterClassName() {
		return "org.hibernate.tool.hbm2x.GenericExporter";
	}

	@Override
	public IHbm2DDLExporter getHbm2DDLExporter() {
		IHbm2DDLExporter result = null;
		if (getHbm2DDLExporterClass().isAssignableFrom(getTarget().getClass())) {
			result = getFacadeFactory().createHbm2DDLExporter(getTarget());
		}
		return result;
	}
	
	protected Class<?> getHbm2DDLExporterClass() {
		return Util.getClass(
				getHbm2DDLExporterClassName(), 
				getFacadeFactoryClassLoader());
	}

	protected String getHbm2DDLExporterClassName() {
		return "org.hibernate.tool.hbm2x.Hbm2DDLExporter";
	}

}
