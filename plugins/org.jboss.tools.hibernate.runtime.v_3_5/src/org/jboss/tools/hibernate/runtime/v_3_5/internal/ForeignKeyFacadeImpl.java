package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import org.hibernate.mapping.ForeignKey;
import org.jboss.tools.hibernate.runtime.common.AbstractForeignKeyFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class ForeignKeyFacadeImpl extends AbstractForeignKeyFacade {
	
	public ForeignKeyFacadeImpl(
			IFacadeFactory facadeFactory,
			ForeignKey foreignKey) {
		super(facadeFactory, foreignKey);
	}
	
}
