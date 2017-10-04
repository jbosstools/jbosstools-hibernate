package org.jboss.tools.hibernate.search.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.jboss.tools.common.base.test.validation.TestUtil;
import org.jboss.tools.hibernate.runtime.spi.IQuery;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.search.runtime.common.AbstractAnalyzer;
import org.jboss.tools.hibernate.search.runtime.common.AbstractHSearchService;
import org.jboss.tools.hibernate.search.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.search.runtime.spi.HSearchServiceLookup;
import org.jboss.tools.hibernate.search.runtime.spi.IAnalyzer;
import org.jboss.tools.hibernate.search.runtime.spi.IDocument;
import org.jboss.tools.hibernate.search.runtime.spi.IField;
import org.jboss.tools.hibernate.search.runtime.spi.IFullTextSession;
import org.jboss.tools.hibernate.search.runtime.spi.IIndexReader;
import org.jboss.tools.hibernate.search.runtime.spi.ILuceneQuery;
import org.jboss.tools.hibernate.search.runtime.spi.IQueryParser;
import org.jboss.tools.hibernate.search.runtime.spi.ISearchFactory;
import org.jboss.tools.hibernate.search.test.db.TestDbStarter;
import org.jboss.tools.test.util.ResourcesUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class HSearchServiceImplTest {
	public static final String PLUGIN_ID = "org.jboss.tools.hibernate.search.runtime.v_5_3.test";
	public static final String PROJECT_NAME = "TestProject";
	public static final String PROJECT_PATH = "projects/TestProject";
	public static final String XML_CONFIG_PATH = "projects/TestProject/src/hibernate.cfg.xml";

	private AbstractHSearchService serviceImpl = (AbstractHSearchService)HSearchServiceLookup.findService("5.3");

	@BeforeClass
	public static void setUp() {
		TestDbStarter.startDB();
		importProject();
	}

	@AfterClass
	public static void cleanUp() throws CoreException {
		TestDbStarter.stopDB();
		ResourcesUtils.deleteProject(PROJECT_NAME);
	}

	@Test
	public void testGetHibernateService() {
		assertEquals("org.jboss.tools.hibernate.runtime.v_4_3.internal.ServiceImpl",
				serviceImpl.getHibernateService().getClass().getName());
	}

	@Test
	public void testNotNullFacadeFactory() {
		assertNotNull(((AbstractHSearchService)serviceImpl).getFacadeFactory());
	}

	@Test
	public void testGetIndexedTypes() {
		ConsoleConfiguration consoleConfig = KnownConfigurations.getInstance().find("TestProject");
		consoleConfig.build();
		consoleConfig.buildSessionFactory();
		assertTrue(serviceImpl.getIndexedTypes(consoleConfig.getSessionFactory()).size() == 1);
		assertEquals("org.bdshadow.Actor", ((Class<?>)serviceImpl.getIndexedTypes(consoleConfig.getSessionFactory()).toArray()[0]).getCanonicalName());
	}

	@Test
	public void testGetAnalyzerByName() {
		IAnalyzer analyzer = serviceImpl.getAnalyzerByName("org.apache.lucene.analysis.core.WhitespaceAnalyzer");
		assertTrue(analyzer instanceof AbstractAnalyzer);
		assertEquals("org.apache.lucene.analysis.core.WhitespaceAnalyzer",
				((AbstractAnalyzer)analyzer).getTarget().getClass().getCanonicalName());
	}

	@Test
	public void testDoAnalyze() {
		assertEquals("hi my name dmitrii bocharov",
				serviceImpl.doAnalyze("Hi, my name is Dmitrii Bocharov!", "org.apache.lucene.analysis.en.EnglishAnalyzer"));
	}

	@Test
	public void testNewIndexRebuild() {
		IFacadeFactory facadeFactoryMock = mock(IFacadeFactory.class);
		ISessionFactory sessionFactoryMock = mock(ISessionFactory.class);
		IFullTextSession fullTextSessionMock = mock(IFullTextSession.class);
		AbstractHSearchService hsearchServiceSpy = spy(serviceImpl);
		when(hsearchServiceSpy.getFacadeFactory()).thenReturn(facadeFactoryMock);
		when(facadeFactoryMock.createFullTextSession(eq(sessionFactoryMock))).thenReturn(fullTextSessionMock);

		hsearchServiceSpy.newIndexRebuild(sessionFactoryMock, Collections.EMPTY_SET);

		verify(fullTextSessionMock, only()).createIndexerStartAndWait(any());
	}

	@Test
	public void testSearch() throws ClassNotFoundException {
		String defaultField = "defaultField";
		String analyzer = "org.apache.lucene.analysis.WhitespaceAnalyzer";
		String request = "string_to_search";
		IFacadeFactory facadeFactoryMock = mock(IFacadeFactory.class);
		ISessionFactory sessionFactoryMock = mock(ISessionFactory.class);
		IFullTextSession fullTextSessionMock = mock(IFullTextSession.class);
		IQueryParser queryParserMock = mock(IQueryParser.class);
		ILuceneQuery luceneQueryMock = mock(ILuceneQuery.class);
		IQuery queryMock = mock(IQuery.class);

		AbstractHSearchService hsearchServiceSpy = spy(serviceImpl);

		when(hsearchServiceSpy.getFacadeFactory()).thenReturn(facadeFactoryMock);
		when(facadeFactoryMock.createFullTextSession(eq(sessionFactoryMock))).thenReturn(fullTextSessionMock);
		when(facadeFactoryMock.createQueryParser(eq(defaultField), any())).thenReturn(queryParserMock);
		when(queryParserMock.parse(eq(request))).thenReturn(luceneQueryMock);
		when(fullTextSessionMock.createFullTextQuery(eq(luceneQueryMock), any())).thenReturn(queryMock);

		hsearchServiceSpy.search(sessionFactoryMock, Class.forName("java.lang.Object"), defaultField, analyzer, request);

		verify(queryMock, only()).list();
	}

	@Test
	public void testGetEntityDocuments() throws ClassNotFoundException {
		IFacadeFactory facadeFactoryMock = mock(IFacadeFactory.class);
		ISessionFactory sessionFactoryMock = mock(ISessionFactory.class);
		IFullTextSession fullTextSessionMock = mock(IFullTextSession.class);
		ISearchFactory searchFactoryMock = mock(ISearchFactory.class);
		IIndexReader indexReaderMock = mock(IIndexReader.class);
		IDocument documentMock = mock(IDocument.class);
		IField fieldMock1 = mock(IField.class);
		IField fieldMock2 = mock(IField.class);

		AbstractHSearchService hsearchServiceSpy = spy(serviceImpl);
		when(hsearchServiceSpy.getFacadeFactory()).thenReturn(facadeFactoryMock);
		when(facadeFactoryMock.createFullTextSession(eq(sessionFactoryMock))).thenReturn(fullTextSessionMock);
		when(fullTextSessionMock.getSearchFactory()).thenReturn(searchFactoryMock);
		when(searchFactoryMock.getIndexReader(any())).thenReturn(indexReaderMock);
		when(indexReaderMock.numDocs()).thenReturn(1);
		when(indexReaderMock.document(eq(0))).thenReturn(documentMock);
		when(documentMock.getFields()).thenReturn(Arrays.asList(fieldMock1, fieldMock2));
		when(fieldMock1.name()).thenReturn("name1");
		when(fieldMock1.stringValue()).thenReturn("value1");
		when(fieldMock2.name()).thenReturn("name2");
		when(fieldMock2.stringValue()).thenReturn("value2");

		List<Map<String, String>> results = hsearchServiceSpy.getEntityDocuments(sessionFactoryMock, Class.forName("java.lang.Object"));
		assertEquals(1, results.size());
		assertEquals(new TreeMap<String, String>() {{ put("name1", "value1"); put("name2", "value2"); }}, results.get(0));

	}

	private static void importProject() {
		try {
			IProject project = ResourcesUtils.importProject(PLUGIN_ID, PROJECT_PATH);
			TestUtil._waitForValidation(project);
		} catch (InvocationTargetException | IOException | CoreException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
