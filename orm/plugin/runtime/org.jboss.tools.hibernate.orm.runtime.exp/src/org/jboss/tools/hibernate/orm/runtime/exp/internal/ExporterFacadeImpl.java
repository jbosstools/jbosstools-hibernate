package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import java.io.File;

import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.ConfigurationMetadataDescriptor;
import org.jboss.tools.hibernate.runtime.common.AbstractExporterFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;

public class ExporterFacadeImpl extends AbstractExporterFacade {

	public ExporterFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}

	@Override
	public void setConfiguration(IConfiguration configuration) {
		setCustomProperties(configuration.getProperties());
		((Exporter)getTarget()).getProperties().put(
				ExporterConstants.METADATA_DESCRIPTOR, 
				new ConfigurationMetadataDescriptor(configuration));
	}
	
	@Override
	public void setArtifactCollector(IArtifactCollector artifactCollector) {
		((Exporter)getTarget()).getProperties().put(
				ExporterConstants.ARTIFACT_COLLECTOR,
				((IFacade)artifactCollector).getTarget());
	}

	@Override
	public void setOutputDirectory(File file) {
		((Exporter)getTarget()).getProperties().put(ExporterConstants.DESTINATION_FOLDER, file);
	}

	@Override
	public void setTemplatePath(String[] templatePath) {
		((Exporter)getTarget()).getProperties().put(ExporterConstants.TEMPLATE_PATH, templatePath);
	}

	@Override
	protected String getHibernateConfigurationExporterClassName() {
		return "org.hibernate.tool.internal.export.cfg.CfgExporter";
	}
	
	@Override
	protected String getGenericExporterClassName() {
		return "org.hibernate.tool.internal.export.common.GenericExporter";
	}

	@Override
	protected String getHbm2DDLExporterClassName() {
		return "org.hibernate.tool.internal.export.ddl.DdlExporter";
	}

	@Override
	protected String getQueryExporterClassName() {
		return "org.hibernate.tool.internal.export.query.QueryExporter";
	}

}
