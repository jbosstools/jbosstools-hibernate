package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.util.Collections;
import java.util.List;

import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.query.QueryExporter;
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
	public void before() {
		queryExporterTarget = new QueryExporter();
		queryExporterFacade = new AbstractQueryExporterFacade(FACADE_FACTORY, queryExporterTarget) {};
	}
	
	@Test
	public void testSetQueries() {
		List<String> queries = Collections.emptyList();
		assertNotSame(queries, queryExporterTarget.getProperties().get(ExporterConstants.QUERY_LIST));
		queryExporterFacade.setQueries(queries);
		assertSame(queries, queryExporterTarget.getProperties().get(ExporterConstants.QUERY_LIST));
	}
	
}
