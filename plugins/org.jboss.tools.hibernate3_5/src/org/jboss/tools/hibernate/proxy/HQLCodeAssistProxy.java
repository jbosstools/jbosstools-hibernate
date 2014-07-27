package org.jboss.tools.hibernate.proxy;

import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.jboss.tools.hibernate.spi.IHQLCodeAssist;
import org.jboss.tools.hibernate.spi.IHQLCompletionRequestor;

public class HQLCodeAssistProxy implements IHQLCodeAssist {
	
	HQLCodeAssist target = null;

	public HQLCodeAssistProxy(HQLCodeAssist hqlCodeAssist) {
		target = hqlCodeAssist;
	}

	@Override
	public void codeComplete(String query, int currentOffset,
			IHQLCompletionRequestor requestor) {
		assert requestor instanceof org.hibernate.tool.ide.completion.IHQLCompletionRequestor;
		target.codeComplete(query, currentOffset, (org.hibernate.tool.ide.completion.IHQLCompletionRequestor)requestor);
	}

}
