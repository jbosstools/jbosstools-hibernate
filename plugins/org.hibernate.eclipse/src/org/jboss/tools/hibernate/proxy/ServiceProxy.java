package org.jboss.tools.hibernate.proxy;

import java.io.File;
import java.util.Map;

import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.console.HibernateConsoleRuntimeException;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.hibernate.util.xpl.ReflectHelper;
import org.hibernate.util.xpl.StringHelper;
import org.jboss.tools.hibernate.spi.IArtifactCollector;
import org.jboss.tools.hibernate.spi.IConfiguration;
import org.jboss.tools.hibernate.spi.IExporter;
import org.jboss.tools.hibernate.spi.IHQLQueryPlan;
import org.jboss.tools.hibernate.spi.INamingStrategy;
import org.jboss.tools.hibernate.spi.IOverrideRepository;
import org.jboss.tools.hibernate.spi.ISchemaExport;
import org.jboss.tools.hibernate.spi.IService;
import org.jboss.tools.hibernate.spi.ISessionFactory;
import org.jboss.tools.hibernate.spi.ITypeFactory;
import org.xml.sax.EntityResolver;

public class ServiceProxy implements IService {

	@Override
	public IConfiguration newAnnotationConfiguration() {
		Configuration configuration = new AnnotationConfiguration();
		return new ConfigurationProxy(configuration);
	}

	@Override
	public IConfiguration newJpaConfiguration(
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
	public IConfiguration newDefaultConfiguration() {
		return new ConfigurationProxy(new Configuration());
	}

	@Override
	public void setExporterConfiguration(
			IExporter exporter,
			IConfiguration hcfg) {
		exporter.setConfiguration(hcfg);
	}

	@Override
	public HibernateMappingExporter newHibernateMappingExporter(
			IConfiguration hcfg, File file) {
		HibernateMappingExporter result = null;
		if (hcfg instanceof ConfigurationProxy) {
			result = new HibernateMappingExporter(((ConfigurationProxy)hcfg).getConfiguration()	, file);
		}
		return result;
	}

	@Override
	public ISchemaExport newSchemaExport(IConfiguration hcfg) {
		ISchemaExport result = null;
		if (hcfg instanceof ConfigurationProxy) {
			SchemaExport schemaExport = 
					new SchemaExport(((ConfigurationProxy)hcfg).getConfiguration());
			result = new SchemaExportProxy(schemaExport);
		}
		return result;
	}

	@Override
	public HQLCodeAssist newHQLCodeAssist(IConfiguration hcfg) {
		HQLCodeAssist result = null;
		if (hcfg instanceof ConfigurationProxy) {
			result = new HQLCodeAssist(((ConfigurationProxy)hcfg).getConfiguration());
		}
		return result;
	}

	@Override
	public IConfiguration newJDBCMetaDataConfiguration() {
		Configuration configuration = new JDBCMetaDataConfiguration();
		return new ConfigurationProxy(configuration);
	}

	@Override
	public IExporter createExporter(String exporterClassName) {
		return new ExporterProxy(exporterClassName);
	}

	@Override
	public IArtifactCollector newArtifactCollector() {
		return new ArtifactCollectorProxy();
	}

	@Override
	public IHQLQueryPlan newHQLQueryPlan(
			String query, 
			boolean shallow,
			ISessionFactory sessionFactory) {
		return new HQLQueryPlanProxy(query, shallow, sessionFactory);
	}

	@Override
	public ITypeFactory newTypeFactory() {
		return new TypeFactoryProxy();
	}

	@Override
	public INamingStrategy newNamingStrategy(String strategyClassName) {
		try {
			NamingStrategy ns = (NamingStrategy) ReflectHelper.classForName(
					strategyClassName).newInstance();
			return new NamingStrategyProxy(ns);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			return null;
		}
	}

	@Override
	public IOverrideRepository newOverrideRepository() {
		return new OverrideRepositoryProxy(new OverrideRepository());
	}


}
