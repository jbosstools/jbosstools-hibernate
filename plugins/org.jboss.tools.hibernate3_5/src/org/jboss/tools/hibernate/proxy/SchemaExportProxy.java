package org.jboss.tools.hibernate.proxy;

import java.util.List;

import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;

public class SchemaExportProxy implements ISchemaExport {
	
	private SchemaExport target;

	public SchemaExportProxy(SchemaExport schemaExport) {
		target = schemaExport;
	}

	@Override
	public void create(boolean script, boolean export) {
		target.create(script, export);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Throwable> getExceptions() {
		return target.getExceptions();
	}

}
