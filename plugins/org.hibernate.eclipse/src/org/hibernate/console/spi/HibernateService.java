package org.hibernate.console.spi;

import java.io.File;
import java.util.Map;

import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.ide.completion.HQLCodeAssist;

public interface HibernateService {

	HibernateConfiguration newAnnotationConfiguration();

	HibernateConfiguration newJpaConfiguration(String entityResolver,
			String persistenceUnit, Map<Object, Object> overrides);
	
	HibernateConfiguration newDefaultConfiguration();
	
	void setExporterConfiguration(Exporter exporter, HibernateConfiguration hcfg);
	
	HibernateMappingExporter newHibernateMappingExporter(
			HibernateConfiguration hcfg, File file);
	
	SchemaExport newSchemaExport(HibernateConfiguration hcfg);
	
	HQLCodeAssist newHQLCodeAssist(HibernateConfiguration hcfg);

	HibernateConfiguration newJDBCMetaDataConfiguration();

}
