package org.jboss.tools.hibernate.orm.runtime.v_6_2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Collections;
import java.util.List;

import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.query.QueryExporter;
import org.hibernate.tool.orm.jbt.wrp.QueryExporterWrapperFactory;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;
import org.jboss.tools.hibernate.orm.runtime.common.GenericFacadeFactory;
import org.jboss.tools.hibernate.orm.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IQueryExporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IQueryExporterTest {
	
	private QueryExporter queryExporterTarget = null;
	private IQueryExporter queryExporterFacade = null;
	
	@BeforeEach
	public void beforeEach() {
		queryExporterFacade = (IQueryExporter)GenericFacadeFactory.createFacade(
				IQueryExporter.class, 
				QueryExporterWrapperFactory.create(new QueryExporter()));
		Object queryExporterWrapper = ((IFacade)queryExporterFacade).getTarget();
		queryExporterTarget = (QueryExporter)((Wrapper)queryExporterWrapper).getWrappedObject();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(queryExporterFacade);
		assertNotNull(queryExporterTarget);
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
