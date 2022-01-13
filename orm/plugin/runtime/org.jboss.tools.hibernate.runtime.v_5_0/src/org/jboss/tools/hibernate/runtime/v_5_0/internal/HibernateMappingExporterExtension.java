package org.jboss.tools.hibernate.runtime.v_5_0.internal;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.internal.MetadataImpl;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.hbm2x.AbstractExporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IExportPOJODelegate;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;

public class HibernateMappingExporterExtension 
extends HibernateMappingExporter {
	
	private IExportPOJODelegate delegateExporter;
	private IFacadeFactory facadeFactory;
	
	public HibernateMappingExporterExtension(IFacadeFactory facadeFactory, IConfiguration  cfg, File file) {
		super((Configuration)((IFacade)cfg).getTarget(), file);
		this.facadeFactory = facadeFactory;
		createMetadata(cfg);
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
	
	void createMetadata(IConfiguration cfg) {
		try {
			if (cfg instanceof ConfigurationFacadeImpl) {
				ConfigurationFacadeImpl configuration = (ConfigurationFacadeImpl)cfg;
				Metadata metadata = configuration.getMetadata();
				if (metadata instanceof MetadataImpl ) {
					MetadataImpl metadataImpl = (MetadataImpl)metadata;
					HashMap<String, PersistentClass> map = new HashMap<String, PersistentClass>();
					for (IPersistentClass ipc : configuration.addedClasses) {
						PersistentClass pc = (PersistentClass)((IFacade)ipc).getTarget();
						map.put(pc.getEntityName(), pc);
					}
					Field entityBindingMapField = metadataImpl.getClass().getDeclaredField("entityBindingMap");
					if (entityBindingMapField != null) {
						entityBindingMapField.setAccessible(true);
						entityBindingMapField.set(metadataImpl, map);
					}
				}
				Field metadataField = AbstractExporter.class.getDeclaredField("metadata");
				if (metadataField != null) {
					metadataField.setAccessible(true);
					metadataField.set(this, metadata);
				}
			}
		}
		catch (Throwable t) {
			throw new RuntimeException("Problem while creating metadata", t);
		}
	}
	
	
}
