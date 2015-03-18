package org.jboss.tools.hibernate.proxy;

import java.io.StringWriter;
import java.util.Properties;

import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.Hbm2DDLExporter;
import org.hibernate.tool.hbm2x.HibernateConfigurationExporter;
import org.hibernate.tool.hbm2x.QueryExporter;
import org.jboss.tools.hibernate.runtime.common.AbstractExporterFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
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
