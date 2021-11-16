package org.jboss.tools.hibernate.runtime.v_5_2.internal;

import java.util.EnumSet;

import org.hibernate.boot.Metadata;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.jboss.tools.hibernate.runtime.common.AbstractSchemaExportFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;

public class SchemaExportFacadeImpl extends AbstractSchemaExportFacade {
	
	private Metadata metadata = null;

	public SchemaExportFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
		assert target instanceof SchemaExport;
	}
	
	public void setConfiguration(IConfiguration configuration) {
		metadata = ((ConfigurationFacadeImpl)configuration).getMetadata();
	}
	
	@Override
	public void create() {
		((SchemaExport)getTarget()).create(EnumSet.of(TargetType.DATABASE), metadata);
	}

}
