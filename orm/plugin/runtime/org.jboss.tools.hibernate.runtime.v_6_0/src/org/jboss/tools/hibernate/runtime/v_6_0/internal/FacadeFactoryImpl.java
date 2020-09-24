package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import org.jboss.tools.hibernate.runtime.common.AbstractFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.ICollectionMetadata;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.ICriteria;
import org.jboss.tools.hibernate.runtime.spi.IDatabaseCollector;
import org.jboss.tools.hibernate.runtime.spi.IEntityMetamodel;
import org.jboss.tools.hibernate.runtime.spi.IEnvironment;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IForeignKey;
import org.jboss.tools.hibernate.runtime.spi.IGenericExporter;
import org.jboss.tools.hibernate.runtime.spi.IHQLCodeAssist;
import org.jboss.tools.hibernate.runtime.spi.IHQLCompletionProposal;
import org.jboss.tools.hibernate.runtime.spi.IHQLQueryPlan;
import org.jboss.tools.hibernate.runtime.spi.IHbm2DDLExporter;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.IJDBCReader;
import org.jboss.tools.hibernate.runtime.spi.IJoin;
import org.jboss.tools.hibernate.runtime.spi.IPOJOClass;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IPrimaryKey;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.IQuery;
import org.jboss.tools.hibernate.runtime.spi.IQueryExporter;
import org.jboss.tools.hibernate.runtime.spi.IQueryTranslator;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;
import org.jboss.tools.hibernate.runtime.spi.ISession;
import org.jboss.tools.hibernate.runtime.spi.ITableFilter;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;
import org.jboss.tools.hibernate.runtime.spi.IValue;

public class FacadeFactoryImpl  extends AbstractFacadeFactory {

	@Override
	public ClassLoader getClassLoader() {
		return FacadeFactoryImpl.class.getClassLoader();
	}

	@Override
	public ISchemaExport createSchemaExport(Object target) {
		return new SchemaExportFacadeImpl(this, target);
	}
	
	@Override
	public IGenericExporter createGenericExporter(Object target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IHbm2DDLExporter createHbm2DDLExporter(Object target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IQueryExporter createQueryExporter(Object target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITableFilter createTableFilter(Object target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IExporter createExporter(Object target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IClassMetadata createClassMetadata(Object target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICollectionMetadata createCollectionMetadata(Object target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IColumn createColumn(Object target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IConfiguration createConfiguration(Object target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICriteria createCriteria(Object target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDatabaseCollector createDatabaseCollector(Object target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IEntityMetamodel createEntityMetamodel(Object target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IEnvironment createEnvironment() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IForeignKey createForeignKey(Object target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IHibernateMappingExporter createHibernateMappingExporter(Object target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IHQLCodeAssist createHQLCodeAssist(Object target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IHQLCompletionProposal createHQLCompletionProposal(Object target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IHQLQueryPlan createHQLQueryPlan(Object target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IJDBCReader createJDBCReader(Object target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IJoin createJoin(Object target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPOJOClass createPOJOClass(Object target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPrimaryKey createPrimaryKey(Object target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IProperty createProperty(Object target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IQuery createQuery(Object target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IQueryTranslator createQueryTranslator(Object target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISession createSession(Object target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPersistentClass createSpecialRootClass(IProperty property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITypeFactory createTypeFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IType createType(Object target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IValue createValue(Object target) {
		// TODO Auto-generated method stub
		return null;
	}

}
