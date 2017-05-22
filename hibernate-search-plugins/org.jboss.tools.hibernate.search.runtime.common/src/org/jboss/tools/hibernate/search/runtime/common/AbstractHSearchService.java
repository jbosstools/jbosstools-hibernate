package org.jboss.tools.hibernate.search.runtime.common;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.Util;
import org.jboss.tools.hibernate.runtime.spi.IQuery;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.search.runtime.spi.IAnalyzer;
import org.jboss.tools.hibernate.search.runtime.spi.IDocument;
import org.jboss.tools.hibernate.search.runtime.spi.IField;
import org.jboss.tools.hibernate.search.runtime.spi.IFullTextSession;
import org.jboss.tools.hibernate.search.runtime.spi.IHSearchService;
import org.jboss.tools.hibernate.search.runtime.spi.IIndexReader;
import org.jboss.tools.hibernate.search.runtime.spi.ILuceneQuery;
import org.jboss.tools.hibernate.search.runtime.spi.IQueryParser;

public abstract class AbstractHSearchService implements IHSearchService {
	
	public abstract IFacadeFactory getFacadeFactory();
	public abstract IAnalyzer getAnalyzerByName(String analyzer);

	@Override
	public void newIndexRebuild(ISessionFactory sessionFactory, Set<Class> entities) {
		IFullTextSession fullTextSession = getFacadeFactory().createFullTextSession(sessionFactory);
		fullTextSession.createIndexerStartAndWait(entities.toArray(new Class[0]));
	}
	
	@Override
	public Set<Class<?>> getIndexedTypes(ISessionFactory sessionFactory) {
		Set<Class<?>> indexedTypes = new TreeSet<Class<?>>(new Comparator<Class<?>>() {

			@Override
			public int compare(Class<?> o1, Class<?> o2) {
				return o1.getName().compareTo(o2.getName());
			}
			
		});
		indexedTypes.addAll(getFacadeFactory().createFullTextSession(sessionFactory).getSearchFactory().getIndexedTypes());
		return indexedTypes;
	}
	
	@Override
	public List<Object> search(ISessionFactory sessionFactory, Class<?> entity, String defaultField, String analyzer, String request) {
		IFullTextSession session = getFacadeFactory().createFullTextSession(sessionFactory);
		IQueryParser queryParser = getFacadeFactory().createQueryParser(defaultField, getAnalyzerByName(analyzer));
		ILuceneQuery luceneQuery = queryParser.parse(request);
		IQuery query = session.createFullTextQuery(luceneQuery, entity);
		return query.list();
	}
	
//	@Override
//	public String doAnalyze(String text, String analyzerClassName) {
//		Analyzer analyzer = (Analyzer)((IFacade)getAnalyzerByName(analyzerClassName)).getTarget();
//		if (analyzer == null) {
//			return "";
//		}
//
//		try {
//			TokenStream stream = analyzer.tokenStream("field", new StringReader(text));
//			CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);
//			stream.reset();
//			StringBuilder result = new StringBuilder();
//			while (stream.incrementToken()) {
//				result.append(termAtt.toString() + "\n");
//			}
//
//			stream.end();
//			stream.close();
//			return result.toString();
//		} catch (IOException e) {
//			e.printStackTrace();
//			return "";
//		}
//	}
	
	@Override
	public String doAnalyze(String text, String analyzerClassName) {
		try {
			Object analyzer = ((IFacade)getAnalyzerByName(analyzerClassName)).getTarget();
			if (analyzer == null) {
				return "";
			}
			Object tokenStream = Util.invokeMethod(
					analyzer, 
					"tokenStream", 
					new Class[] { Class.forName("java.lang.String"), Class.forName("java.io.Reader")}, 
					new Object[] { "field", new StringReader(text)});
			Object termAtt = Util.invokeMethod(
					tokenStream, 
					"addAttribute", 
					new Class[] { Class.class }, 
					new Object[] { 
							Class.forName("org.apache.lucene.analysis.tokenattributes.CharTermAttribute", 
									true, getFacadeFactory().getClassLoader()) });
			Util.invokeMethod(
					tokenStream, 
					"reset", 
					new Class[] { }, 
					new Object[] { });
			StringBuilder result = new StringBuilder();
			while ((Boolean)Util.invokeMethod(
					tokenStream, 
					"incrementToken", 
					new Class[] { }, 
					new Object[] { })) {
				result.append(termAtt.toString() + " ");
			}
			Util.invokeMethod(
					tokenStream, 
					"end", 
					new Class[] { }, 
					new Object[] { });
			Util.invokeMethod(
					tokenStream, 
					"close", 
					new Class[] { }, 
					new Object[] { });
			return result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "Exception happened while analyzing " + e.getMessage();
		}
	}
	
	@Override
	public Set<String> getIndexedFields(ISessionFactory sessionFactory, Class<?> entity) {
		final Set<String> fields = new TreeSet<String>();
		IIndexReader ireader = getFacadeFactory().createFullTextSession(sessionFactory).getSearchFactory().getIndexReader(entity);
		if (ireader.numDocs() > 0) {
			for (IField ifield: ireader.document(0).getFields()) {
				fields.add(ifield.name());
			}
		}
		return fields;
	}
	
	@Override
	public List<Map<String, String>> getEntityDocuments(ISessionFactory sessionFactory, Class... entities) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		IIndexReader ireader = getFacadeFactory().createFullTextSession(sessionFactory).getSearchFactory().getIndexReader(entities);
		
		for (int i = 0; i < ireader.numDocs(); i++) {
			IDocument doc = ireader.document(i);
			Map<String, String> fieldValueMap = new TreeMap<String, String>();
			for (IField field: doc.getFields()) {
				fieldValueMap.put(field.name(), field.stringValue());
			}
			list.add(fieldValueMap);
		}
		return list;
	}

}
