package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import org.hibernate.boot.Metadata;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.jboss.tools.hibernate.runtime.common.AbstractSchemaExportFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;

public class SchemaExportFacadeImpl extends AbstractSchemaExportFacade {

	SchemaExport target = null;
	Metadata metadata = null;

	public SchemaExportFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
		this.target = (SchemaExport)target;
	}

}
