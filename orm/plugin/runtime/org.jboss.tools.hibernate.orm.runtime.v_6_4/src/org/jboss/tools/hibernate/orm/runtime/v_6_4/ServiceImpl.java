package org.jboss.tools.hibernate.orm.runtime.v_6_4;

import java.io.File;
import java.util.Map;

import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.jboss.tools.hibernate.orm.runtime.common.GenericFacadeFactory;
import org.jboss.tools.hibernate.orm.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;

public class ServiceImpl {

	public IConfiguration newDefaultConfiguration() {
		return (IConfiguration)GenericFacadeFactory.createFacade(
				IConfiguration.class, 
				WrapperFactory.createNativeConfigurationWrapper());
	}

	public IConfiguration newAnnotationConfiguration() {
		return newDefaultConfiguration();
	}

	public IConfiguration newJpaConfiguration(
			String entityResolver, 
			String persistenceUnit,
			Map<Object, Object> overrides) {
		return (IConfiguration)GenericFacadeFactory.createFacade(
				IConfiguration.class, 
				WrapperFactory.createJpaConfigurationWrapper(persistenceUnit, overrides));
	}

	public IHibernateMappingExporter newHibernateMappingExporter(
			IConfiguration configuration, File file) {
		return (IHibernateMappingExporter)GenericFacadeFactory.createFacade(
				IHibernateMappingExporter.class, 
				WrapperFactory.createHbmExporterWrapper(((IFacade)configuration).getTarget(), file));
	}

	public ISchemaExport newSchemaExport(IConfiguration configuration) {
		return (ISchemaExport)GenericFacadeFactory.createFacade(
				ISchemaExport.class, 
				WrapperFactory.createSchemaExport(((IFacade)configuration).getTarget()));
	}

}
