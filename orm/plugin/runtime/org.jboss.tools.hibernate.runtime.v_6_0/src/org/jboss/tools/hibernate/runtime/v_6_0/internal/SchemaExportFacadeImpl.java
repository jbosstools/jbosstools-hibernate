package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import java.util.EnumSet;

import org.hibernate.boot.Metadata;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.jboss.tools.hibernate.runtime.common.AbstractSchemaExportFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;

public class SchemaExportFacadeImpl extends AbstractSchemaExportFacade {

	SchemaExport target = null;
	Metadata metadata = null;

	public SchemaExportFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
		this.target = (SchemaExport)target;
	}

	public void setConfiguration(IConfiguration configuration) {
		metadata = ((ConfigurationFacadeImpl)configuration).getMetadata();
	}

	@Override
	public void create() {
		target.create(EnumSet.of(TargetType.DATABASE), metadata);
	}

}
