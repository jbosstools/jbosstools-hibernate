package org.jboss.tools.hibernate.runtime.v_5_4.internal.util;

import org.hibernate.boot.model.naming.ObjectNameNormalizer;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.boot.spi.ClassLoaderAccess;
import org.hibernate.boot.spi.InFlightMetadataCollector;
import org.hibernate.boot.spi.MappingDefaults;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.MetadataBuildingOptions;

public class DummyMetadataBuildingContext implements MetadataBuildingContext {

	@Override
	public BootstrapContext getBootstrapContext() {
		return null;
	}

	@Override
	public MetadataBuildingOptions getBuildingOptions() {
		return null;
	}

	@Override
	public MappingDefaults getMappingDefaults() {
		return null;
	}

	@Override
	public InFlightMetadataCollector getMetadataCollector() {
		return null;
	}

	@Override
	public ClassLoaderAccess getClassLoaderAccess() {
		return null;
	}

	@Override
	public ObjectNameNormalizer getObjectNameNormalizer() {
		// TODO Auto-generated method stub
		return null;
	}

}
