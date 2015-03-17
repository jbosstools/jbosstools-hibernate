package org.jboss.tools.hibernate.proxy;

import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.jboss.tools.hibernate.runtime.common.AbstractSchemaExportFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class SchemaExportProxy 
extends AbstractSchemaExportFacade {
	
	public SchemaExportProxy(
			IFacadeFactory facadeFactory, 
			SchemaExport schemaExport) {
		super(facadeFactory, schemaExport);
	}

	public SchemaExport getTarget() {
		return (SchemaExport)super.getTarget();
	}

}
