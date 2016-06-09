package org.jboss.tools.hibernate.runtime.v_5_2.internal;

import org.jboss.tools.hibernate.runtime.common.AbstractExporterFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;

// TODO: JBIDE-22579 - Remove this class again
public class ExporterFacadeImpl extends AbstractExporterFacade {

	public ExporterFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}

	protected String getHibernateConfigurationExporterClassName() {
		return "org.hibernate.tool.hbm2x.HibernateConfigurationExporter";
	}

}
