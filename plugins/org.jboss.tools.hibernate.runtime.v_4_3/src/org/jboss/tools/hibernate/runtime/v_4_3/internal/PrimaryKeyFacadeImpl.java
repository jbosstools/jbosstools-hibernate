package org.jboss.tools.hibernate.runtime.v_4_3.internal;

import org.hibernate.mapping.PrimaryKey;
import org.jboss.tools.hibernate.runtime.common.AbstractPrimaryKeyFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class PrimaryKeyFacadeImpl extends AbstractPrimaryKeyFacade {

	public PrimaryKeyFacadeImpl(
			IFacadeFactory facadeFactory,
			PrimaryKey primaryKey) {
		super(facadeFactory, primaryKey);
	}

}
