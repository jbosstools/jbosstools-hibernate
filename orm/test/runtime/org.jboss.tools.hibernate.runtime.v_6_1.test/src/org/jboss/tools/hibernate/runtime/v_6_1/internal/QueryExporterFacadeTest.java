package org.jboss.tools.hibernate.runtime.v_6_1.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Collections;
import java.util.List;

import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.query.QueryExporter;
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
		queryExporterFacade = new QueryExporterFacadeImpl(FACADE_FACTORY, queryExporterTarget);
	}
	
	@Test
	public void testSetQueries() {
		List<String> queries = Collections.emptyList();
		assertNotSame(queries, queryExporterTarget.getProperties().get(ExporterConstants.QUERY_LIST));
		queryExporterFacade.setQueries(queries);
		assertSame(queries, queryExporterTarget.getProperties().get(ExporterConstants.QUERY_LIST));
	}
	
	@Test
	public void testSetFileName() {
		assertNotEquals("foo", queryExporterTarget.getProperties().get(ExporterConstants.OUTPUT_FILE_NAME));
		queryExporterFacade.setFilename("foo");
		assertEquals("foo", queryExporterTarget.getProperties().get(ExporterConstants.OUTPUT_FILE_NAME));
	}
	
}
