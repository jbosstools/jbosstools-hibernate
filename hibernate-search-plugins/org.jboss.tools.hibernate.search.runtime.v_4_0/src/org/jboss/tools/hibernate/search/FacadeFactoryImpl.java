package org.jboss.tools.hibernate.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.util.Version;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.Util;
import org.jboss.tools.hibernate.search.runtime.common.AbstractFacadeFactory;
import org.jboss.tools.hibernate.search.runtime.common.AbstractQueryParser;
import org.jboss.tools.hibernate.search.runtime.spi.IAnalyzer;
import org.jboss.tools.hibernate.search.runtime.spi.IQueryParser;

public class FacadeFactoryImpl extends AbstractFacadeFactory {
	
	private IFacadeFactory hibernateFacadeFactory = 
			new org.jboss.tools.hibernate.runtime.v_4_0.internal.FacadeFactoryImpl();
	
	@Override
	protected Class<?> getQueryParserClass() {
		return Util.getClass("org.apache.lucene.queryParser.QueryParser", getClassLoader());
	}

	@Override
	public ClassLoader getClassLoader() {
		return FacadeFactoryImpl.class.getClassLoader();
	}

	@Override
	public IFacadeFactory getHibernateFacadeFactory() {
		return hibernateFacadeFactory;
	}
	
	@Override
	public IQueryParser createQueryParser(String defaultField, IAnalyzer analyzer) {
		Analyzer luceneAnalyzer = (Analyzer)((IFacade)analyzer).getTarget();
		QueryParser targetParser = new QueryParser(Version.LUCENE_34, defaultField, luceneAnalyzer);
		return new AbstractQueryParser(this, targetParser) {};
	}
}
