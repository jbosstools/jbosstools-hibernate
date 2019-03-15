package org.jboss.tools.hibernate.search.runtime.spi;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.tools.hibernate.runtime.spi.IService;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;

public interface IHSearchService {

	void newIndexRebuild(ISessionFactory sessionFactory, Set<Class> entities);
	
	String doAnalyze(String text, String analyzerClassName);

	List <Map<String, String>> getEntityDocuments(ISessionFactory sessionFactory, Class... entities);
	
	Set<Class<?>> getIndexedTypes(ISessionFactory sessionFactory);
	
	IService getHibernateService();
	
	Set<String> getIndexedFields(ISessionFactory sessionFactory, Class<?> entity);
	
	List<Object> search(ISessionFactory sessionFactory, Class<?> entity, String defaultField, String analyzer, String request);
}
