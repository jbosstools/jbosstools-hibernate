package org.jboss.tools.hibernate.runtime.v_5_1.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import org.hibernate.tool.hbm2x.QueryExporter;
import org.jboss.tools.hibernate.runtime.common.AbstractQueryExporterFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IQueryExporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class QueryExporterFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private QueryExporter queryExporterTarget = null;
	private IQueryExporter queryExporterFacade = null;

	@BeforeEach
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
