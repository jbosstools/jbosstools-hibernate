package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import java.io.File;
import java.util.Map;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.internal.export.hbm.HbmExporter;
import org.hibernate.tool.internal.export.java.POJOClass;
import org.hibernate.tool.orm.jbt.util.ConfigurationMetadataDescriptor;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IExportPOJODelegate;

public class HibernateMappingExporterExtension extends HbmExporter {
	
	private IExportPOJODelegate delegateExporter;

	public HibernateMappingExporterExtension(IFacadeFactory facadeFactory, IConfiguration cfg, File file) {
		getProperties().put(
				METADATA_DESCRIPTOR, 
				new ConfigurationMetadataDescriptor((Configuration)((IFacade)cfg).getTarget()));
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
			delegateExporter.exportPojo(
					(Map<Object, Object>)map, 
					pojoClass,
					pojoClass.getQualifiedDeclarationName());
		}
	}
}
