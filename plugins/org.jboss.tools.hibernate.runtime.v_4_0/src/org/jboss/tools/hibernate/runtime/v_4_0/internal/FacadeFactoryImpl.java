package org.jboss.tools.hibernate.runtime.v_4_0.internal;

import org.hibernate.Criteria;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Column;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.jboss.tools.hibernate.proxy.ClassMetadataProxy;
import org.jboss.tools.hibernate.proxy.CollectionMetadataProxy;
import org.jboss.tools.hibernate.proxy.ColumnProxy;
import org.jboss.tools.hibernate.proxy.ConfigurationProxy;
import org.jboss.tools.hibernate.proxy.CriteriaProxy;
import org.jboss.tools.hibernate.runtime.common.AbstractFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.ICollectionMetadata;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.ICriteria;

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
	
}
