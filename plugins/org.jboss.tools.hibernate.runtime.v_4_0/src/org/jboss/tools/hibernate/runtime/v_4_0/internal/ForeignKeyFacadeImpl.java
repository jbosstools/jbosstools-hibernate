package org.jboss.tools.hibernate.runtime.v_4_0.internal;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.jboss.tools.hibernate.runtime.common.AbstractForeignKeyFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class ForeignKeyFacadeImpl extends AbstractForeignKeyFacade {
	
	public ForeignKeyFacadeImpl(
			IFacadeFactory facadeFactory,
			ForeignKey foreignKey) {
		super(facadeFactory, foreignKey);
	}

	public ForeignKey getTarget() {
		return (ForeignKey)super.getTarget();
	}

	@Override
	public boolean containsColumn(IColumn column) {
		assert column instanceof IFacade;
		return getTarget().containsColumn((Column)((IFacade)column).getTarget());
	}

}
