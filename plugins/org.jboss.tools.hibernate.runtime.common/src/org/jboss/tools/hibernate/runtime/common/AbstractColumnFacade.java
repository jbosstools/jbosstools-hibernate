package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public abstract class AbstractColumnFacade 
extends AbstractFacade 
implements IColumn {

	public AbstractColumnFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	public String getName() {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getName", 
				new Class[] {}, 
				new Object[] {});
	}

}
