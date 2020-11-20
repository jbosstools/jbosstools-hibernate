package org.jboss.tools.hibernate.runtime.v_6_0.internal.util;

import org.hibernate.boot.internal.BootstrapContextImpl;
import org.hibernate.boot.internal.InFlightMetadataCollectorImpl;
import org.hibernate.boot.internal.MetadataBuilderImpl;
import org.hibernate.boot.internal.MetadataBuildingContextRootImpl;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.boot.spi.InFlightMetadataCollector;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.MetadataBuildingOptions;
import org.hibernate.dialect.Dialect;

public class DummyMetadataBuildingContext {
	
	public static MetadataBuildingContext INSTANCE = createInstance();
	
	private static MetadataBuildingContext createInstance() {
		StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder();
		ssrb.applySetting("hibernate.dialect", DummyDialect.class.getName());
		StandardServiceRegistry serviceRegistry = ssrb.build();
		MetadataBuildingOptions metadataBuildingOptions = new MetadataBuilderImpl.MetadataBuildingOptionsImpl(serviceRegistry);
		BootstrapContext bootstrapContext = new BootstrapContextImpl(serviceRegistry, metadataBuildingOptions);
		InFlightMetadataCollector inflightMetadataCollector = new InFlightMetadataCollectorImpl(bootstrapContext, metadataBuildingOptions);
		return new MetadataBuildingContextRootImpl(bootstrapContext, metadataBuildingOptions, inflightMetadataCollector);
	}

	public static class DummyDialect extends Dialect {
		@Override public int getVersion() {return 0;}		
	}

}
