package org.jboss.tools.hibernate.search.runtime.common;

import org.jboss.tools.hibernate.search.runtime.spi.IAnalyzer;

public abstract class AbstractAnalyzer extends AbstractFacade implements IAnalyzer {

	public AbstractAnalyzer(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}

}
