package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.hibernate.query.Query;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

//TODO JBIDE-28326: Investigate, reimplement and reenable these tests
@Disabled
public class QueryFacadeTest {
	
	private final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private Query<Object> queryTarget = null;
	private IQuery queryFacade = null;
	
	@BeforeEach
	public void beforeEach() {
//		queryTarget = new TestQuery<Object>(null);
//		queryFacade = new QueryFacadeImpl(FACADE_FACTORY, queryTarget) {};
	}
	
	@Test
	public void testList() {
	}
	
	@Test
	public void testSetMaxResults() {
	}
	
	@Test
	public void testSetParameterList() {
	}
	
	@Test
	public void testSetParameter() {
	}
	
	@Test
	public void testGetReturnAliases() {
		// TODO JBIDE-27532: Review the Query Page Viewer as the used APIs have completely changed
 		assertEquals(0, queryFacade.getReturnAliases().length);
	}
	
	@Test
	public void testGetReturnTypes() {
		// TODO JBIDE-27532: Review the Query Page Viewer as the used APIs have completely changed
		assertEquals(0, queryFacade.getReturnTypes().length);
	}
	
}
