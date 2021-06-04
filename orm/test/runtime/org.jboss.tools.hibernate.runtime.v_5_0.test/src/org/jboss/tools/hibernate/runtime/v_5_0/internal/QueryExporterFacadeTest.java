package org.jboss.tools.hibernate.runtime.v_5_0.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import org.hibernate.tool.hbm2x.QueryExporter;
import org.jboss.tools.hibernate.runtime.common.AbstractQueryExporterFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IQueryExporter;
import org.junit.Before;
import org.junit.Test;

public class QueryExporterFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private QueryExporter queryExporterTarget = null;
	private IQueryExporter queryExporterFacade = null;

	@Before
	public void beforeEach() {
		queryExporterTarget = new QueryExporter();
		queryExporterFacade = new AbstractQueryExporterFacade(FACADE_FACTORY, queryExporterTarget) {};
	}
	
	@Test
	public void testSetQueries() throws Exception {
		Field queryStringsField = QueryExporter.class.getDeclaredField("queryStrings");
		queryStringsField.setAccessible(true);
		List<String> queries = Collections.emptyList();
		assertNotSame(queries, queryStringsField.get(queryExporterTarget));
		queryExporterFacade.setQueries(queries);
		assertSame(queries, queryStringsField.get(queryExporterTarget));
	}
	
	@Test
	public void testSetFileName() throws Exception {
		Field fileNameField = QueryExporter.class.getDeclaredField("filename");
		fileNameField.setAccessible(true);
		assertNotEquals("foo", fileNameField.get(queryExporterTarget));
		queryExporterFacade.setFilename("foo");
		assertEquals("foo", fileNameField.get(queryExporterTarget));
	}
	
}
