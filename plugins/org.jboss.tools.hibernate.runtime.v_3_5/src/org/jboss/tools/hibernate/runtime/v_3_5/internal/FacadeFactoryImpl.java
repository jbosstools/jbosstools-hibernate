package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Settings;
import org.hibernate.cfg.reveng.DefaultDatabaseCollector;
import org.hibernate.cfg.reveng.JDBCReader;
import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.hibernate.engine.query.HQLQueryPlan;
import org.hibernate.hql.QueryTranslator;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Property;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.jboss.tools.hibernate.proxy.ClassMetadataProxy;
import org.jboss.tools.hibernate.proxy.CollectionMetadataProxy;
import org.jboss.tools.hibernate.proxy.ColumnProxy;
import org.jboss.tools.hibernate.proxy.ConfigurationProxy;
import org.jboss.tools.hibernate.proxy.CriteriaProxy;
import org.jboss.tools.hibernate.proxy.DatabaseCollectorProxy;
import org.jboss.tools.hibernate.proxy.EntityMetamodelProxy;
import org.jboss.tools.hibernate.proxy.EnvironmentProxy;
import org.jboss.tools.hibernate.proxy.ForeignKeyProxy;
import org.jboss.tools.hibernate.proxy.HQLCodeAssistProxy;
import org.jboss.tools.hibernate.proxy.HQLCompletionProposalProxy;
import org.jboss.tools.hibernate.proxy.HQLQueryPlanProxy;
import org.jboss.tools.hibernate.proxy.HibernateMappingExporterExtension;
import org.jboss.tools.hibernate.proxy.HibernateMappingExporterProxy;
import org.jboss.tools.hibernate.proxy.JDBCReaderProxy;
import org.jboss.tools.hibernate.proxy.JoinProxy;
import org.jboss.tools.hibernate.proxy.MetaDataDialectProxy;
import org.jboss.tools.hibernate.proxy.POJOClassProxy;
import org.jboss.tools.hibernate.proxy.PersistentClassProxy;
import org.jboss.tools.hibernate.proxy.PrimaryKeyProxy;
import org.jboss.tools.hibernate.proxy.PropertyProxy;
import org.jboss.tools.hibernate.proxy.QueryProxy;
import org.jboss.tools.hibernate.proxy.QueryTranslatorProxy;
import org.jboss.tools.hibernate.proxy.SessionFactoryProxy;
import org.jboss.tools.hibernate.proxy.SessionProxy;
import org.jboss.tools.hibernate.proxy.SettingsProxy;
import org.jboss.tools.hibernate.runtime.common.AbstractFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.ICollectionMetadata;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.ICriteria;
import org.jboss.tools.hibernate.runtime.spi.IDatabaseCollector;
import org.jboss.tools.hibernate.runtime.spi.IEntityMetamodel;
import org.jboss.tools.hibernate.runtime.spi.IEnvironment;
import org.jboss.tools.hibernate.runtime.spi.IForeignKey;
import org.jboss.tools.hibernate.runtime.spi.IHQLCodeAssist;
import org.jboss.tools.hibernate.runtime.spi.IHQLCompletionProposal;
import org.jboss.tools.hibernate.runtime.spi.IHQLQueryPlan;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.IJDBCReader;
import org.jboss.tools.hibernate.runtime.spi.IJoin;
import org.jboss.tools.hibernate.runtime.spi.IMetaDataDialect;
import org.jboss.tools.hibernate.runtime.spi.IPOJOClass;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IPrimaryKey;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.IQuery;
import org.jboss.tools.hibernate.runtime.spi.IQueryTranslator;
import org.jboss.tools.hibernate.runtime.spi.ISession;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.runtime.spi.ISettings;

public class FacadeFactoryImpl extends AbstractFacadeFactory {
	
	public ClassLoader getClassLoader() {
		return FacadeFactoryImpl.class.getClassLoader();
	}

	@Override
	public IClassMetadata createClassMetadata(Object target) {
		return new ClassMetadataProxy(this, (ClassMetadata)target);
	}

	@Override
	public ICollectionMetadata createCollectionMetadata(Object target) {
		return new CollectionMetadataProxy(this, (CollectionMetadata)target);
	}

	@Override
	public IColumn createColumn(Object target) {
		return new ColumnProxy(this, (Column)target);
	}

	@Override
	public IConfiguration createConfiguration(Object target) {
		return new ConfigurationProxy(this, (Configuration)target);
	}

	@Override
	public ICriteria createCriteria(Object target) {
		return new CriteriaProxy(this, (Criteria)target);
	}

	@Override
	public IDatabaseCollector createDatabaseCollector(Object target) {
		return new DatabaseCollectorProxy(this, (DefaultDatabaseCollector)target);
	}

	@Override
	public IEntityMetamodel createEntityMetamodel(Object target) {
		return new EntityMetamodelProxy(this, (EntityMetamodel)target);
	}

	@Override
	public IEnvironment createEnvironment(Object target) {
		return new EnvironmentProxy(this);
	}

	@Override
	public IForeignKey createForeignKey(Object target) {
		return new ForeignKeyProxy(this, (ForeignKey)target);
	}

	@Override
	public IHibernateMappingExporter createHibernateMappingExporter(Object target) {
		return new HibernateMappingExporterProxy(this, (HibernateMappingExporterExtension)target);
	}

	@Override
	public IHQLCodeAssist createHQLCodeAssist(Object target) {
		return new HQLCodeAssistProxy(this, (HQLCodeAssist)target);
	}

	@Override
	public IHQLCompletionProposal createHQLCompletionProposal(Object target) {
		return new HQLCompletionProposalProxy(this, (HQLCompletionProposal)target);
	}

	@Override
	public IHQLQueryPlan createHQLQueryPlan(Object target) {
		return new HQLQueryPlanProxy(this, (HQLQueryPlan)target);
	}

	@Override
	public IJDBCReader createJDBCReader(Object target) {
		return new JDBCReaderProxy(this, (JDBCReader)target);
	}

	@Override
	public IJoin createJoin(Object target) {
		return new JoinProxy(this, (Join)target);
	}

	@Override
	public IMetaDataDialect createMetaDataDialect(Object target) {
		return new MetaDataDialectProxy(this, (MetaDataDialect)target);
	}

	@Override
	public IPersistentClass createPersistentClass(Object target) {
		return new PersistentClassProxy(this, (PersistentClass)target);
	}

	@Override
	public IPOJOClass createPOJOClass(Object target) {
		return new POJOClassProxy(this, (POJOClass)target);
	}

	@Override
	public IPrimaryKey createPrimaryKey(Object target) {
		return new PrimaryKeyProxy(this, (PrimaryKey)target);
	}

	@Override
	public IProperty createProperty(Object target) {
		return new PropertyProxy(this, (Property)target);
	}

	@Override
	public IQuery createQuery(Object target) {
		return new QueryProxy(this, (Query)target);
	}

	@Override
	public IQueryTranslator createQueryTranslator(Object target) {
		return new QueryTranslatorProxy(this, (QueryTranslator)target);
	}

	@Override
	public ISessionFactory createSessionFactory(Object target) {
		return new SessionFactoryProxy(this, (SessionFactory)target);
	}

	@Override
	public ISession createSession(Object target) {
		return new SessionProxy(this, (Session)target);
	}

	@Override
	public ISettings createSettings(Object target) {
		return new SettingsProxy(this, (Settings)target);
	}
	
}
