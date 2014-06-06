package org.jboss.tools.hibernate.proxy;

import java.io.File;
import java.util.Properties;

import org.hibernate.console.HibernateConsoleRuntimeException;
import org.hibernate.tool.hbm2x.ArtifactCollector;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.GenericExporter;
import org.hibernate.tool.hbm2x.Hbm2DDLExporter;
import org.hibernate.tool.hbm2x.QueryExporter;
import org.hibernate.util.xpl.ReflectHelper;
import org.jboss.tools.hibernate.spi.IConfiguration;
import org.jboss.tools.hibernate.spi.IExporter;
import org.jboss.tools.hibernate.spi.IGenericExporter;
import org.jboss.tools.hibernate.spi.IQueryExporter;

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
			result = (Exporter) ReflectHelper.classForName(exporterClassName).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new HibernateConsoleRuntimeException(e);
		}
		return result;
	}

	@Override
	public void setProperties(Properties properties) {
		target.setProperties(properties);
	}

	@Override
	public void setArtifactCollector(ArtifactCollector collector) {
		target.setArtifactCollector(collector);
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
	public void start() {
		target.start();
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
	public Hbm2DDLExporter getHbm2DDLExporter() {
		Hbm2DDLExporter result = null;
		if (target instanceof Hbm2DDLExporter) {
			result = (Hbm2DDLExporter)target;
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

}
