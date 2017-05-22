package org.jboss.tools.hibernate.search.runtime.common;

import org.jboss.tools.hibernate.runtime.common.Util;
import org.jboss.tools.hibernate.search.runtime.spi.ILuceneQuery;
import org.jboss.tools.hibernate.search.runtime.spi.IQueryParser;

public abstract class AbstractQueryParser extends AbstractFacade implements IQueryParser {

	public AbstractQueryParser(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}

	@Override
	public ILuceneQuery parse(String request) {
		Object targetLuceneQuery = Util.invokeMethod(
				getTarget(), 
				"parse", 
				new Class[] { String.class }, 
				new Object[] { request });
		return new AbstractLuceneQuery(getFacadeFactory(), targetLuceneQuery) {};
	}

}
