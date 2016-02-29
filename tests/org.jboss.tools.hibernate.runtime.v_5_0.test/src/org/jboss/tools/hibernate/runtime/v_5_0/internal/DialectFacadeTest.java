package org.jboss.tools.hibernate.runtime.v_5_0.internal;

import org.hibernate.dialect.Dialect;
import org.jboss.tools.hibernate.runtime.common.AbstractDialectFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IDialect;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class DialectFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private IDialect dialectFacade = null; 
	private Dialect dialect = null;
	
	private String methodName = null;
	
	@Before
	public void setUp() throws Exception {
		dialect = new TestDialect();
		dialectFacade = new AbstractDialectFacade(FACADE_FACTORY, dialect) {};
	}
	
	@Test
	public void testOpenQuote() {
		Assert.assertEquals('o', dialectFacade.openQuote());
		Assert.assertEquals("openQuote", methodName);
	}
	
	@Test
	public void testCloseQuote()  {
		Assert.assertEquals('c', dialectFacade.closeQuote());
		Assert.assertEquals("closeQuote", methodName);
	}
	
	private class TestDialect extends Dialect {
		@Override public char openQuote() { 
			methodName = "openQuote";
			return 'o'; }
		@Override public char closeQuote() { 
			methodName = "closeQuote";
			return 'c'; }
	}
	
}
