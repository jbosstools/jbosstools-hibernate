package org.jboss.tools.hibernate.runtime.v_4_3.internal;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Settings;
import org.hibernate.cfg.reveng.JDBCReader;
import org.hibernate.cfg.reveng.dialect.MetaDataDialect;
import org.hibernate.engine.query.spi.HQLQueryPlan;
import org.hibernate.hql.spi.QueryTranslator;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.hibernate.type.Type;
import org.hibernate.type.TypeFactory;
import org.jboss.tools.hibernate.proxy.HQLCodeAssistProxy;
import org.jboss.tools.hibernate.proxy.HQLCompletionProposalProxy;
import org.jboss.tools.hibernate.proxy.HQLQueryPlanProxy;
import org.jboss.tools.hibernate.proxy.HibernateMappingExporterExtension;
import org.jboss.tools.hibernate.proxy.HibernateMappingExporterProxy;
import org.jboss.tools.hibernate.proxy.JDBCReaderProxy;
import org.jboss.tools.hibernate.proxy.JoinFacadeImpl;
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
import org.jboss.tools.hibernate.proxy.SpecialRootClassProxy;
import org.jboss.tools.hibernate.proxy.TableProxy;
import org.jboss.tools.hibernate.proxy.TypeFactoryProxy;
import org.jboss.tools.hibernate.proxy.TypeProxy;
import org.jboss.tools.hibernate.runtime.common.AbstractFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IEnvironment;
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
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;

public class FacadeFactoryImpl extends AbstractFacadeFactory {
	
	public ClassLoader getClassLoader() {
		return FacadeFactoryImpl.class.getClassLoader();
	}
	
	@Override
	public IConfiguration createConfiguration(Object target) {
		return new ConfigurationFacadeImpl(this, (Configuration)target);
	}
	
	@Override
	public IEnvironment createEnvironment() {
		return new EnvironmentFacadeImpl(this);
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
		return new JoinFacadeImpl(this, (Join)target);
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
	
	@Override
	public IPersistentClass createSpecialRootClass(IProperty property) {
		return new SpecialRootClassProxy(this, property);
	}
	
	@Override
	public ITable createTable(Object target) {
		return new TableProxy(this, (Table)target);
	}
	
	@Override
	public ITypeFactory createTypeFactory() {
		return new TypeFactoryProxy(this, (TypeFactory)null);
	}
	
	@Override
	public IType createType(Object target) {
		return new TypeProxy(this, (Type)target);
	}
	
}
