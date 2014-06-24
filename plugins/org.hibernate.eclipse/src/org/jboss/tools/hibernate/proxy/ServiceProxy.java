package org.jboss.tools.hibernate.proxy;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.JDBCReaderFactory;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.cfg.Settings;
import org.hibernate.cfg.reveng.DefaultDatabaseCollector;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.JDBCReader;
import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableFilter;
import org.hibernate.console.HibernateConsoleRuntimeException;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2x.Cfg2HbmTool;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.hibernate.util.xpl.ReflectHelper;
import org.hibernate.util.xpl.StringHelper;
import org.jboss.tools.hibernate.spi.IArtifactCollector;
import org.jboss.tools.hibernate.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.spi.IColumn;
import org.jboss.tools.hibernate.spi.IConfiguration;
import org.jboss.tools.hibernate.spi.IDatabaseCollector;
import org.jboss.tools.hibernate.spi.IExporter;
import org.jboss.tools.hibernate.spi.IHQLQueryPlan;
import org.jboss.tools.hibernate.spi.IJDBCReader;
import org.jboss.tools.hibernate.spi.IMetaDataDialect;
import org.jboss.tools.hibernate.spi.INamingStrategy;
import org.jboss.tools.hibernate.spi.IOverrideRepository;
import org.jboss.tools.hibernate.spi.IProgressListener;
import org.jboss.tools.hibernate.spi.IProperty;
import org.jboss.tools.hibernate.spi.IReverseEngineeringSettings;
import org.jboss.tools.hibernate.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.spi.ISchemaExport;
import org.jboss.tools.hibernate.spi.IService;
import org.jboss.tools.hibernate.spi.ISessionFactory;
import org.jboss.tools.hibernate.spi.ITable;
import org.jboss.tools.hibernate.spi.ITableFilter;
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

	@Override
	public ITableFilter newTableFilter() {
		return new TableFilterProxy(new TableFilter());
	}

	@Override
	public IReverseEngineeringSettings newReverseEngineeringSettings(
			IReverseEngineeringStrategy res) {
		assert res instanceof ReverseEngineeringStrategyProxy;
		return new ReverseEngineeringSettingsProxy(
				new ReverseEngineeringSettings(
						((ReverseEngineeringStrategyProxy)res).getTarget()));
	}

	@Override
	public IReverseEngineeringStrategy newDefaultReverseEngineeringStrategy() {
		return new ReverseEngineeringStrategyProxy(new DefaultReverseEngineeringStrategy());
	}

	@Override
	public IJDBCReader newJDBCReader(Properties properties, Settings settings,
			IReverseEngineeringStrategy strategy) {
		assert strategy instanceof ReverseEngineeringStrategyProxy;
		JDBCReader target = 
				JDBCReaderFactory.newJDBCReader(
						properties, 
						settings, 
						((ReverseEngineeringStrategyProxy)strategy).getTarget());
		return new JDBCReaderProxy(target);
	}

	@Override
	public IReverseEngineeringStrategy newReverseEngineeringStrategy(
			String strategyName, IReverseEngineeringStrategy delegate) {
		assert delegate instanceof ReverseEngineeringStrategyProxy;
		ReverseEngineeringStrategy target = 
				newReverseEngineeringStrategy(strategyName, ((ReverseEngineeringStrategyProxy)delegate).getTarget());
		return new ReverseEngineeringStrategyProxy(target);
	}
	

	@SuppressWarnings("unchecked")
	private ReverseEngineeringStrategy newReverseEngineeringStrategy(final String className, ReverseEngineeringStrategy delegate) {
        try {
            Class<ReverseEngineeringStrategy> clazz = ReflectHelper.classForName(className);
			Constructor<ReverseEngineeringStrategy> constructor = clazz.getConstructor(new Class[] { ReverseEngineeringStrategy.class });
            return constructor.newInstance(new Object[] { delegate });
        }
        catch (NoSuchMethodException e) {
			try {
				Class<?> clazz = ReflectHelper.classForName(className);
				ReverseEngineeringStrategy rev = (ReverseEngineeringStrategy) clazz.newInstance();
				return rev;
			}
			catch (Exception eq) {
				throw new HibernateConsoleRuntimeException(eq);
			}
		}
        catch (Exception e) {
			throw new HibernateConsoleRuntimeException(e);
		}
    }

	@Override
	public String getReverseEngineeringStrategyClassName() {
		return ReverseEngineeringStrategy.class.getName();
	}

	@Override
	public IDatabaseCollector newDatabaseCollector(IMetaDataDialect metaDataDialect) {
		assert metaDataDialect instanceof MetaDataDialectProxy;
		return new DatabaseCollectorProxy(
				new DefaultDatabaseCollector(
						((MetaDataDialectProxy)metaDataDialect).getTarget()));
	}

	@Override
	public IProgressListener newProgressListener(IProgressMonitor monitor) {
		return new ProgressListenerProxy(monitor);
	}

	@Override
	public ICfg2HbmTool newCfg2HbmTool() {
		return new Cfg2HbmToolProxy(new Cfg2HbmTool());
	}

	@Override
	public IProperty newProperty() {
		return new PropertyProxy(new Property());
	}

	@Override
	public ITable newTable(String name) {
		return new TableProxy(new Table(name));
	}

	@Override
	public IColumn newColumn(String string) {
		return new ColumnProxy(new Column(string));
	}

}
