package org.jboss.tools.hibernate.proxy;

import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.hibernate.tool.ide.completion.IHQLCompletionRequestor;
import org.jboss.tools.hibernate.runtime.common.AbstractHQLCodeAssistFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IHQLCompletionHandler;

public class HQLCodeAssistProxy extends AbstractHQLCodeAssistFacade {
	
	public HQLCodeAssistProxy(
			IFacadeFactory facadeFactory,
			HQLCodeAssist hqlCodeAssist) {
		super(facadeFactory, hqlCodeAssist);
	}

	public HQLCodeAssist getTarget() {
		return (HQLCodeAssist)super.getTarget();
	}

	@Override
	public void codeComplete(String query, int currentOffset,
			IHQLCompletionHandler handler) {
		getTarget().codeComplete(
				query, 
				currentOffset, 
				(IHQLCompletionRequestor)createIHQLCompletionRequestor(handler));
	}
	
}
