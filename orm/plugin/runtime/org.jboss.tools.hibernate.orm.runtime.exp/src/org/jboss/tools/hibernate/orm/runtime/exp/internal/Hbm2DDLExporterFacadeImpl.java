package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.ddl.DdlExporter;
import org.jboss.tools.hibernate.runtime.common.AbstractHbm2DDLExporterFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;

public class Hbm2DDLExporterFacadeImpl extends AbstractHbm2DDLExporterFacade {

	public Hbm2DDLExporterFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}
	
	public void setExport(boolean b) {
		((DdlExporter)getTarget()).getProperties().put(ExporterConstants.EXPORT_TO_DATABASE, b);
	}

}
