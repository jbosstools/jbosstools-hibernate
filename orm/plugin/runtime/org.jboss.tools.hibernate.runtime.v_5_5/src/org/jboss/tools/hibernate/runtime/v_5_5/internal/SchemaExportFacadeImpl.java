package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import org.hibernate.boot.Metadata;
import org.jboss.tools.hibernate.runtime.common.AbstractSchemaExportFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;

public class SchemaExportFacadeImpl extends AbstractSchemaExportFacade {

	private Metadata metadata = null;

	public SchemaExportFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}

	public void setConfiguration(IConfiguration configuration) {
		metadata = ((ConfigurationFacadeImpl)configuration).getMetadata();
	}

}
