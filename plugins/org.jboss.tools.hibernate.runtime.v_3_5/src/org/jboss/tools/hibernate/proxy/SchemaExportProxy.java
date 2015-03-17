package org.jboss.tools.hibernate.proxy;

import java.util.List;

import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;

public class SchemaExportProxy implements ISchemaExport {
	
	private SchemaExport target;

	public SchemaExportProxy(
			IFacadeFactory facadeFactory, 
			SchemaExport schemaExport) {
		target = schemaExport;
	}
	
	public SchemaExport getTarget() {
		return target;
	}

	@Override
	public void create(boolean script, boolean export) {
		getTarget().create(script, export);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Throwable> getExceptions() {
		return getTarget().getExceptions();
	}

}
