package org.jboss.tools.hibernate.runtime.v_5_2.internal.util;

import org.hibernate.boot.internal.InFlightMetadataCollectorImpl;
import org.hibernate.boot.internal.MetadataBuilderImpl.MetadataBuildingOptionsImpl;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.dialect.Dialect;
import org.hibernate.type.TypeResolver;

public class DummyMetadataImplementor {

	public static final MetadataImplementor INSTANCE = createInstance();
						
	private static MetadataImplementor createInstance() {
		try {
			StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder();
			ssrb.applySetting("hibernate.dialect", DummyDialect.class.getName());
			return new InFlightMetadataCollectorImpl(
				new MetadataBuildingOptionsImpl(ssrb.build()),
				new TypeResolver());
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}
	
	public static class DummyDialect extends Dialect {}

}
