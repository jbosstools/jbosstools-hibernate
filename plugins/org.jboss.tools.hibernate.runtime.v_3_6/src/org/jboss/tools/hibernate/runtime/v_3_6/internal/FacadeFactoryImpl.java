package org.jboss.tools.hibernate.runtime.v_3_6.internal;

import org.jboss.tools.hibernate.runtime.common.AbstractFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;

public class FacadeFactoryImpl extends AbstractFacadeFactory {
	
	public IArtifactCollector createArtifactCollector() {
		return new ArtifactCollectorFacadeImpl();
	}
	
}
