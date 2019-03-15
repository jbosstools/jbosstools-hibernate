package org.jboss.tools.hibernate.search.runtime.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.Util;
import org.jboss.tools.hibernate.runtime.spi.ISession;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.search.runtime.spi.IAnalyzer;
import org.jboss.tools.hibernate.search.runtime.spi.IFullTextSession;
import org.jboss.tools.hibernate.search.runtime.spi.IQueryParser;
import org.jboss.tools.hibernate.search.runtime.spi.ISearchFactory;

public abstract class AbstractFacadeFactory implements IFacadeFactory {
	
	@Override
	public ISearchFactory createSearchFactory(Object target) {
		return new AbstractSearchFactoryFacade(this, target) {};
	}
	
	@Override
	public IFullTextSession createFullTextSession(ISessionFactory sessionFactory) {
		ISession session = sessionFactory.openSession();
		Object targetSession = ((IFacade)session).getTarget();
		Object targetFullTextSession = 
				Util.invokeMethod(
						getSearchClass(), 
						"getFullTextSession", 
						new Class[] { getSessionClass() }, 
						new Object[] { targetSession });
		return new AbstractFullTextSessionFacade(this, targetFullTextSession) {};
	}
	
	@Override
	public IQueryParser createQueryParser(String defaultField, IAnalyzer analyzer) {
		assert analyzer instanceof IFacade;
		try {
			Constructor<?> constructor = 
					getQueryParserClass().getConstructor(new Class[] { String.class, getAnalyzerClass()});
			Object target = constructor.newInstance(new Object[] { defaultField,  ((IFacade)analyzer).getTarget() });
			return new AbstractQueryParser(this, target) {};
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public IAnalyzer createAnalyzerByName(String analyzerClassName, Object luceneVersion) {
		try {
			Class<?> analyzerClass = Class.forName(analyzerClassName, true, getClassLoader());
			for (Constructor<?> constructor : analyzerClass.getConstructors()) {
				if (constructor.getParameterTypes().length == 0) {
					constructor.setAccessible(true);
					return new AbstractAnalyzer(this, constructor.newInstance()) {};
				}
				if (constructor.getParameterTypes().length == 1 && constructor.getParameterTypes()[0].equals(luceneVersion.getClass())) {
					constructor.setAccessible(true);
					return new AbstractAnalyzer(this, constructor.newInstance(luceneVersion)) {};
				}
			}
		} catch (ClassNotFoundException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected Class<?> getSearchClass() {
		return Util.getClass("org.hibernate.search.Search", getClassLoader());
	}
	
	protected Class<?> getSessionClass() {
		return Util.getClass("org.hibernate.Session", getClassLoader());
	}
	
	protected Class<?> getAnalyzerClass() {
		return Util.getClass("org.apache.lucene.analysis.Analyzer", getClassLoader());
	}
	
	protected Class<?> getQueryParserClass() {
		return Util.getClass("org.apache.lucene.queryparser.classic.QueryParser", getClassLoader());
	}

}
