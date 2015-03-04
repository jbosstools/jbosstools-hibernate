package org.jboss.tools.hibernate.runtime.v_4_3.internal;

import org.jboss.tools.hibernate.runtime.common.AbstractArtifactCollectorFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class ArtifactCollectorFacadeImpl extends AbstractArtifactCollectorFacade {
	
	public ArtifactCollectorFacadeImpl(IFacadeFactory facadeFactory) {
		this.facadeFactory = facadeFactory;
	}
	
}

