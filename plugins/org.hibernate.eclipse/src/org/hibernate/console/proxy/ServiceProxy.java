package org.hibernate.console.proxy;

import java.io.File;
import java.util.Map;

import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.console.HibernateConsoleRuntimeException;
import org.hibernate.console.spi.HibernateConfiguration;
import org.hibernate.console.spi.HibernateService;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.hibernate.util.xpl.ReflectHelper;
import org.hibernate.util.xpl.StringHelper;
import org.xml.sax.EntityResolver;

public class ServiceProxy implements HibernateService {

	@Override
	public HibernateConfiguration newAnnotationConfiguration() {
		Configuration configuration = new AnnotationConfiguration();
		return new ConfigurationProxy(configuration);
	}

	@Override
	public HibernateConfiguration newJpaConfiguration(
			String entityResolver, 
			String persistenceUnit, 
			Map<Object, Object> overrides) {
		Ejb3Configuration ejb3Configuration = new Ejb3Configuration();
		if (StringHelper.isNotEmpty(entityResolver)) {
			try {
				Class<?> resolver = ReflectHelper.classForName(entityResolver, this.getClass());
				Object object = resolver.newInstance();
				ejb3Configuration.setEntityResolver((EntityResolver)object);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				throw new HibernateConsoleRuntimeException(e);
			}
		}
		ejb3Configuration.configure(persistenceUnit, overrides);
		Configuration configuration = ejb3Configuration.getHibernateConfiguration();
		return new ConfigurationProxy(configuration);
	}

	@Override
	public HibernateConfiguration newDefaultConfiguration() {
		return new ConfigurationProxy(new Configuration());
	}

	@Override
	public void setExporterConfiguration(
			Exporter exporter,
			HibernateConfiguration hcfg) {
		if (hcfg instanceof ConfigurationProxy) {
			exporter.setConfiguration(((ConfigurationProxy)hcfg).getConfiguration());
		}
	}

	@Override
	public HibernateMappingExporter newHibernateMappingExporter(
			HibernateConfiguration hcfg, File file) {
		HibernateMappingExporter result = null;
		if (hcfg instanceof ConfigurationProxy) {
			result = new HibernateMappingExporter(((ConfigurationProxy)hcfg).getConfiguration()	, file);
		}
		return result;
	}

	@Override
	public SchemaExport newSchemaExport(HibernateConfiguration hcfg) {
		SchemaExport result = null;
		if (hcfg instanceof ConfigurationProxy) {
			result = new SchemaExport(((ConfigurationProxy)hcfg).getConfiguration());
		}
		return result;
	}

	@Override
	public HQLCodeAssist newHQLCodeAssist(HibernateConfiguration hcfg) {
		HQLCodeAssist result = null;
		if (hcfg instanceof ConfigurationProxy) {
			result = new HQLCodeAssist(((ConfigurationProxy)hcfg).getConfiguration());
		}
		return result;
	}

	@Override
	public HibernateConfiguration newJDBCMetaDataConfiguration() {
		Configuration configuration = new JDBCMetaDataConfiguration();
		return new ConfigurationProxy(configuration);
	}


}
