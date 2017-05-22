package org.jboss.tools.hibernate.search;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.lucene.util.Version;
import org.hibernate.search.annotations.Indexed;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.IService;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.runtime.spi.ServiceLookup;
import org.jboss.tools.hibernate.search.runtime.common.AbstractHSearchService;
import org.jboss.tools.hibernate.search.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.search.runtime.spi.IAnalyzer;
import org.jboss.tools.hibernate.search.runtime.spi.IHSearchService;

public class HSearchServiceImpl extends AbstractHSearchService implements IHSearchService {
	
	private IFacadeFactory facadeFactory = new FacadeFactoryImpl();
	
	@Override
	public IService getHibernateService() {
		return ServiceLookup.findService("4.0");
	}
	
	@Override
	public IFacadeFactory getFacadeFactory() {
		return this.facadeFactory;
	}
	
	public IAnalyzer getAnalyzerByName(String analyzerClassName) {
		return facadeFactory.createAnalyzerByName(analyzerClassName, Version.LUCENE_34);
	}
	
	@Override
	public Set<Class<?>> getIndexedTypes(ISessionFactory sessionFactory) {
		Map<String, IClassMetadata> meta = sessionFactory.getAllClassMetadata();
		Set<Class<?>> entities = new HashSet<Class<?>>();
		for (String entity : new TreeSet<String>(meta.keySet())) {
			Class<?> entityClass = meta.get(entity).getMappedClass();
			Annotation[] annotations = entityClass.getAnnotations();
			for (Annotation annotation: annotations) {
				if (Indexed.class.isAssignableFrom(annotation.annotationType())) {
					entities.add(entityClass);
					break;
				}
			}
		}
		return entities;
	}
}
