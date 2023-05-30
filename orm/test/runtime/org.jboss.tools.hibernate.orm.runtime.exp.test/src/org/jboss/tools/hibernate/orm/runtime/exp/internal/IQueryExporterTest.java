package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.tool.internal.export.query.QueryExporter;
import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.GenericFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IQueryExporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IQueryExporterTest {
	
	private QueryExporter queryExporterTarget = null;
	private IQueryExporter queryExporterFacade = null;
	
	@BeforeEach
	public void beforeEach() {
		queryExporterFacade = (IQueryExporter)GenericFacadeFactory.createFacade(IQueryExporter.class, WrapperFactory.createQueryExporterWrapper());
		queryExporterTarget = (QueryExporter)((IFacade)queryExporterFacade).getTarget();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(queryExporterFacade);
		assertNotNull(queryExporterTarget);
	}

}
