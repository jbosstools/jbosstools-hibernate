package org.jboss.tools.hibernate.search;

import org.apache.lucene.util.Version;
import org.jboss.tools.hibernate.runtime.spi.IService;
import org.jboss.tools.hibernate.runtime.spi.ServiceLookup;
import org.jboss.tools.hibernate.search.runtime.common.AbstractHSearchService;
import org.jboss.tools.hibernate.search.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.search.runtime.spi.IAnalyzer;
import org.jboss.tools.hibernate.search.runtime.spi.IHSearchService;

public class HSearchServiceImpl extends AbstractHSearchService implements IHSearchService {
	
	private IFacadeFactory facadeFactory = new FacadeFactoryImpl();
	
	@Override
	public IService getHibernateService() {
		return ServiceLookup.findService("4.3");
	}
	
	@Override
	public IFacadeFactory getFacadeFactory() {
		return this.facadeFactory;
	}
	
	public IAnalyzer getAnalyzerByName(String analyzerClassName) {
		return facadeFactory.createAnalyzerByName(analyzerClassName, Version.LUCENE_4_10_4);
	}
}
