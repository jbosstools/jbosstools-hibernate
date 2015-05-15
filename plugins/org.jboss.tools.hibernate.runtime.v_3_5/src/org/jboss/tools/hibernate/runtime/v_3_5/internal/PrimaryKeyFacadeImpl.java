package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import org.hibernate.mapping.PrimaryKey;
import org.jboss.tools.hibernate.runtime.common.AbstractPrimaryKeyFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class PrimaryKeyFacadeImpl extends AbstractPrimaryKeyFacade {

	public PrimaryKeyFacadeImpl(
			IFacadeFactory facadeFactory,
			PrimaryKey primaryKey) {
		super(facadeFactory, primaryKey);
	}

	public PrimaryKey getTarget() {
		return (PrimaryKey)super.getTarget();
	}

}
