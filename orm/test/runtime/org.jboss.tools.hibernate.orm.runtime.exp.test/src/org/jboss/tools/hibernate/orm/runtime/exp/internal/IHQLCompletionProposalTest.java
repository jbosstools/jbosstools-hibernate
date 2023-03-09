package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.NewFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IHQLCompletionProposal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IHQLCompletionProposalTest {
	
	private IHQLCompletionProposal hqlCompletionProposalFacade = null;
	private HQLCompletionProposal hqlCompletionProposalTarget = null;
	
	@BeforeEach
	public void beforeEach() {
		hqlCompletionProposalTarget = new HQLCompletionProposal(0, 0);
		hqlCompletionProposalFacade = 
				NewFacadeFactory.INSTANCE.createHQLCompletionProposal(hqlCompletionProposalTarget);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(hqlCompletionProposalTarget);
		assertNotNull(hqlCompletionProposalFacade);
		assertSame(hqlCompletionProposalTarget, ((IFacade)hqlCompletionProposalFacade).getTarget());
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
