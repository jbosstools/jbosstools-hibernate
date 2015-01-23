package org.jboss.tools.hibernate.proxy;

import java.io.File;
import java.io.StringWriter;
import java.util.Properties;

import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.GenericExporter;
import org.hibernate.tool.hbm2x.Hbm2DDLExporter;
import org.hibernate.tool.hbm2x.HibernateConfigurationExporter;
import org.hibernate.tool.hbm2x.QueryExporter;
import org.jboss.tools.hibernate.runtime.spi.HibernateException;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IGenericExporter;
import org.jboss.tools.hibernate.runtime.spi.IHbm2DDLExporter;
import org.jboss.tools.hibernate.runtime.spi.IQueryExporter;

public class ExporterProxy implements IExporter {
	
	private Exporter target;
	
	public ExporterProxy(String exporterClassName) {
		target = createTarget(exporterClassName);
	}

	@Override
	public void setConfiguration(IConfiguration configuration) {
		if (configuration instanceof ConfigurationProxy) {
			target.setConfiguration(((ConfigurationProxy)configuration).getConfiguration());
		}
	}
	
	private Exporter createTarget(String exporterClassName) {
		Exporter result = null;
		try {
			result = (Exporter)this.getClass().getClassLoader().loadClass(exporterClassName).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new HibernateException(e);
		}
		return result;
	}

	@Override
	public void setProperties(Properties properties) {
		target.setProperties(properties);
	}

	@Override
	public void setArtifactCollector(IArtifactCollector collector) {
		if (collector instanceof ArtifactCollectorProxy) { 
			target.setArtifactCollector((((ArtifactCollectorProxy)collector).getTarget()));
		}
	}

	@Override
	public void setOutputDirectory(File file) {
		target.setOutputDirectory(file);
	}

	@Override
	public void setTemplatePath(String[] strings) {
		target.setTemplatePath(strings);
	}

	@Override
	public void start() throws HibernateException {
		try {
			target.start();
		} catch (org.hibernate.HibernateException e) {
			throw new HibernateException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public Properties getProperties() {
		return target.getProperties();
	}

	@Override
	public IGenericExporter getGenericExporter() {
		IGenericExporter result = null;
		if (target instanceof GenericExporter) {
			result = new GenericExporterProxy((GenericExporter)target);
		}
		return result;
	}

	@Override
	public IHbm2DDLExporter getHbm2DDLExporter() {
		IHbm2DDLExporter result = null;
		if (target instanceof Hbm2DDLExporter) {
			result = new Hbm2DDLExporterProxy((Hbm2DDLExporter)target);
		}
		return result;
	}

	@Override
	public IQueryExporter getQueryExporter() {
		IQueryExporter result = null;
		if (target instanceof QueryExporter) {
			result = new QueryExporterProxy((QueryExporter)target);
		}
		return result;
	}

	@Override
	public void setCustomProperties(Properties props) {
		assert target instanceof HibernateConfigurationExporter;
		((HibernateConfigurationExporter)target).setCustomProperties(props);
	}

	@Override
	public void setOutput(StringWriter stringWriter) {
		assert target instanceof HibernateConfigurationExporter;
		((HibernateConfigurationExporter)target).setOutput(stringWriter);
	}

}
