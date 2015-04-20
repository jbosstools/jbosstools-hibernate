package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import org.hibernate.Criteria;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.reveng.DefaultDatabaseCollector;
import org.hibernate.mapping.Column;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.jboss.tools.hibernate.proxy.ClassMetadataProxy;
import org.jboss.tools.hibernate.proxy.CollectionMetadataProxy;
import org.jboss.tools.hibernate.proxy.ColumnProxy;
import org.jboss.tools.hibernate.proxy.ConfigurationProxy;
import org.jboss.tools.hibernate.proxy.CriteriaProxy;
import org.jboss.tools.hibernate.proxy.DatabaseCollectorProxy;
import org.jboss.tools.hibernate.proxy.EntityMetamodelProxy;
import org.jboss.tools.hibernate.runtime.common.AbstractFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.ICollectionMetadata;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.ICriteria;
import org.jboss.tools.hibernate.runtime.spi.IDatabaseCollector;
import org.jboss.tools.hibernate.runtime.spi.IEntityMetamodel;

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
	
}
