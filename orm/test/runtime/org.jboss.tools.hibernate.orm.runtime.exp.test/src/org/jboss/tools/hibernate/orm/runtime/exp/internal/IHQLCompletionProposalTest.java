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
	
}
