package org.hibernate.console.spi;

import java.io.File;
import java.util.Map;

import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.ide.completion.HQLCodeAssist;

public interface IService {

	IConfiguration newAnnotationConfiguration();

	IConfiguration newJpaConfiguration(String entityResolver,
			String persistenceUnit, Map<Object, Object> overrides);
	
	IConfiguration newDefaultConfiguration();
	
	void setExporterConfiguration(Exporter exporter, IConfiguration hcfg);
	
	HibernateMappingExporter newHibernateMappingExporter(
			IConfiguration hcfg, File file);
	
	SchemaExport newSchemaExport(IConfiguration hcfg);
	
	HQLCodeAssist newHQLCodeAssist(IConfiguration hcfg);

	IConfiguration newJDBCMetaDataConfiguration();

}
