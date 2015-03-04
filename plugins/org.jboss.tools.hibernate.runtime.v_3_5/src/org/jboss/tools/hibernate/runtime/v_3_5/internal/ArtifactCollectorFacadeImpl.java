package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import org.jboss.tools.hibernate.runtime.common.AbstractArtifactCollectorFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class ArtifactCollectorFacadeImpl extends AbstractArtifactCollectorFacade {
	
	public ArtifactCollectorFacadeImpl(IFacadeFactory facadeFactory) {
		this.facadeFactory = facadeFactory;
	}
	
}
	