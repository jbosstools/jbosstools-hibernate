package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import org.jboss.tools.hibernate.runtime.common.AbstractHibernateMappingExporterFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;

public class HibernateMappingExporterFacadeImpl 
	extends AbstractHibernateMappingExporterFacade
		implements IHibernateMappingExporter {

	public HibernateMappingExporterFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}

}
