package org.jboss.tools.hibernate.proxy;

import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.hibernate.tool.ide.completion.IHQLCompletionRequestor;
import org.jboss.tools.hibernate.runtime.common.AbstractHQLCodeAssistFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IHQLCompletionHandler;

public class HQLCodeAssistProxy extends AbstractHQLCodeAssistFacade {
	
	HQLCodeAssist target = null;

	public HQLCodeAssistProxy(
			IFacadeFactory facadeFactory,
			HQLCodeAssist hqlCodeAssist) {
		super(facadeFactory, hqlCodeAssist);
		target = hqlCodeAssist;
	}

	@Override
	public void codeComplete(String query, int currentOffset,
			IHQLCompletionHandler handler) {
		target.codeComplete(query, currentOffset, new HQLCompletionRequestor(handler));
	}
	
	private class HQLCompletionRequestor implements IHQLCompletionRequestor {		
		private IHQLCompletionHandler handler = null;		
		public HQLCompletionRequestor(IHQLCompletionHandler handler) {
			this.handler = handler;
		}
		@Override
		public boolean accept(HQLCompletionProposal proposal) {
			return handler.accept(new HQLCompletionProposalProxy(proposal));
		}
		@Override
		public void completionFailure(String errorMessage) {
			handler.completionFailure(errorMessage);			
		}		
	}

}
