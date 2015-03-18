package org.jboss.tools.hibernate.proxy;

import java.io.File;
import java.io.StringWriter;
import java.util.Properties;

import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.GenericExporter;
import org.hibernate.tool.hbm2x.Hbm2DDLExporter;
import org.hibernate.tool.hbm2x.HibernateConfigurationExporter;
import org.hibernate.tool.hbm2x.QueryExporter;
import org.jboss.tools.hibernate.runtime.common.AbstractExporterFacade;
import org.jboss.tools.hibernate.runtime.spi.HibernateException;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IGenericExporter;
import org.jboss.tools.hibernate.runtime.spi.IHbm2DDLExporter;
import org.jboss.tools.hibernate.runtime.spi.IQueryExporter;

public class ExporterProxy extends AbstractExporterFacade {
	
	public ExporterProxy(IFacadeFactory facadeFactory, Exporter target) {
		super(facadeFactory, target);
	}
	
	public Exporter getTarget() {
		return (Exporter)super.getTarget();
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
			result = getFacadeFactory().createGenericExporter(getTarget());
		}
		return result;
	}

	@Override
	public IHbm2DDLExporter getHbm2DDLExporter() {
		IHbm2DDLExporter result = null;
		if (getTarget() instanceof Hbm2DDLExporter) {
			result = getFacadeFactory().createHbm2DDLExporter(getTarget());
		}
		return result;
	}

	@Override
	public IQueryExporter getQueryExporter() {
		IQueryExporter result = null;
		if (getTarget() instanceof QueryExporter) {
			result = getFacadeFactory().createQueryExporter(getTarget());
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
