package org.jboss.tools.hibernate.runtime.v_6_2.internal;

import org.jboss.tools.hibernate.runtime.common.AbstractFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IGenericExporter;
import org.jboss.tools.hibernate.runtime.spi.IHbm2DDLExporter;
import org.jboss.tools.hibernate.runtime.spi.IOverrideRepository;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.IQueryExporter;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;

public class FacadeFactoryImpl  extends AbstractFacadeFactory {

	@Override
	public ClassLoader getClassLoader() {
		return FacadeFactoryImpl.class.getClassLoader();
	}

	@Override
	public IReverseEngineeringStrategy createReverseEngineeringStrategy(Object target) {
		return new ReverseEngineeringStrategyFacadeImpl(this, target);
	}

	public IOverrideRepository createOverrideRepository(Object target) {
		return new OverrideRepositoryFacadeImpl(this, target);
	}
	
	@Override
	public ISchemaExport createSchemaExport(Object target) {
		return new SchemaExportFacadeImpl(this, target);
	}
	
	@Override
	public IGenericExporter createGenericExporter(Object target) {
		return new GenericExporterFacadeImpl(this, target);
	}

	@Override
	public IHbm2DDLExporter createHbm2DDLExporter(Object target) {
		return new Hbm2DDLExporterFacadeImpl(this, target);
	}

	@Override
	public IQueryExporter createQueryExporter(Object target) {
		return new QueryExporterFacadeImpl(this, target);
	}

	@Override
	public IExporter createExporter(Object target) {
		return new ExporterFacadeImpl(this, target);
	}
	
	@Override
	public IClassMetadata createClassMetadata(Object target) {
		return new ClassMetadataFacadeImpl(this, target);
	}
	
	@Override
	public IPersistentClass createSpecialRootClass(IProperty arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}