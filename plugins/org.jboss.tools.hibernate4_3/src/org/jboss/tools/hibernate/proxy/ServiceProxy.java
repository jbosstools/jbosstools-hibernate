package org.jboss.tools.hibernate.proxy;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.hibernate.annotations.common.util.StandardClassLoaderDelegateImpl;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.console.HibernateConsoleRuntimeException;
import org.hibernate.console.spi.HibernateConfiguration;
import org.hibernate.console.spi.HibernateService;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.ide.completion.HQLCodeAssist;

public class ServiceProxy implements HibernateService {

	@Override
	public HibernateConfiguration newAnnotationConfiguration() {
		Configuration configuration = new Configuration();
		return new ConfigurationProxy(configuration);
	}
	
	@Override
	public HibernateConfiguration newJpaConfiguration(
			String entityResolver, 
			String persistenceUnit, 
			Map<Object, Object> overrides) {
		HibernateConfiguration result = null;
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
