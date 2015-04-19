package org.jboss.tools.hibernate.proxy;

import java.io.File;
import java.util.Map;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.jboss.tools.hibernate.runtime.spi.IExportPOJODelegate;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class HibernateMappingExporterExtension 
extends HibernateMappingExporter {
	
	private IExportPOJODelegate delegateExporter;
	private IFacadeFactory facadeFactory;
	
	public HibernateMappingExporterExtension(IFacadeFactory facadeFactory, Configuration cfg, File file) {
		super(cfg, file);
		this.facadeFactory = facadeFactory;
	}
	
	public void setDelegate(IExportPOJODelegate delegate) {
		delegateExporter = delegate;
	}

	void superExportPOJO(Map<Object, Object> map, POJOClass pojoClass) {
		super.exportPOJO(map, pojoClass);
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void exportPOJO(Map map, POJOClass pojoClass) {
		if (delegateExporter == null) {
			super.exportPOJO(map, pojoClass);
		} else {
			delegateExporter.exportPOJO(
					(Map<Object, Object>)map, 
					new POJOClassProxy(facadeFactory, pojoClass));
		}
	}
}
