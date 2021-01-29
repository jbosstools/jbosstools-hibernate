package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.jboss.tools.hibernate.runtime.common.AbstractHQLCompletionProposalFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IHQLCompletionProposal;
import org.junit.Before;
import org.junit.Test;

public class HQLCompletionProposalFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();

	private IHQLCompletionProposal hqlCompletionProposalFacade = null; 
	private HQLCompletionProposal hqlCompletionProposalTarget = null;
	
	@Before
	public void before() {
		hqlCompletionProposalTarget = new HQLCompletionProposal(HQLCompletionProposal.PROPERTY, Integer.MAX_VALUE);
		hqlCompletionProposalFacade = new AbstractHQLCompletionProposalFacade(FACADE_FACTORY, hqlCompletionProposalTarget) {};
	}
	
	@Test
	public void testGetCompletion() {
		assertNotEquals("foo", hqlCompletionProposalFacade.getCompletion());
		hqlCompletionProposalTarget.setCompletion("foo");
		assertEquals("foo", hqlCompletionProposalFacade.getCompletion());
	}
	
	@Test
	public void testGetReplaceStart() {
		assertNotEquals(Integer.MAX_VALUE, hqlCompletionProposalFacade.getReplaceStart());
		hqlCompletionProposalTarget.setReplaceStart(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, hqlCompletionProposalFacade.getReplaceStart());
	}
	
	@Test
	public void testGetReplaceEnd() {
		assertNotEquals(Integer.MIN_VALUE, hqlCompletionProposalFacade.getReplaceEnd());
		hqlCompletionProposalTarget.setReplaceEnd(Integer.MIN_VALUE);
		assertEquals(Integer.MIN_VALUE, hqlCompletionProposalFacade.getReplaceEnd());
	}
	
	@Test
	public void testGetSimpleName() {
		assertNotEquals("foo", hqlCompletionProposalFacade.getSimpleName());
		hqlCompletionProposalTarget.setSimpleName("foo");
		assertEquals("foo", hqlCompletionProposalFacade.getSimpleName());
	}
	
}
