package org.jboss.tools.hibernate.proxy;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.hibernate.annotations.common.util.StandardClassLoaderDelegateImpl;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.console.HibernateConsoleRuntimeException;
import org.hibernate.console.spi.IConfiguration;
import org.hibernate.console.spi.IExporter;
import org.hibernate.console.spi.IService;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.ide.completion.HQLCodeAssist;

public class ServiceProxy implements IService {

	@Override
	public IConfiguration newAnnotationConfiguration() {
		Configuration configuration = new Configuration();
		return new ConfigurationProxy(configuration);
	}
	
	@Override
	public IConfiguration newJpaConfiguration(
			String entityResolver, 
			String persistenceUnit, 
			Map<Object, Object> overrides) {
		IConfiguration result = null;
		try {
			HibernatePersistenceProvider hibernatePersistenceProvider = new HibernatePersistenceProvider();
			Method getEntityManagerFactoryBuilderOrNull = hibernatePersistenceProvider.getClass().getDeclaredMethod(
					"getEntityManagerFactoryBuilderOrNull", 
					new Class[] { String.class, Map.class });
			getEntityManagerFactoryBuilderOrNull.setAccessible(true);
			Object entityManagerFactoryBuilder = 
					getEntityManagerFactoryBuilderOrNull.invoke(
							hibernatePersistenceProvider, 
							new Object[] { persistenceUnit, overrides});	
			if (entityManagerFactoryBuilder == null) {
				throw new HibernateConsoleRuntimeException(
						"Persistence unit not found: '" + 
						persistenceUnit + 
						"'.");
			}		
			Method buildServiceRegistry = 
					entityManagerFactoryBuilder.getClass().getMethod(
							"buildServiceRegistry", new Class[0]);
			Object serviceRegistry = buildServiceRegistry.invoke(entityManagerFactoryBuilder, null);		
			Class serviceRegistryClass = StandardClassLoaderDelegateImpl.INSTANCE.classForName(
					"org.hibernate.service.ServiceRegistry");
			Method buildHibernateConfiguration = 
					entityManagerFactoryBuilder.getClass().getMethod(
							"buildHibernateConfiguration", 
							new Class[] { serviceRegistryClass });		
			Configuration configuration = (Configuration)buildHibernateConfiguration.invoke(entityManagerFactoryBuilder, new Object[] { serviceRegistry });		
			result = new ConfigurationProxy(configuration);
		} catch (SecurityException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException  e) {
			throw new HibernateConsoleRuntimeException(e);
		}
		return result;
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
	public SchemaExport newSchemaExport(IConfiguration hcfg) {
		SchemaExport result = null;
		if (hcfg instanceof ConfigurationProxy) {
			result = new SchemaExport(((ConfigurationProxy)hcfg).getConfiguration());
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


}
