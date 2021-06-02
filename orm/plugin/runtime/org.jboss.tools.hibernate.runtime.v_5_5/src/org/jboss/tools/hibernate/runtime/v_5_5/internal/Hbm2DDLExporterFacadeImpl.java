package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import org.jboss.tools.hibernate.runtime.common.AbstractHbm2DDLExporterFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IHbm2DDLExporter;

public class Hbm2DDLExporterFacadeImpl extends AbstractHbm2DDLExporterFacade implements IHbm2DDLExporter {

	public Hbm2DDLExporterFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}

}
