package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.IDialect;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IGenericExporter;
import org.jboss.tools.hibernate.runtime.spi.IHbm2DDLExporter;
import org.jboss.tools.hibernate.runtime.spi.IMapping;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IOverrideRepository;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;

public abstract class AbstractFacadeFactory implements IFacadeFactory {

	public IArtifactCollector createArtifactCollector() {
		return new AbstractArtifactCollectorFacade(this) {};
	}
	
	public ICfg2HbmTool createCfg2HbmTool() {
		return new AbstractCfg2HbmToolFacade(this) {};
	}
	
	public INamingStrategy createNamingStrategy(Object target) {
		return new AbstractNamingStrategyFacade(this, target) {};
	}
	
	public IDialect createDialect(Object target) {
		return new AbstractDialectFacade(this, target) {};
	}
	
	public IMapping createMapping(Object target ) {
		return new AbstractMappingFacade(this, target) {};
	}
	
	public IReverseEngineeringSettings createReverseEngineeringSettings(Object target) {
		return new AbstractReverseEngineeringSettingsFacade(this, target) {};
	}
	
	public IReverseEngineeringStrategy createReverseEngineeringStrategy(Object target) {
		return new AbstractReverseEngineeringStrategyFacade(this, target) {};
	}
	
	public IOverrideRepository createOverrideRepository(Object target) {
		return new AbstractOverrideRepositoryFacade(this, target) {};
	}
	
	public ISchemaExport createSchemaExport(Object target) {
		return new AbstractSchemaExportFacade(this, target) {};
	}
	
	public IGenericExporter createGenericExporter(Object target) {
		return new AbstractGenericExporterFacade(this, target) {};
	}
	
	public IHbm2DDLExporter createHbm2DDLExporter(Object target) {
		return new AbstractHbm2DDLExporterFacade(this, target) {};
	}
	
}
