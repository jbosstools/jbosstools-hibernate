package org.jboss.tools.hibernate.proxy;

import org.jboss.tools.hibernate.runtime.common.AbstractHibernateMappingExporterFacade;
import org.jboss.tools.hibernate.runtime.spi.IExportPOJODelegate;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.v_3_6.internal.HibernateMappingExporterExtension;

public class HibernateMappingExporterProxy extends AbstractHibernateMappingExporterFacade {
	
	private HibernateMappingExporterExtension target = null;

	public HibernateMappingExporterProxy(
			IFacadeFactory facadeFactory, 
			HibernateMappingExporterExtension hibernateMappingExporter) {
		super(facadeFactory, hibernateMappingExporter);
		target = hibernateMappingExporter;
	}
	
	@Override
	public void setExportPOJODelegate(IExportPOJODelegate delegate) {
		target.setDelegate(delegate);
	}
	
}
