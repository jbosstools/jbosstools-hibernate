package org.jboss.tools.hibernate.search.runtime.spi;

import org.jboss.tools.hibernate.runtime.spi.IQuery;

public interface IFullTextSession {
	ISearchFactory getSearchFactory();
	void createIndexerStartAndWait(Class<?>[] entities);
	IQuery createFullTextQuery(ILuceneQuery luceneQuery, Class<?> entity);
}
