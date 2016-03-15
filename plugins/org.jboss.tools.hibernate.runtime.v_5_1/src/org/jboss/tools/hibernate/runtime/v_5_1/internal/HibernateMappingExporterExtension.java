package org.jboss.tools.hibernate.runtime.v_5_1.internal;

import java.io.File;
import java.util.Map;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IExportPOJODelegate;

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

	public void superExportPOJO(Map<String, Object> map, POJOClass pojoClass) {
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
					facadeFactory.createPOJOClass(pojoClass));
		}
	}
}
