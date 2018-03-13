package org.jboss.tools.hibernate.runtime.v_5_1.internal;

import java.util.EnumSet;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.hibernate.tool.util.MetadataHelper;
import org.jboss.tools.hibernate.runtime.common.AbstractSchemaExportFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;

public class SchemaExportFacadeImpl extends AbstractSchemaExportFacade {
	
	private SchemaExport target = null;
	private Metadata metadata = null;

	public SchemaExportFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
		this.target = (SchemaExport)target;
	}
	
	public void setConfiguration(IConfiguration configuration) {
		Configuration configurationTarget = (Configuration)((IFacade)configuration).getTarget();
		this.metadata = MetadataHelper.getMetadata(configurationTarget);
	}
	
	@Override
	public void create(boolean script, boolean export) {
		target.create(EnumSet.of(TargetType.DATABASE), metadata);
	}

}
