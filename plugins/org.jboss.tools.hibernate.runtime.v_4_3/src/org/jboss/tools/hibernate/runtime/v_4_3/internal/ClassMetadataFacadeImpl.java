package org.jboss.tools.hibernate.runtime.v_4_3.internal;

import org.hibernate.metadata.ClassMetadata;
import org.jboss.tools.hibernate.runtime.common.AbstractClassMetadataFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class ClassMetadataFacadeImpl extends AbstractClassMetadataFacade {
	
	public ClassMetadataFacadeImpl(
			IFacadeFactory facadeFactory,
			ClassMetadata classMetadata) {
		super(facadeFactory, classMetadata);
	}

	public ClassMetadata getTarget() {
		return (ClassMetadata)super.getTarget();
	}

}
