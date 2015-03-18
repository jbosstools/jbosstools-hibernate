package org.jboss.tools.hibernate.runtime.common;

import java.io.File;
import java.util.Properties;

import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

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

}
