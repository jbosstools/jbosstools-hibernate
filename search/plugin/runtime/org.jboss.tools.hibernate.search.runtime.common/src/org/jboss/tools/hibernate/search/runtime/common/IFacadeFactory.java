package org.jboss.tools.hibernate.search.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.search.runtime.spi.IAnalyzer;
import org.jboss.tools.hibernate.search.runtime.spi.IFullTextSession;
import org.jboss.tools.hibernate.search.runtime.spi.IQueryParser;
import org.jboss.tools.hibernate.search.runtime.spi.ISearchFactory;

public interface IFacadeFactory {
	org.jboss.tools.hibernate.runtime.common.IFacadeFactory getHibernateFacadeFactory();
	ClassLoader getClassLoader();
	IFullTextSession createFullTextSession(ISessionFactory sessionFactory);
	ISearchFactory createSearchFactory(Object target);
	IQueryParser createQueryParser(String defaultField, IAnalyzer analyzer);
	IAnalyzer createAnalyzerByName(String analyzerClassName, Object luceneVersion);
}
