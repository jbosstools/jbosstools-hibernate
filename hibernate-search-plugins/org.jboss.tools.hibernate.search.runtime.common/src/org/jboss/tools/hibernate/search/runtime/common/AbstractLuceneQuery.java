package org.jboss.tools.hibernate.search.runtime.common;

import org.jboss.tools.hibernate.search.runtime.spi.ILuceneQuery;

public abstract class AbstractLuceneQuery extends AbstractFacade implements ILuceneQuery {

	public AbstractLuceneQuery(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}

}
