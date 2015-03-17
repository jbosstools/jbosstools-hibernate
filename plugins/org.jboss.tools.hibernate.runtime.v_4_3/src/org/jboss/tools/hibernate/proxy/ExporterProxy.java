package org.jboss.tools.hibernate.proxy;

import java.io.File;
import java.io.StringWriter;
import java.util.Properties;

import org.hibernate.tool.hbm2x.ArtifactCollector;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.GenericExporter;
import org.hibernate.tool.hbm2x.Hbm2DDLExporter;
import org.hibernate.tool.hbm2x.HibernateConfigurationExporter;
import org.hibernate.tool.hbm2x.QueryExporter;
import org.jboss.tools.hibernate.runtime.common.AbstractExporterFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.HibernateException;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IGenericExporter;
import org.jboss.tools.hibernate.runtime.spi.IHbm2DDLExporter;
import org.jboss.tools.hibernate.runtime.spi.IQueryExporter;

public class ExporterProxy extends AbstractExporterFacade {
	
	private Exporter target;
	
	public ExporterProxy(IFacadeFactory facadeFactory, Exporter target) {
		super(facadeFactory, target);
		this.target = target;		
	}
	
	public Exporter getTarget() {
		return target;
	}

	@Override
	public void setConfiguration(IConfiguration configuration) {
		if (configuration instanceof ConfigurationProxy) {
			getTarget().setConfiguration(((ConfigurationProxy)configuration).getConfiguration());
		}
	}
	
	@Override
	public void setProperties(Properties properties) {
		getTarget().setProperties(properties);
	}

	@Override
	public void setArtifactCollector(IArtifactCollector collector) {
		if (collector instanceof IFacade) {
			getTarget().setArtifactCollector((ArtifactCollector)((IFacade)collector).getTarget());
		}
	}

	@Override
	public void setOutputDirectory(File file) {
		getTarget().setOutputDirectory(file);
	}

	@Override
	public void setTemplatePath(String[] strings) {
		getTarget().setTemplatePath(strings);
	}

	@Override
	public void start() throws HibernateException {
		try {
			getTarget().start();
		} catch (org.hibernate.HibernateException e) {
			throw new HibernateException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public Properties getProperties() {
		return getTarget().getProperties();
	}

	@Override
	public IGenericExporter getGenericExporter() {
		IGenericExporter result = null;
		if (getTarget() instanceof GenericExporter) {
			result = new GenericExporterProxy((GenericExporter)getTarget());
		}
		return result;
	}

	@Override
	public IHbm2DDLExporter getHbm2DDLExporter() {
		IHbm2DDLExporter result = null;
		if (getTarget() instanceof Hbm2DDLExporter) {
			result = new Hbm2DDLExporterProxy((Hbm2DDLExporter)getTarget());
		}
		return result;
	}

	@Override
	public IQueryExporter getQueryExporter() {
		IQueryExporter result = null;
		if (getTarget() instanceof QueryExporter) {
			result = new QueryExporterProxy((QueryExporter)getTarget());
		}
		return result;
	}

	@Override
	public void setCustomProperties(Properties props) {
		assert getTarget() instanceof HibernateConfigurationExporter;
		((HibernateConfigurationExporter)getTarget()).setCustomProperties(props);
	}

	@Override
	public void setOutput(StringWriter stringWriter) {
		assert getTarget() instanceof HibernateConfigurationExporter;
		((HibernateConfigurationExporter)getTarget()).setOutput(stringWriter);
	}

}
