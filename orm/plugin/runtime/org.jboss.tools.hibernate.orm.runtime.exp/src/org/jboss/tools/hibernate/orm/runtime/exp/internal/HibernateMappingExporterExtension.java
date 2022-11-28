package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import java.io.File;
import java.util.Map;

import org.hibernate.tool.internal.export.hbm.HbmExporter;
import org.hibernate.tool.internal.export.java.POJOClass;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.ConfigurationMetadataDescriptor;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IExportPOJODelegate;

public class HibernateMappingExporterExtension extends HbmExporter {
	
	private IExportPOJODelegate delegateExporter;
	private IFacadeFactory facadeFactory;

	public HibernateMappingExporterExtension(IFacadeFactory facadeFactory, IConfiguration cfg, File file) {
		this.facadeFactory = facadeFactory;
		getProperties().put(
				METADATA_DESCRIPTOR, 
				new ConfigurationMetadataDescriptor((IConfiguration)cfg));
		if (file != null) {
			getProperties().put(OUTPUT_FILE_NAME, file);
		}
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
