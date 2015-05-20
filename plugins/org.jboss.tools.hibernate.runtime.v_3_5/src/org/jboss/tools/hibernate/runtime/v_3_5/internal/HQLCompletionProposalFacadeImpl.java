package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.jboss.tools.hibernate.runtime.common.AbstractHQLCompletionProposalFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class HQLCompletionProposalFacadeImpl 
extends AbstractHQLCompletionProposalFacade {
	
	public HQLCompletionProposalFacadeImpl(
			IFacadeFactory facadeFactory,
			HQLCompletionProposal proposal) {
		super(facadeFactory, proposal);
	}
	
}
