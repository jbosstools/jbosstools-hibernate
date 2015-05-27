package org.jboss.tools.hibernate.proxy;

import java.lang.reflect.Proxy;

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
				createIHQLCompletionRequestor(handler));
	}
	
	private IHQLCompletionRequestor createIHQLCompletionRequestor(IHQLCompletionHandler handler) {
		return (IHQLCompletionRequestor)Proxy.newProxyInstance(
				getFacadeFactoryClassLoader(), 
				new Class[] { IHQLCompletionRequestor.class }, 
				new HQLCompletionRequestorInvocationHandler(handler));
	}
	
}
